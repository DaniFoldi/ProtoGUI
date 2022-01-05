package com.danifoldi.protogui.util;

import com.electronwill.nightconfig.core.file.FileConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    public static @NotNull FileConfig ensureConfigFile(final @NotNull Path folder, final @NotNull String fileName) throws IOException {
        final @NotNull Path dest = folder.resolve(fileName);
        if (Files.exists(dest)) {
            return loadConfigFile(dest);
        }

        try (final @Nullable InputStream stream = FileUtil.class.getResourceAsStream("/" + fileName)) {
            Files.copy(stream, dest);
        }

        return loadConfigFile(dest);
    }

    private static @NotNull FileConfig loadConfigFile(final @NotNull Path file) {
        final @NotNull FileConfig config = FileConfig.of(file);
        config.load();

        return config;
    }

    public static void ensureFolder(final @NotNull Path folder) throws IOException {
        if (Files.notExists(folder)) {
            Files.createDirectories(folder);
        }
    }

    private FileUtil() {
        throw new UnsupportedOperationException();
    }
}
