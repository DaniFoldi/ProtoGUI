package com.danifoldi.bungeegui.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ConfigUtil {
    public static final int LATEST = 2;

    public static String backupAndUpgrade(FileConfig config) throws IOException {
        Path folder = config.getFile().toPath().getParent();
        String backup = getBackupFilename(folder);

        Files.copy(folder.resolve(config.getFile().getName()), folder.resolve(backup));

        int currentVersion = config.getIntOrElse("configVersion", 0);
        ConfigUtil.upgrade(config, currentVersion, LATEST);

        return backup;
    }

    private static void upgrade(FileConfig config, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            return;
        }

        if (oldVersion <= 0 && newVersion >= 1) {
            ensureValue(config, "messages", Collections.emptyList());
            ensureValue(config, "guis", Collections.emptyList());

            ensureValue(config, "messages.playerOnly", Message.PLAYER_ONLY);
            ensureValue(config, "messages.targetRequired", Message.TARGET_REQUIRED);
            ensureValue(config, "messages.noSelfTarget", Message.NO_SELF_TARGET);
            ensureValue(config, "messages.serverDisabled", Message.SERVER_DISABLED);
            ensureValue(config, "messages.targetNotFound", Message.TARGET_NOT_FOUND);
            ensureValue(config, "messages.reloadSuccess", Message.RELOAD_SUCCESS);
        }

        if (oldVersion <= 1 && newVersion >= 2) {
            ensureValue(config, "messages.targetBypass", Message.TARGET_BYPASS);
        }

        config.set("configVersion", newVersion);
        config.save();
    }

    private static void ensureValue(Config config, String path, Object value) {
        if (!config.contains(path)) {
            config.add(path, value);
        }
    }

    private static String getBackupFilename(Path folder) {
        if (!Files.exists(folder.resolve("config_backup.yml"))) {
            return "config_backup.yml";
        }

        int backup = 1;
        while (Files.exists(folder.resolve("config_backup" + backup + ".yml"))) {
            backup += 1;
        }

        return "config_backup" + backup + ".yml";
    }

    private ConfigUtil() {
        throw new UnsupportedOperationException();
    }
}
