package com.danifoldi.protogui.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum VersionUtil {
    UNKNOWN	(999),
    v1_18_2 (758),
    v1_18_1 (757),
    v1_18   (757),
    v1_17_1 (756),
    v1_17   (755),
    v1_16_5 (754),
    v1_16_4	(754),
    v1_16_3	(753),
    v1_16_2	(751),
    v1_16_1	(736),
    v1_16	(735),
    v1_15_2	(578),
    v1_15_1	(575),
    v1_15	(573),
    v1_14_4	(498),
    v1_14_3	(490),
    v1_14_2	(485),
    v1_14_1	(480),
    v1_14	(477),
    v1_13_2	(404),
    v1_13_1	(401),
    v1_13	(393),
    v1_12_2	(340),
    v1_12_1	(338),
    v1_12	(335),
    v1_11_2	(316),
    v1_11_1	(316),
    v1_11	(315),
    v1_10_2	(210),
    v1_10_1	(210),
    v1_10	(210),
    v1_9_4	(110),
    v1_9_3	(110),
    v1_9_2	(109),
    v1_9_1	(108),
    v1_9	(107),
    v1_8_9	(47),
    v1_8_8	(47),
    v1_8_7	(47),
    v1_8_6	(47),
    v1_8_5	(47),
    v1_8_4	(47),
    v1_8_3	(47),
    v1_8_2	(47),
    v1_8_1	(47),
    v1_8	(47),
    v1_7_10	(5),
    v1_7_9	(5),
    v1_7_8	(5),
    v1_7_7	(5),
    v1_7_6	(5),
    v1_7_5	(4),
    v1_7_4	(4),
    v1_7_2	(4),
    v1_6_4	(78),
    v1_6_2	(74),
    v1_6_1	(73),
    v1_5_2	(61),
    v1_5_1	(60),
    v1_5	(60),
    v1_4_7	(51),
    v1_4_6	(51);

    private final int protocolVersion;

    VersionUtil(final int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public static @NotNull VersionUtil find(final int protocolVersion) {
        return Arrays.stream(VersionUtil.values()).filter(p -> p.protocolVersion == protocolVersion).findFirst().orElse(UNKNOWN);
    }

    public @NotNull String getVersion() {
        for (VersionUtil version: VersionUtil.values()) {
            if (version.protocolVersion == protocolVersion) {
                return version.toString().replace("v", "").replace("_", ".");
            }
        }

        return "UNKNOWN";
    }
}
