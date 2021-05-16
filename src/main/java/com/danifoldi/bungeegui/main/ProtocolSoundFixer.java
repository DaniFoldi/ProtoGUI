package com.danifoldi.bungeegui.main;

import de.exceptionflug.protocolize.api.event.PacketSendEvent;
import de.exceptionflug.protocolize.api.handler.PacketAdapter;
import de.exceptionflug.protocolize.api.protocol.Stream;
import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.packet.NamedSoundEffect;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProtocolSoundFixer extends PacketAdapter<NamedSoundEffect> {
    @Inject
    public ProtocolSoundFixer() {
        super(Stream.UPSTREAM, NamedSoundEffect.class);

    }

    @Override
    public void send(PacketSendEvent<NamedSoundEffect> event) {
        if (!enabled) {
            return;
        }
        String sound = event.getPacket().getSound() == null ? event.getPacket().getSoundObject().getSoundName(event.getPlayer().getPendingConnection().getVersion()) : event.getPacket().getSound();
        event.getPacket().setSound(replace(sound));
        event.getPacket().setSound((Sound)null);
    }

    private boolean enabled = true;

    void enable() {
        enabled = true;
    }
    void disable() {
        enabled = false;
    }

    private final Map<String, String> rewrites = convertToMap(
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

    private String replace(String original) {
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

    private Map<String, String> convertToMap(String... input) {
        Map<String, String> map = new HashMap<>();

        Iterator<String> iterator = Arrays.stream(input).iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            if (iterator.hasNext()) {
                map.put(key, iterator.next());
            }
        }

        return map;
    }
}
