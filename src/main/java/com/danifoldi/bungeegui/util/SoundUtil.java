package com.danifoldi.bungeegui.util;

import de.exceptionflug.protocolize.world.Location;
import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.SoundCategory;
import de.exceptionflug.protocolize.world.WorldModule;
import de.exceptionflug.protocolize.world.packet.NamedSoundEffect;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class SoundUtil {
    public static void playSound(ProxiedPlayer player, String soundName, SoundCategory category, float volume, float pitch) {
        NamedSoundEffect soundEffect = new NamedSoundEffect();
        soundEffect.setCategory(category);
        soundEffect.setPitch(pitch);
        soundEffect.setVolume(volume);
        Pair<String, String> name = StringUtil.get(soundName);
        soundEffect.setSound(name.getFirst().equalsIgnoreCase("custom") ? name.getSecond() : correct(name.getSecond()));
        Location location = WorldModule.getLocation(player.getUniqueId());
        soundEffect.setX(location.getX());
        soundEffect.setY(location.getY());
        soundEffect.setZ(location.getZ());
        player.unsafe().sendPacket(soundEffect);
    }

    public static boolean isValidSound(String soundName) {
        Pair<String, String> value = StringUtil.get(soundName);
        try {
            Sound.valueOf(value.getSecond());
        } catch (IllegalArgumentException e) {
            if (!value.getFirst().equals("custom")) {
                return false;
            }
        }

        return true;
    }

    private static final Map<String, String> rewrites = MapUtil.convertToMap(
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

    private static String correct(String original) {
        if (original == null) {
            return null;
        }
        String replacement = original;
        for (Map.Entry<String, String> rewrite: rewrites.entrySet()) {
            if (rewrite.getKey() == null || rewrite.getValue() == null) {
                continue;
            }
            replacement = replacement.replace(rewrite.getKey(), rewrite.getValue());
        }
        return replacement;
    }

    private SoundUtil() {
        throw new UnsupportedOperationException();
    }
}
