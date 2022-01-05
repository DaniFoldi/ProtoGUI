package com.danifoldi.protogui.util;

import dev.simplix.protocolize.api.Location;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.data.Sound;
import dev.simplix.protocolize.data.packets.NamedSoundEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class SoundUtil {
    public static void playSound(final @NotNull UUID uuid,
                                 final @NotNull String soundName,
                                 final @NotNull SoundCategory category,
                                 final float volume,
                                 final float pitch) {
        final @NotNull NamedSoundEffect soundEffect = new NamedSoundEffect();
        soundEffect.category(category);
        soundEffect.pitch(pitch);
        soundEffect.volume(volume);
        final @NotNull Pair<String, String> name = StringUtil.get(soundName);
        soundEffect.sound(name.getFirst().equalsIgnoreCase("custom") ? name.getSecond() : correct(name.getSecond()));
        final @NotNull Location location = Protocolize.playerProvider().player(uuid).location();
        soundEffect.x(location.x());
        soundEffect.y(location.y());
        soundEffect.z(location.z());
        Protocolize.playerProvider().player(uuid).sendPacket(soundEffect);
    }

    public static boolean isValidSound(final @NotNull String soundName) {
        final @NotNull Pair<String, String> value = StringUtil.get(soundName);
        try {
            Sound.valueOf(value.getSecond());
        } catch (IllegalArgumentException e) {
            if (!value.getFirst().equals("custom")) {
                return false;
            }
        }

        return true;
    }

    private static final @NotNull Map<String, String> rewrites = MapUtil.convertToMap(
            "zombie.villager_", "zombie_villager.",
            "armor.stand", "armor_stand",
            "cave.spider", "cave_spider",
            "dragon.fireball", "dragon_fireball",
            "elder.guardian", "elder_guardian",
            "ender.dragon", "ender_dragon",
            "ender.eye", "ender_eye",
            "firework.rocket", "firework_rocket",
            "iron.golem", "iron_golem",
            "item.frame", "item_frame",
            "leash.knot", "leash_knot",
            "lightning.bolt", "lightning_bolt",
            "polar.bear", "polar_bear",
            "shulker.box", "shulker_box",
            "snow.golem", "snow_golem",
            "ender.pearl", "ender_pearl",
            "puffer.fish", "puffer_fish",
            "tropical.fish", "tropical_fish",
            "wither.skeleton", "wither_skeleton",
            "zombie.horse", "zombie_horse",
            "zombie.pigman", "zombie_pigman",
            "fishing.bobber", "fishing_bobber",
            "zombie.villager", "zombie_villager",
            "power.select", "power_select",
            "bubble.column", "bubble_column",
            "bubble.pop", "bubble_pop",
            "upwards.ambient", "upwards_ambient",
            "upwards.inside", "upwards_inside",
            "whirlpool.ambient", "whirlpool_ambient",
            "whirlpool.inside", "whirlpool_inside",
            "coral.block", "coral_block",
            "note.block", "note_block",
            "hurt.drown", "hurt_drown",
            "hurt.on.fire", "hurt_on_fire",
            "slime.block", "slime_block",
            "blow.out", "blow_out",
            "blow.up", "blow_up"
    );

    private static @NotNull String correct(@NotNull String value) {
        for (Map.Entry<String, String> rewrite: rewrites.entrySet()) {
            if (rewrite.getKey() == null || rewrite.getValue() == null) {
                continue;
            }
            value = value.replace(rewrite.getKey(), rewrite.getValue());
        }
        return value;
    }

    private SoundUtil() {
        throw new UnsupportedOperationException();
    }
}
