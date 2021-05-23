package com.metriql.db;

import io.airlift.configuration.Config;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

public class JDBCConfig {
    private String url;
    private String table;
    private String username;
    private String password = "";
    private Integer maxConnection;
    private String dataSource;
    private String host;
    private int port;
    private String database;
    private Long connectionMaxLifeTime;
    private Long connectionIdleTimeout;
    private boolean connectionDisablePool;

    @NotNull
    public String getUrl()
    {
        return url;
    }

    @Config("url")
    public JDBCConfig setUrl(String url)
            throws URISyntaxException
    {
        if (url.startsWith("jdbc:")) {
            url = url.substring(5);
        }

        URI dbUri = new URI(url);
        String userInfo = dbUri.getUserInfo();
        if (userInfo != null) {
            String[] split = userInfo.split(":");
            this.username = split[0];
            if (split.length > 1) {
                this.password = split[1];
            }
        }

        String query = Optional.ofNullable(dbUri.getQuery()).orElse("");
        this.setHost(dbUri.getHost());
        this.setPort(dbUri.getPort());
        this.setDatabase(dbUri.getPath().substring(1));

        this.url = "jdbc:" + convertScheme(dbUri.getScheme()) + ":" +
                (dbUri.getHost() != null ? "//" + dbUri.getHost() : "") +
                ((dbUri.getHost() != null && dbUri.getPort() > -1) ? (":" + dbUri.getPort()) : "")
                + dbUri.getPath()
                + (query.isEmpty() ? "" : ("?" + query));

        return this;
    }

    public String getDataSource()
    {
        return dataSource;
    }

    @Config("data-source")
    public JDBCConfig setDataSource(String dataSource)
    {
        this.dataSource = dataSource;
        return this;
    }

    public String getUsername()
    {
        return username;
    }

    @Config("username")
    public JDBCConfig setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public Integer getMaxConnection()
    {
        return maxConnection;
    }

    @Config("max-connection")
    public JDBCConfig setMaxConnection(Integer maxConnection)
    {
        this.maxConnection = maxConnection;
        return this;
    }

    public String getPassword()
    {
        return password;
    }

    @Config("password")
    public JDBCConfig setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public boolean getConnectionDisablePool()
    {
        return connectionDisablePool;
    }

    @Config("connection.disable-pool")
    public JDBCConfig setConnectionDisablePool(boolean connectionDisablePool)
    {
        this.connectionDisablePool = connectionDisablePool;
        return this;
    }

    public String getTable()
    {
        return table;
    }

    @Config("table")
    public JDBCConfig setTable(String table)
    {
        this.table = table;
        return this;
    }

    public Long getConnectionMaxLifeTime()
    {
        return connectionMaxLifeTime;
    }

    @Config("connection.max-life-time")
    public JDBCConfig setConnectionMaxLifeTime(Long connectionMaxLifeTime)
    {
        this.connectionMaxLifeTime = connectionMaxLifeTime;
        return this;
    }

    public Long getConnectionIdleTimeout()
    {
        return connectionIdleTimeout;
    }

    @Config("connection.max-idle-timeout")
    public JDBCConfig setConnectionIdleTimeout(Long connectionIdleTimeout)
    {
        this.connectionIdleTimeout = connectionIdleTimeout;
        return this;
    }

    public String convertScheme(String scheme)
    {
        switch (scheme) {
            case "postgres":
                return "postgresql";
            default:
                return scheme;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JDBCConfig)) {
            return false;
        }

        JDBCConfig that = (JDBCConfig) o;

        if (!Objects.equals(url, that.url)) {
            return false;
        }
        if (!Objects.equals(table, that.table)) {
            return false;
        }
        if (!Objects.equals(username, that.username)) {
            return false;
        }
        if (!Objects.equals(password, that.password)) {
            return false;
        }
        if (!Objects.equals(maxConnection, that.maxConnection)) {
            return false;
        }
        return !(!Objects.equals(dataSource, that.dataSource));
    }

    @Override
    public int hashCode()
    {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (maxConnection != null ? maxConnection.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return username + "@" + url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
