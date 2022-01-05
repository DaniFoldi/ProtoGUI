# ProtoGUI Plugin for BungeeCord

## Features

- Open chest GUIs of any size
- 100% configurable commands
- Completely translatable
- Highly customisable GUIs
- Sound support
- Full color support (with RGB where applicable) in all content
- Many placeholders
- Targeted GUIs
- Utility commands that can be used as click actions or separately

## Setup tutorial

Dependencies: [**Protocolize**](https://www.spigotmc.org/resources/protocolize-protocollib-for-bungeecord-waterfall-aegis.63778/) is required. Please make sure you have it installed, or the plugin will fail to load.
Download the latest version from either GitHub or Spigot, put it into the `plugins` folder of your Bungee, and you're done.

Of course, you will probably want to customize the GUIs of the plugin. For that, see the example `config.yml` [here](https://github.com/DaniFoldi/ProtoGUI/blob/main/src/main/resources/config.yml), and below for explanations.

Optional dependencies: *PremiumVanish*, *LuckPerms*.

---

## Plugin configuration

All data of the plugin is saved in the `config.yml` file of the plugin's folder. You can apply any changes and then `/bgui reload` for the plugin to reload the config.


**messages**

All player-visible messages of the plugin are translatable in the `messages` section.

**guis**

```yaml
test: # the name of the gui, used internally and in the API
    targeted: true # default: false; do the commands require a target attribute
    aliases: # default: []; the commands that open the GUI
      - test
      - testcommand
    permission: 'somepermission.test' # default: protogui.gui.<guiname>; overrides the permission required to execute the commands
    size: 27 # default: 54; the size of the GUI
    title: '&2Test {target}' # default: GUI <guiname>; the title of the GUI displayed on top
    selfTarget: false # default: true; can the executing player and the target be the same player
    requireOnlineTarget: true # default: false; does the target have to be an online player
    ignoreVanished: false # default: true; are vanished players ignored from the target list. NOTE: supports PremiumVanish on Bungee side, and only works if requireOnlineTarget is true
    whitelistServers: # default: [*]; the commands will only work on the servers specified below. NOTE: omit or set the first element to `*` to enable on all servers
      - server1
      - server2
    blacklistServers: # default: []; the commands will not work on the servers specified below. NOTE: overrides whitelisted servers
      - server3
      - server4
    placeholdersTarget: true # default: false; if the placeholders should target the {target} or the {player}. NOTE: only works with requireOnlineTarget: true
    openSound: # omit to play no sound
      sound: entity_piglin_jealous # default: ENTITY_VILLAGER_NO; the sound to play when opening the gui NOTE: see link below for valid sounds
      soundCategory: blocks # default: MASTER; the sound channel to play the sound on. NOTE: see link below for valid soundcategories
      volume: 0.6 # default: 1.0; the volume to play the sound at
      pitch: 1.2 # default 1.0; the pitch to play the sound at
    targetBypass: true # default false; whether players with the permission <guiPermission>.bypass can not be targeted
    closeable: false # default: true; whether players can press Esc to close the gui, or a command has to be executed. NOTE: see below for how to create a "close" button
    notifyTarget: '{player} targeted you with a GUI' # default: ''; the message that will be sent to the target, if they are online. NOTE: omit to send no message
    items: # default: []; the items in the GUI
      '13': # the slot this item will be displayed in. NOTE: see below for advanced options. Has to be a 'string', eg. '1'. The first slot is indexed 0
        type: 'cobblestone' # default: stone; the material of the item
        count: 10 # default: 1; the amount of the item
        name: '&5some item' # default: <item name>; the name of the item
        lore: # default: []; the lore of the item
          - '&5&litem'
          - '&5lore'
        enchanted: true # default: false; whether the item is glowing as enchanted
        data: '' # default: ''; set to owner:<UUID/name> or texture:<texture> to display custom heads. NOTE: only supports `player_head`
        commands: # default: []; the commands to be executed. NOTE: prefix a command with `console:` to run it as the Bungee console instead of the player
          - 'console:broadcast {target}'
          - 'list'
        clickSound: # omit to play no sound
          sound: entity_parrot_imitate_ghast # default: ENTITY_VILLAGER_NO; the sound to play when opening the gui NOTE: see link below for valid sounds
          soundCategory: hostile # default: MASTER; the sound channel to play the sound on. NOTE: see link below for valid soundcatetories
          volume: 0.6 # default: 1.0; the volume to play the sound at
          pitch: 1.2 # default 1.0; the pitch to play the sound at
```

_Tips:_

All commands, gui titles, item names, lores support the `{player}` and `{target}` placeholders. `{player}` is the player seeing the GUI, `{target}` is their target, if there is one.

Set the `commands` of an item to `['']` to create a _close_ interaction, empty commands are ignored. Omit the `commands` property if you don't want an item to close the GUI.

If a player does not have the permission to run the commands, you can still use the API or helper command (see below) to open the GUI for them.

Setting many `owner:<UUID/name>` playerheads will delay the GUI opening, so unless you need it to be dynamic, `texture:<texture>` is recommended.

Show the player their own head by setting the data to `owner:{player}`.

See the example `config.yml` that is auto-generated or in the repository for some GUI ideas.

You can specify multiple slots with one item, and they will be cloned.

- Formatting examples: `row1`, `row4even`, `row6odd`, `row2,-9,-17`, `column0, column8`, `row3odd,row4even,row5odd`
- You can chain multiple expressions together with commas.
- You can select a row, a column, or a slot.
    - You can select only odd or only even slots from a row/column, or all.
- You can add or remove a slot from the list.

Valid sound list: [here](https://github.com/Exceptionflug/protocolize/blob/master/protocolize-world/src/main/java/de/exceptionflug/protocolize/world/Sound.java)

Valid soundcategory list: [here](https://github.com/Exceptionflug/protocolize/blob/master/protocolize-world/src/main/java/de/exceptionflug/protocolize/world/SoundCategory.java)

You can send custom sounds with `custom:<soundName>`.

___Expressions are evaluated in order___

**configVersion**

This should say `3`. If there are any config changes, the value will be incremented. The conversion process should be automatic, unless the release notes say otherwise.

---

## Commands

The plugin registers the command `/protogui` with many subcommands that contain useful utility functions.
A list of these will be added here shortly. You can view them in-game with instructions on how to use each.

## Placeholders

All messages support the placeholders in the table below. The API can be used to register additional placeholders.

| Placeholder | Description                                                                                    |
| ----------- |------------------------------------------------------------------------------------------------|
|`%protogui%`| ProtoGUI information                                                                          |
|`%guicount%`| Number of loaded GUIs in ProtoGUI                                                             |
|`%placeholdercount%`| Number of placeholders registered in ProtoGUI                                                 |
|`%ram_used%`| Amount of RAM used by the proxy                                                                |
|`%ram_total%`| Total RAM allocated by the proxy                                                               |
|`%proxyname%`| The name of the proxy                                                                          |
|`%proxyversion%`| The version of the proxy                                                                       |
|`%plugincount%`| Number of plugins loaded                                                                       |
|`%servercount%`| Number of servers proxied                                                                      |
|`%online%`| Total number of players online                                                                 |
|`%online_visible%`| Number of players online that are not vanished _(PV required)_                                 |
|`%max%`| Maximum number of online players                                                               |
|`%name%`| Player name                                                                                    |
|`%uuid%`| Player UUID                                                                                    |
|`%displayname%`| Player displayname                                                                             |
|`%locale%`| The locale used by the player                                                                  |
|`%version%`| The game version used by the player                                                            |
|`%ping%`| Latest ping measurement of the player                                                          |
|`%vanished%`| Whether the player is vanished or not                                                          |
|`%servername%`| The name of the server the player is connected to                                              |
|`%servermotd%`| The MOTD of the server the player is connected to                                              |
|`%luckperms_friendlyname%`| The friendly name of the player as set in LP _(LP required)_                                   |
|`%luckperms_prefix%`| The prefix of the player _(LP required)_                                                       |
|`%luckperms_suffix%`| The suffix of the player _(LP required)_                                                       |
|`%luckperms_group%`| The primary group of the player _(LP required)_                                                |
|`%is_online@<servername>%`| Whether the server is online or not _(Updates every 5 seconds)_                                |
|`%status@<servername>%`| Returns Online / Offline based on the last ping _(Updates every 5 seconds)_                    |
|`%online@<servername>%`| Number of players connected to a server _(Updates every 5 seconds)_                            |
|`%online_visible@<servername>%`| Number of players not vanished connected to a server  _(Updates every 5 seconds, PV required)_ |
|`%max@<servername>%`| Maximum number of players connected to a server _(Updates every 5 seconds)_                    |
|`%version@<servername>%`| Game version of a server _(Updates every 5 seconds)_                                           |
|`%name@<servername>%`| Name of a server _(Updates every 5 seconds)_                                                   |
|`%motd@<servername>%`| MOTD of a server  _(Updates every 5 seconds)_                                                  |
|`%plugin_description@<pluginname>%`| The description of a plugin                                                                    |
|`%plugin_version@<pluginname>%`| The version of a plugin                                                                        |
|`%plugin_main@<pluginname>%`| The main class of a plugin                                                                     |
|`%plugin_author@<pluginname>%`| The author of a plugin                                                                         |
|`%plugin_depends@<pluginname>%`| The dependencies of a plugin                                                                   |
|`%plugin_softdepends@<pluginname>%`| The soft-dependencies of a plugin                                                              |


## Permissions

The default permission to open a GUI is `protogui.gui.<guiname>`. You can override this in the config of each GUI.
The permission for each of the utility commands is `protogui.command.<command>`. You can see the complete list in-game by typing `/pgui`.

## API Usage

As the plugin was previously called BungeeGUI, the old API class had a different package, which is now deprecated, but bridging is provided for a smooth transition until a future version. **The old API class is now deprecated.**

ProtoGUI provides an API which you can use to open and close GUIs for players and more. You can obtain the API instance with `ProtoGuiAPI.getInstance()`.

All available methods have JavaDocs provided in `ProtoGuiAPI.java`. [Click here](https://github.com/DaniFoldi/ProtoGUI/blob/main/src/main/java/com/danifoldi/protogui/main/ProtoGuiAPI.java) to see the available methods and how to use them.

NOTE: the placeholder registry is currently in beta, and the API will likely be changed.

## Notes

I developed this plugin in my free time. Please use the Issues page on GitHub (linked above) if you believe you found a bug, or would like a new feature added. I cannot guarantee any form of support, or any updates.

What I can guarantee however, is that leaving a 1* review won't get you far. Please be kind, and I will do my best to help you.

## License, terms

The plugin is licensed under the GNU GPL v3.0 license. [check here](https://github.com/DaniFoldi/ProtoGUI/blob/main/LICENSE)

For non-coders:
- don't claim the plugin as yours
- don't sell the plugin
- decompile the plugin, I don't care, it's opensource

You can view the source code at the link above, contributions and pull requests are welcome.
