package com.danifoldi.protogui.platform.velocity;

import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;

@Plugin(authors = {"DaniFoldi", "HgeX"},
dependencies = {
        @Dependency(id = "Protocolize"),
        @Dependency(id = "LuckPerms", optional = true),
        @Dependency(id = "PremiumVanish", optional = true)
},
description = "Create GUIs that perform commands on behalf of the player",
id = "protogui",
version = "@version@"
)
public class ProtoGui {
}
