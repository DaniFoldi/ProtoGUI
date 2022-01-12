package com.danifoldi.protogui.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class ConfigUtil {
    public static final int LATEST = 6;

    public static @NotNull String backupAndUpgrade(final @NotNull Path datafolder, int oldVersion) throws IOException {
        final @NotNull Path backup = getBackupFolder(datafolder.getParent(), oldVersion);

        Files.copy(datafolder, backup);

        ConfigUtil.upgrade(datafolder, oldVersion, LATEST);

        return backup.getFileName().toString();
    }

    private static void upgrade(Path datafolder, int oldVersion, int newVersion) {
        FileConfig config = FileConfig.of(datafolder.resolve("config.yml"));
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

        if (oldVersion <= 3 && newVersion >= 4) {
            ensureValue(config, "actions", Collections.emptyList());
        }

        if (oldVersion <= 4 && newVersion >= 5) {
            ensureValue(config, "logLevel", config.getOrElse("debugLevel", "ALL"));
            config.remove("debugLevel");
        }

        if (oldVersion <= 5 && newVersion >= 6) {
            FileConfig messages = FileConfig.of(datafolder.resolve("messages.yml"));
            ensureValue(messages, "commandHelp", config.getOrElse("messages.commandHelp", Message.COMMAND_HELP.getDefaultValue()));
            ensureValue(messages, "commandReload", config.getOrElse("messages.commandReload", Message.COMMAND_RELOAD.getDefaultValue()));
            ensureValue(messages, "commandGuis", config.getOrElse("messages.commandGuis", Message.COMMAND_GUIS.getDefaultValue()));
            ensureValue(messages, "commandBroadcast", config.getOrElse("messages.commandBroadcast", Message.COMMAND_BROADCAST.getDefaultValue()));
            ensureValue(messages, "commandLog", config.getOrElse("messages.commandLog", Message.COMMAND_LOG.getDefaultValue()));
            ensureValue(messages, "commandSend", config.getOrElse("messages.commandSend", Message.COMMAND_SEND.getDefaultValue()));
            ensureValue(messages, "commandChat", config.getOrElse("messages.commandChat", Message.COMMAND_CHAT.getDefaultValue()));
            ensureValue(messages, "commandActionbar", config.getOrElse("messages.commandActionbar", Message.COMMAND_ACTIONBAR.getDefaultValue()));
            ensureValue(messages, "commandTitle", config.getOrElse("messages.commandTitle", Message.COMMAND_TITLE.getDefaultValue()));
            ensureValue(messages, "commandSound", config.getOrElse("messages.commandSound", Message.COMMAND_SOUND.getDefaultValue()));
            ensureValue(messages, "commandOpen", config.getOrElse("messages.commandOpen", Message.COMMAND_OPEN.getDefaultValue()));
            ensureValue(messages, "commandClose", config.getOrElse("messages.commandClose", Message.COMMAND_CLOSE.getDefaultValue()));
            ensureValue(messages, "guiListTop", config.getOrElse("messages.guiListTop", Message.GUI_LIST_TOP.getDefaultValue()));
            ensureValue(messages, "guiListItem", config.getOrElse("messages.guiListItem", Message.GUI_LIST_ITEM.getDefaultValue()));
            ensureValue(messages, "guiNotFound", config.getOrElse("messages.guiNotFound", Message.GUI_NOT_FOUND.getDefaultValue()));
            ensureValue(messages, "guiTargetRequired", config.getOrElse("messages.guiTargetRequired", Message.GUI_TARGET_REQUIRED.getDefaultValue()));
            ensureValue(messages, "invalidProperty", config.getOrElse("messages.invalidProperty", Message.INVALID_PROPERTY.getDefaultValue()));
            ensureValue(messages, "serverNotFound", config.getOrElse("messages.serverNotFound", Message.SERVER_NOT_FOUND.getDefaultValue()));
            ensureValue(messages, "emptyMessage", config.getOrElse("messages.emptyMessage", Message.EMPTY_MESSAGE.getDefaultValue()));
            ensureValue(messages, "noPermission", config.getOrElse("messages.noPermission", Message.NO_PERMISSION.getDefaultValue()));
            ensureValue(messages, "targetRequired", config.getOrElse("messages.targetRequired", Message.TARGET_REQUIRED.getDefaultValue()));
            ensureValue(messages, "conditionFailed", Message.CONDITION_FAILED.getDefaultValue());
            ensureValue(messages, "noPermission", Message.DISPATCHER_AUTHORIZATION_ERROR.getDefaultValue());
            ensureValue(messages, "dispatcherFailedToExecuteCommand", Message.DISPATCHER_FAILED_TO_EXECUTE_COMMAND.getDefaultValue());
            ensureValue(messages, "dispatcherNoSuchCommand", Message.DISPATCHER_NO_SUCH_COMMAND.getDefaultValue());
            ensureValue(messages, "dispatcherTooFewArguments", Message.DISPATCHER_TOO_FEW_ARGUMENTS.getDefaultValue());
            ensureValue(messages, "dispatcherTooManyArguments", Message.DISPATCHER_TOO_MANY_ARGUMENTS.getDefaultValue());
            ensureValue(messages, "dispatcherIllegalCommandSource", Message.DISPATCHER_ILLEGAL_COMMAND_SOURCE.getDefaultValue());
            ensureValue(messages, "dispatcherInvalidBooleanValue", Message.DISPATCHER_INVALID_BOOLEAN_VALUE.getDefaultValue());
            ensureValue(messages, "dispatcherInvalidNumberValue", Message.DISPATCHER_INVALID_NUMBER_VALUE.getDefaultValue());
            ensureValue(messages, "dispatcherInvalidCharacterValue", Message.DISPATCHER_INVALID_CHARACTER_VALUE.getDefaultValue());
            ensureValue(messages, "dispatcherNumberOutOfRange", Message.DISPATCHER_NUMBER_OUT_OF_RANGE.getDefaultValue());
            ensureValue(messages, "parameterQuotedStringInvalidTrailingCharacter", Message.PARAMETER_QUOTED_STRING_INVALID_TRAILING_CHARACTER.getDefaultValue());
            ensureValue(messages, "parameterStringRegexError", Message.PARAMETER_STRING_REGEX_ERROR.getDefaultValue());
            ensureValue(messages, "parameterMissingFlagValue", Message.PARAMETER_MISSING_FLAG_VALUE.getDefaultValue());
            ensureValue(messages, "parameterMissingFlag", Message.PARAMETER_MISSING_FLAG.getDefaultValue());
            ensureValue(messages, "parameterDuplicateFlag", Message.PARAMETER_DUPLICATE_FLAG.getDefaultValue());
            ensureValue(messages, "parameterUnrecognizedCommandFlag", Message.PARAMETER_UNRECOGNIZED_COMMAND_FLAG.getDefaultValue());

            config.remove("messages");
            Config actions = config.get("actions");
            if (actions != null) {
                for (Config.Entry action : actions.entrySet()) {
                    FileConfig actionFile = FileConfig.of(datafolder.resolve("actions").resolve(action.getKey() + ".yml"));
                    actionFile.putAll(((Config) action.getValue()).unmodifiable());
                    actionFile.save();
                    actionFile.close();
                }
                config.remove("actions");
            }
            Config guis = config.get("guis");
            if (guis != null) {
                for (Config.Entry gui : guis.entrySet()) {
                    FileConfig actionFile = FileConfig.of(datafolder.resolve("actions").resolve(gui.getKey() + ".yml"));
                    actionFile.putAll(((Config) gui.getValue()).unmodifiable());
                    actionFile.save();
                    actionFile.close();
                }
                config.remove("guis");
            }
        }

        config.set("configVersion", newVersion);
        config.save();
        config.close();
    }

    private static void ensureValue(final @NotNull Config config, final @NotNull String path, final @NotNull Object value) {
        if (!config.contains(path)) {
            config.add(path, value);
        }
    }

    private static @NotNull Path getBackupFolder(final @NotNull Path folder, final int oldVersion) {
        if (!Files.exists(folder.resolve("ProtoGUI_backup_%d".formatted(oldVersion)))) {
            return folder.resolve("ProtoGUI_backup_%d".formatted(oldVersion));
        }

        int backup = 1;
        while (Files.exists(folder.resolve("ProtoGUI_backup_%d_%d".formatted(oldVersion, backup))) && backup < 100) {
            backup += 1;
        }
        if (backup >= 100) {
            throw new RuntimeException("Failed to find backup location");
        }

        return folder.resolve("ProtoGUI_backup_%d_%d".formatted(oldVersion, backup));
    }

    private ConfigUtil() {
        throw new UnsupportedOperationException();
    }
}
