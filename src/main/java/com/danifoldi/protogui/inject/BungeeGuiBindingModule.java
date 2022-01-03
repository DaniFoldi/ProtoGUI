package com.danifoldi.protogui.inject;

import com.danifoldi.protogui.command.PluginCommand;
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
