package com.danifoldi.bungeegui.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ConfigUtil {
    public static final int LATEST = 3;

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

            ensureValue(config, "messages.playerOnly", Message.PLAYER_ONLY.getDefaultValue());
            ensureValue(config, "messages.targetRequired", Message.TARGET_REQUIRED.getDefaultValue());
            ensureValue(config, "messages.noSelfTarget", Message.NO_SELF_TARGET.getDefaultValue());
            ensureValue(config, "messages.serverDisabled", Message.SERVER_DISABLED.getDefaultValue());
            ensureValue(config, "messages.targetNotFound", Message.TARGET_NOT_FOUND.getDefaultValue());
            ensureValue(config, "messages.reloadSuccess", Message.RELOAD_SUCCESS.getDefaultValue());
        }

        if (oldVersion <= 1 && newVersion >= 2) {
            ensureValue(config, "messages.targetBypass", Message.TARGET_BYPASS.getDefaultValue());
        }

        if (oldVersion <= 2 && newVersion >= 3) {
            ensureValue(config, "messages.commandHelp", Message.COMMAND_HELP.getDefaultValue());
            ensureValue(config, "messages.commandReload", Message.COMMAND_RELOAD.getDefaultValue());
            ensureValue(config, "messages.commandGuis", Message.COMMAND_GUIS.getDefaultValue());
            ensureValue(config, "messages.commandBroadcast", Message.COMMAND_BROADCAST.getDefaultValue());
            ensureValue(config, "messages.commandLog", Message.COMMAND_LOG.getDefaultValue());
            ensureValue(config, "messages.commandSend", Message.COMMAND_SEND.getDefaultValue());
            ensureValue(config, "messages.commandChat", Message.COMMAND_CHAT.getDefaultValue());
            ensureValue(config, "messages.commandActionbar", Message.COMMAND_ACTIONBAR.getDefaultValue());
            ensureValue(config, "messages.commandTitle", Message.COMMAND_TITLE.getDefaultValue());
            ensureValue(config, "messages.commandSound", Message.COMMAND_SOUND.getDefaultValue());
            ensureValue(config, "messages.commandOpen", Message.COMMAND_OPEN.getDefaultValue());
            ensureValue(config, "messages.commandClose", Message.COMMAND_CLOSE.getDefaultValue());
            ensureValue(config, "messages.guiListTop", Message.GUI_LIST_TOP.getDefaultValue());
            ensureValue(config, "messages.guiListItem", Message.GUI_LIST_ITEM.getDefaultValue());
            ensureValue(config, "messages.guiNotFound", Message.GUI_NOT_FOUND.getDefaultValue());
            ensureValue(config, "messages.guiTargetRequired", Message.GUI_TARGET_REQUIRED.getDefaultValue());
            ensureValue(config, "messages.invalidProperty", Message.INVALID_PROPERTY.getDefaultValue());
            ensureValue(config, "messages.serverNotFound", Message.SERVER_NOT_FOUND.getDefaultValue());
            ensureValue(config, "messages.emptyMessage", Message.EMPTY_MESSAGE.getDefaultValue());
            ensureValue(config, "messages.noPermission", Message.NO_PERMISSION.getDefaultValue());
            ensureValue(config, "messages.targetRequired", Message.TARGET_REQUIRED.getDefaultValue());
            ensureValue(config, "debugLevel", "ALL");
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
