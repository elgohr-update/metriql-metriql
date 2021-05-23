package com.metriql.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceUtil
{
    public synchronized static final String getResourceFile(Class clazz, String path)
            throws IOException
    {
        URL url = Resources.getResource(clazz, path);
        return Resources.toString(url, Charsets.UTF_8);
    }

    public synchronized static final String getResourceFile(String path)
            throws IOException
    {
        URL url = Resources.getResource(path);
        return Resources.toString(url, Charsets.UTF_8);
    }

    public synchronized static final List<String> getResourceFiles(String path)
            throws IOException
    {
        FileSystem fileSystem = null;

        try {
            URI uri = ResourceUtil.class.getResource("/" + path).toURI();

            java.nio.file.Path myPath;
            try {
                if (uri.getScheme().equals("jar")) {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    myPath = fileSystem.getPath("/" + path);
                }
                else {
                    myPath = Paths.get(uri);
                }
                return Files.walk(myPath, 1).flatMap(next -> {
                    if (next.equals(myPath)) {
                        return Stream.of();
                    }
                    return Stream.of(CharMatcher.is('/').trimFrom(next.getFileName().toString()));
                }).collect(Collectors.toList());
            }
            finally {
                if (fileSystem != null) {
                    fileSystem.close();
                }
            }
        }
        catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
        finally {
            if (fileSystem != null) {
                fileSystem.close();
            }
        }
    }
}
