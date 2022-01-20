package com.danifoldi.protogui.util;

import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.electronwill.nightconfig.core.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public enum Message {

    PLAYER_ONLY("playerOnly", "&cOnly players can execute this command"),
    TARGET_REQUIRED("targetRequired", "&cThis command requires a target player"),
    NO_SELF_TARGET("noSelfTarget", "&cYou can't target yourself with this command"),
    SERVER_DISABLED("serverDisabled", "&cYou can't use this command on this server"),
    TARGET_BYPASS("targetBypass", "&cThis player can't be targeted with this command"),
    TARGET_NOT_FOUND("targetNotFound", "&cTarget {target} could not be found"),
    RELOAD_SUCCESS("reloadSuccess", "&aPlugin reloaded successfully in &l{time}ms"),
    COMMAND_HELP("commandHelp", "&0------------ &e&l%bungeegui% commands &0------------"),
    COMMAND_RELOAD("commandReload", "&0- &6/bgui &lreload&r &0- &7Reload the plugin"),
    COMMAND_GUIS("commandGuis", "&0- &6/bgui &lguis&r &0- &7List the loaded GUIs"),
    COMMAND_BROADCAST("commandBroadcast", "&0- &6/bgui &lbroadcast&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<message> &0- &7Send a message to one or more players"),
    COMMAND_LOG("commandLog", "&0- &6/bgui &llog&r &6<message> &0- &7Log a message into the console"),
    COMMAND_SEND("commandSend", "&0- &6/bgui &lsend&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<server> &0- &7Send a player to a server"),
    COMMAND_CHAT("commandChat", "&0- &6/bgui &lchat&r &6<player> &6<message> &0- &7Send a message to chat as a player"),
    COMMAND_ACTIONBAR("commandActionbar", "&0- &6/bgui &lactionbar&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<text> &0- &7Show a player a message in their action bar"),
    COMMAND_TITLE("commandTitle", "&0- &6/bgui &ltitle&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<fadeIn> &6<stay> &6<fadeOut> &6<message> &0- &7Send title to players"),
    COMMAND_SOUND("commandSound", "&0- &6/bgui &lsound&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<sound> &6[category] &6[volume] &6[pitch] &0- &7Play a sound for players"),
    COMMAND_OPEN("commandOpen", "&0- &6/bgui &lopen&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<gui> &6[target] &0- &7Open a GUI for players"),
    COMMAND_CLOSE("commandClose", "&0- &6/bgui &lclose&r &6<player> &0- &7Close the GUI for players"),
    ACTION_COMPLETE("actionComplete", "&aAction completed for {count} players"),
    GUI_NOT_FOUND("guiNotFound", "&cGUI {name} not found"),
    GUI_TARGET_REQUIRED("guiTargetRequired", "&cThis GUI requires a target player"),
    INVALID_PROPERTY("invalidProperty", "&cA property is invalid"),
    SERVER_NOT_FOUND("serverNotFound", "&cServer {name} not found"),
    EMPTY_MESSAGE("emptyMessage", "&cMessage can't be empty"),
    NO_PERMISSION("noPermission", "&cYou don't have permission to execute that command"),
    GUI_LIST_TOP("guiListTop", "&a{count} GUIs are loaded:"),
    GUI_LIST_ITEM("guiListItem", "&0- &6{name}"),
    PLAYER_NOT_FOUND("playerNotFound", "&cPlayer {player} could not be found"),
    CONDITION_FAILED("conditionFailed", "&cCondition {id} failed"),
    DISPATCHER_AUTHORIZATION_ERROR("noPermission", "&cYou are missing permission {permission}"),
    DISPATCHER_FAILED_TO_EXECUTE_COMMAND("dispatcherFailedToExecuteCommand", "&cFailed to execute command {commandline}"),
    DISPATCHER_NO_SUCH_COMMAND("dispatcherNoSuchCommand", "&cCommand {name} not found"),
    DISPATCHER_TOO_FEW_ARGUMENTS("dispatcherTooFewArguments", "&cToo few arguments, syntax: {syntax}"),
    DISPATCHER_TOO_MANY_ARGUMENTS("dispatcherTooManyArguments", "&cToo many arguments, syntax: {syntax}"),
    DISPATCHER_ILLEGAL_COMMAND_SOURCE("dispatcherIllegalCommandSource", "&cInvalid source, found {found} instead of {required}"),
    DISPATCHER_INVALID_BOOLEAN_VALUE("dispatcherInvalidBooleanValue", "&cInvalid boolean: {input}"),
    DISPATCHER_INVALID_NUMBER_VALUE("dispatcherInvalidNumberValue", "&cInvalid number: {input}"),
    DISPATCHER_INVALID_CHARACTER_VALUE("dispatcherInvalidCharacterValue", "&cInvalid character: {input}"),
    DISPATCHER_NUMBER_OUT_OF_RANGE("dispatcherNumberOutOfRange", "&cNumber {input} is out of range {min} - {max}"),
    PARAMETER_QUOTED_STRING_INVALID_TRAILING_CHARACTER("parameterQuotedStringInvalidTrailingCharacter", "&cQuoted string {input} has invalid trailing character"),
    PARAMETER_STRING_REGEX_ERROR("parameterStringRegexError", "&cString {input} does not match pattern {regex}"),
    PARAMETER_MISSING_FLAG_VALUE("parameterMissingFlagValue", "&cMissing flag value: {input}"),
    PARAMETER_MISSING_FLAG("parameterMissingFlag", "&cMissing flag: {syntax}"),
    PARAMETER_DUPLICATE_FLAG("parameterDuplicateFlag", "&cDuplicate flag: {flag}"),
    PARAMETER_UNRECOGNIZED_COMMAND_FLAG("parameterUnrecognizedCommandFlag", "&cUnrecognized flag: {input}"),
    COMMAND_SUBTITLE("commandSubtitle", "&0- &6/bgui &lsubtitle&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<fadeIn> &6<stay> &6<fadeOut> &6<message> &0- &7Send subtitle to players"),
    COMMAND_SUDO("commandSudo", "&0- &6/bgui &lsudo&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<command> &0- &7Force players to execute a command");


    private static final @NotNull Map<String, String> messages = new HashMap<>();
    public static void setMessageProvider(Config config) {
        final @NotNull Map<String, String> messages = new HashMap<>();
        for (final @NotNull Config.Entry message: config.entrySet()) {
            messages.put(message.getKey(), message.getValue());
        }

        Message.messages.clear();
        Message.messages.putAll(messages);
    }

    private static final @NotNull Pattern HEX = Pattern.compile("(&#[a-fA-F0-9]{6})");
    private static final @NotNull Pattern COLOR = Pattern.compile("(&(?<char>[0-9a-fA-Fk-oK-OrRxX]))");

    private final @NotNull String messageId;
    private final @NotNull String defaultValue;

    Message(final @NotNull String messageId, final @NotNull String defaultValue) {
        this.messageId = messageId;
        this.defaultValue = defaultValue;
    }

    public static @Nullable Message find(String key) {
        try {
            return Message.valueOf(key.toUpperCase(Locale.ROOT).replace("-", "_").replace(".", "_").trim());
        } catch (EnumConstantNotPresentException e) {
            return null;
        }
    }

    public @NotNull String getDefaultValue() {
        return defaultValue;
    }

    public @NotNull String value() {
        return Message.messages.get(messageId);
    }

    @SafeVarargs
    public static @NotNull String replace(@NotNull String text,final @NotNull Pair<String, String>... replacements) {
        for (Pair<String, String> replacement: replacements) {
            if (replacement.getFirst() == null || replacement.getSecond() == null) {
                continue;
            }
            text = text.replace("{" + replacement.getFirst() + "}", replacement.getSecond());
        }

        return text;
    }

    public static @NotNull String colorCodes(String text) {
        final @NotNull Matcher hexMatcher = HEX.matcher(text);

        while (hexMatcher.find()) {
            final @NotNull String color = hexMatcher.group(1);
            final @NotNull String value = "§x" + color.replace("&#", "")
                    .chars()
                    .mapToObj(i -> (char)i)
                    .map(String::valueOf)
                    .map(s -> "§" + s)
                    .collect(Collectors.joining());
            text = text.replace(color, value);
        }

        final @NotNull Matcher colorMatcher = COLOR.matcher(text);

        while (colorMatcher.find()) {
            final @NotNull String color = colorMatcher.group(1);
            final @NotNull String value = colorMatcher.group("char");
            text = text.replace(color, "§" + value);
        }

        return text;
    }

    @SafeVarargs
    public final void send(final @NotNull PlatformInteraction.ProtoSender sender, final @NotNull Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId).equals("")) {
            send(sender, defaultValue, replacements);
        }

        send(sender, Message.messages.get(messageId), replacements);
    }

    @SafeVarargs
    public static void send(final @NotNull PlatformInteraction.ProtoSender sender, final @NotNull String value, final @NotNull Pair<String, String>... replacements) {
        sender.send(process(sender, value, replacements));
    }

    @SafeVarargs
    public final @NotNull String process(final @Nullable PlatformInteraction.ProtoSender sender, final @NotNull Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId) == null || Message.messages.get(messageId).equals("")) {
            process(sender, defaultValue, replacements);
        }

        return process(sender, value(), replacements);
    }

    @SafeVarargs
    public static @NotNull String process(final @Nullable PlatformInteraction.ProtoSender sender, final @NotNull String value, final @NotNull Pair<String, String>... replacements) {
        return colorCodes(ProtoGuiAPI.getInstance().parsePlaceholders(sender != null ? sender.uniqueId() : null, replace(value, replacements)));
    }
}
