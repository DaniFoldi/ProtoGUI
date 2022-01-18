package com.danifoldi.protogui.util;

import com.danifoldi.protogui.platform.PlatformInteraction;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionUtil {

    private static final Pattern permissionPattern = Pattern.compile("^(?<mode>(no)?perm)<(?<node>[\\w.]+)>");
    private static final Pattern relationPattern = Pattern.compile("^(?<relation>le|lq|eq|ne|ge|gq):(?<left>[\\w\\d.]+):(?<right>[\\w\\d.]+)");

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean holds(String condition, PlatformInteraction.ProtoSender sender) {
        if (!condition.contains(":")) {
            try {
                return Integer.parseInt(condition) != 0;
            } catch (NumberFormatException ignored) {
                return condition.toLowerCase(Locale.ROOT).equals("yes") || condition.toLowerCase(Locale.ROOT).equals("online");
            }
        }
        Matcher permissionMatcher = permissionPattern.matcher(condition);
        if (permissionMatcher.matches()) {
            return permissionMatcher.group("mode").equals("perm") == sender.hasPermission(permissionMatcher.group("node"));
        }

        Matcher relationMatcher = relationPattern.matcher(condition);
        if (relationMatcher.matches()) {
            String mode = permissionMatcher.group("mode");
            String left = permissionMatcher.group("left");
            String right = permissionMatcher.group("right");
            try {
                switch (mode) {
                    case "le": return Integer.parseInt(left) <= Integer.parseInt(right);
                    case "lq": return Integer.parseInt(left) < Integer.parseInt(right);
                    case "eq": return left.equalsIgnoreCase(right);
                    case "ne": return !left.equalsIgnoreCase(right);
                    case "ge": return Integer.parseInt(left) >= Integer.parseInt(right);
                    case "gq": return Integer.parseInt(left) > Integer.parseInt(right);
                }
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        return false;
    }
}