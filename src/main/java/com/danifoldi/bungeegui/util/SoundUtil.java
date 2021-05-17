package com.danifoldi.bungeegui.util;

import de.exceptionflug.protocolize.world.Location;
import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.SoundCategory;
import de.exceptionflug.protocolize.world.WorldModule;
import de.exceptionflug.protocolize.world.packet.NamedSoundEffect;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SoundUtil {
    public static void playSound(ProxiedPlayer player, String soundName, SoundCategory category, float volume, float pitch) {
        NamedSoundEffect soundEffect = new NamedSoundEffect();
        soundEffect.setCategory(category);
        soundEffect.setPitch(pitch);
        soundEffect.setVolume(volume);
        soundEffect.setSound(StringUtil.get(soundName).getSecond());
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

    private SoundUtil() {
        throw new UnsupportedOperationException();
    }
}
