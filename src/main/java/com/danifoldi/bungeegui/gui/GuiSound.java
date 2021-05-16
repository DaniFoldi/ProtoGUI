package com.danifoldi.bungeegui.gui;

import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.SoundCategory;
import de.exceptionflug.protocolize.world.WorldModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GuiSound {

    private final Sound sound;
    private final SoundCategory soundCategory;
    private final float volume;
    private final float pitch;

    private GuiSound(final @NotNull Sound sound,
                     final @NotNull SoundCategory soundCategory,
                     final float volume,
                     final float pitch) {
        this.sound = sound;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return this.sound;
    }

    public SoundCategory getSoundCategory() {
        return this.soundCategory;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void playFor(ProxiedPlayer player) {
        if (volume <= 0) {
            return;
        }

        WorldModule.playSound(player, sound, soundCategory, volume, pitch);
    }

    @Override
    public String toString() {
        return "GuiSound{" +
                "sound=" + sound +
                ", soundCategory=" + soundCategory +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiSound guiSound = (GuiSound) o;
        return Float.compare(guiSound.volume, volume) == 0 && Float.compare(guiSound.pitch, pitch) == 0 && sound == guiSound.sound && soundCategory == guiSound.soundCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sound, soundCategory, volume, pitch);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Sound sound;
        private SoundCategory soundCategory;
        private float volume;
        private float pitch;

        private  Builder() {

        }

        public @NotNull Builder sound(final @NotNull Sound sound) {
            this.sound = sound;
            return this;
        }

        public @NotNull Builder soundCategory(final @NotNull SoundCategory soundCategory) {
            this.soundCategory = soundCategory;
            return this;
        }

        public @NotNull Builder volume(final float volume) {
            this.volume = volume;
            return this;
        }

        public @NotNull Builder pitch(final float pitch) {
            this.pitch = pitch;
            return this;
        }


        public @NotNull GuiSound build() {
            return new GuiSound(
                    sound,
                    soundCategory,
                    volume,
                    pitch
            );
        }
    }

    public GuiSound copy() {
        return new GuiSound(sound, soundCategory, volume, pitch);
    }
}
