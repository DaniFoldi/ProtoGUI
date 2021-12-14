package com.danifoldi.bungeegui.inject;

import com.danifoldi.bungeegui.command.PluginCommand;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import grapefruit.command.CommandContainer;

import javax.inject.Named;

@Module
public interface BungeeGuiBindingModule {

    @Binds
    @IntoSet
    @Named("commands")
    CommandContainer bindPluginCommands(final PluginCommand command);
}
