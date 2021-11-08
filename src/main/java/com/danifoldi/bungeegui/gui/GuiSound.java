package com.danifoldi.bungeegui.gui;

import com.danifoldi.bungeegui.util.SoundUtil;
import dev.simplix.protocolize.api.SoundCategory;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class GuiSound {

    private final @NotNull String soundName;
    private final @NotNull SoundCategory soundCategory;
    private final float volume;
    private final float pitch;

    private GuiSound(final @NotNull String soundName,
                     final @NotNull SoundCategory soundCategory,
                     final float volume,
                     final float pitch) {
        this.soundName = soundName;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    public @NotNull String getSoundName() {
        return this.soundName;
    }

    public @NotNull SoundCategory getSoundCategory() {
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

        SoundUtil.playSound(player, getSoundName(), getSoundCategory(), getVolume(), getPitch());
    }

    @Override
    public @NotNull String toString() {
        return "GuiSound{" +
                "soundName=" + soundName +
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
        return Float.compare(guiSound.volume, volume) == 0 && Float.compare(guiSound.pitch, pitch) == 0 && soundName.equals(guiSound.soundName) && soundCategory == guiSound.soundCategory;
    }

    @Override
    public int hashCode() {
        return Objects.hash(soundName, soundCategory, volume, pitch);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable String soundName;
        private @Nullable SoundCategory soundCategory;
        private float volume;
        private float pitch;

        private  Builder() {

        }

        public @NotNull Builder soundName(final @NotNull String soundName) {
            this.soundName = soundName;
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
                    requireNonNull(soundName),
                    requireNonNull(soundCategory),
                    volume,
                    pitch
            );
        }
    }

    public @NotNull GuiSound copy() {
        return new GuiSound(soundName, soundCategory, volume, pitch);
    }
}
