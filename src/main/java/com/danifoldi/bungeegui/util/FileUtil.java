package com.danifoldi.bungeegui.util;

import com.danifoldi.bungeegui.BungeeGuiPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    public static Path ensureFile(final @NotNull Path folder, final @NotNull String fileName) throws IOException {
        if (Files.notExists(folder)) {
            Files.createDirectories(folder);
        }

        final Path dest = folder.resolve(fileName);
        if (Files.exists(dest)) {
            return dest;
        }

        try (final InputStream stream = BungeeGuiPlugin.class.getResourceAsStream("/" + fileName)) {
            Files.copy(stream, dest);
        }

        return dest;
    }
}
