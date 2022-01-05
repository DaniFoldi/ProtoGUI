package com.danifoldi.bungeegui.gui;

import dev.simplix.protocolize.api.SoundCategory;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Deprecated(forRemoval = true)
public class GuiSound {

    private final @NotNull com.danifoldi.protogui.gui.GuiSound guiSound;

    private GuiSound(final @NotNull com.danifoldi.protogui.gui.GuiSound guiSound) {
        this.guiSound = guiSound;
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getSoundName() {
        return guiSound.getSoundName();
    }

    @Deprecated(forRemoval = true)
    public @NotNull SoundCategory getSoundCategory() {
        return guiSound.getSoundCategory();
    }

    @Deprecated(forRemoval = true)
    public float getVolume() {
        return guiSound.getVolume();
    }

    @Deprecated(forRemoval = true)
    public float getPitch() {
        return guiSound.getPitch();
    }

    @Deprecated(forRemoval = true)
    public void playFor(ProxiedPlayer player) {
        guiSound.playFor(player.getUniqueId());
    }

    @Override
    @Deprecated(forRemoval = true)
    public String toString() {
        return "GuiSound{" +
                "guiSound=" + guiSound +
                '}';
    }

    @Override
    @Deprecated(forRemoval = true)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiSound guiSound1 = (GuiSound) o;
        return guiSound.equals(guiSound1.guiSound);
    }

    @Override
    @Deprecated(forRemoval = true)
    public int hashCode() {
        return Objects.hash(guiSound);
    }

    @Deprecated(forRemoval = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @NotNull com.danifoldi.protogui.gui.GuiSound.Builder builder;

        private  Builder() {
            builder = com.danifoldi.protogui.gui.GuiSound.builder();
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder soundName(final @NotNull String soundName) {
            builder.soundName(soundName);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder soundCategory(final @NotNull SoundCategory soundCategory) {
            builder.soundCategory(soundCategory);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder volume(final float volume) {
            builder.volume(volume);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder pitch(final float pitch) {
            builder.pitch(pitch);
            return this;
        }


        @Deprecated(forRemoval = true)
        public @NotNull GuiSound build() {
            return new GuiSound(builder.build());
        }
    }

    @Deprecated(forRemoval = true)
    public @NotNull GuiSound copy() {
        return new GuiSound(guiSound);
    }

    @Deprecated(forRemoval = true)
    public static com.danifoldi.protogui.gui.GuiSound toNew(GuiSound old) {
        return old.guiSound.copy();
    }

    @Deprecated(forRemoval = true)
    public static GuiSound fromNew(com.danifoldi.protogui.gui.GuiSound n) {
        return new GuiSound(n.copy());
    }
}