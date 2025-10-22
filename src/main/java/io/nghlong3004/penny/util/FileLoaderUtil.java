package io.nghlong3004.penny.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public final class FileLoaderUtil {

    public static String loadFile(String filePath) {
        try (InputStream is = FileLoaderUtil.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new RuntimeException("File" + filePath + " not found!");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (Exception e) {
            throw new RuntimeException("UNREAD file " + filePath, e);
        }
    }

    private FileLoaderUtil() {
    }

}
