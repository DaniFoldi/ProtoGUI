package hu.nugget.bungeegui.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {
    public static Pair<String, String> get(String value) {
        String a = Arrays.stream(value.split(":")).findFirst().orElse("");
        String b = Arrays.stream(value.split(":")).skip(1L).collect(Collectors.joining(":"));

        return Pair.of(a, b);
    }

    public static List<String> blockPrint(String value) {
        String mid = "      " + value + "      ";
        return List.of("-".repeat(mid.length()), " ".repeat(mid.length()), mid, " ".repeat(mid.length()), "-".repeat(mid.length()));
    }
}
