package com.metriql.db.mysql

import com.metriql.DockerContainer
import com.metriql.DockerContainer.HostPortProvider
import com.metriql.db.TestingServer
import com.metriql.util.MetriqlExceptions
import com.metriql.util.ValidationUtil
import com.metriql.warehouse.mysql.MySQLWarehouse
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

object TestingEnvironmentMySQL : TestingServer<Unit, Connection>() {
    private const val MYSQL_ROOT_USER = "root"
    private const val MYSQL_USER = "metriql"
    private const val MYSQL_ROOT_PASSWORD = "mysqlpassword"
    private const val MYSQL_DATABASE = "metriql"
    private const val MYSQL_PORT = 3306

    private val dockerContainer: DockerContainer by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        DockerContainer(
            "mysql:8.0.15",
            listOf(MYSQL_PORT),
            mapOf(
                "MYSQL_ROOT_PASSWORD" to MYSQL_ROOT_PASSWORD,
                "MYSQL_USER" to MYSQL_USER,
                "MYSQL_PASSWORD" to MYSQL_ROOT_PASSWORD,
                "MYSQL_DATABASE" to MYSQL_DATABASE
            )
        ) {
            runQueryAsRoot(it, "SELECT 1")
        }
    }

    override val config = MySQLWarehouse.MysqlConfig(
        "127.0.0.1",
        dockerContainer.getHostPort(MYSQL_PORT),
        MYSQL_DATABASE,
        MYSQL_USER,
        MYSQL_ROOT_PASSWORD,
        false,
        mapOf()
    )

    override fun getTableReference(tableName: String): String {
        return "${ValidationUtil.quoteIdentifier(config.database, '`')}.${ValidationUtil.quoteIdentifier(tableName, '`')}"
    }

    @Volatile
    private var isInitialized = false

    override fun createConnection(): Connection {
        return try {
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            DriverManager.getConnection(getJdbcUrl(dockerContainer::getHostPort, MYSQL_USER, MYSQL_ROOT_PASSWORD))
        } catch (e: SQLException) {
            throw MetriqlExceptions.SYSTEM_EXCEPTION_FROM_CAUSE.exceptionFromObject(e)
        }
    }

    override fun init() {
        runQueryAsRoot(dockerContainer::getHostPort, "GRANT ALL PRIVILEGES ON *.* TO '$MYSQL_USER'")

        createConnection().use { connection ->
            val stmt = connection.createStatement()
            stmt.execute("DROP DATABASE IF EXISTS `${config.database}`")
            stmt.execute("CREATE DATABASE `${config.database}`")
            isInitialized = true
        }
    }

    private fun runQueryAsRoot(hostPortProvider: HostPortProvider, query: String) {
        DriverManager.getConnection(getJdbcUrl(hostPortProvider, MYSQL_ROOT_USER, MYSQL_ROOT_PASSWORD)).use { conn -> conn.createStatement().use { stmt -> stmt.execute(query) } }
    }

    private fun getJdbcUrl(hostPortProvider: HostPortProvider, user: String, password: String): String? {
        return "jdbc:mysql://localhost:${hostPortProvider.getHostPort(MYSQL_PORT)}?user=$user&password=$password&useSSL=false&allowPublicKeyRetrieval=true"
    }

    override fun resultSetFor(query: String): ResultSet {
        val conn = createConnection()
        return conn.createStatement().executeQuery(query)
    }
}
