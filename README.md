# BungeeGUI Plugin for BungeeCord

## Setup tutorial

Dependencies: **Protocolize** is required. Please make sure you have it installed, or the plugin will fail to load.
Download the latest version from either GitHub or Spigot, put it into the `plugins` folder of your Bungee, and you're done.

Of course, you will probably want to customize the GUIs of the plugin. For that, see the example `config.yml` [here](https://github.com/DaniFoldi/BungeeGUI/blob/main/src/main/resources/config.yml), and below for explanations.

---

## Plugin configuration

All data of the plugin is saved in the `config.yml` file of the plugin's folder. You can apply any changes and then `/bguireload` for the plugin to reload the config.


**messages**

All player-visible messages of the plugin are translatable in the `messages` section.

**guis**

```yaml
test: # the name of the gui, used internally and in the API
    targeted: true # default: false; do the commands require a target attribute
    aliases: # default: []; the commands that open the GUI
      - test
      - testcommand
    permission: 'somepermission.test' # default: bungeegui.gui.<guiname>; overrides the permission required to execute the commands
    size: 27 # default: 54; the size of the GUI
    title: '&2Test {target}' # default: GUI <guiname>; The title of the GUI displayed on top
    selfTarget: false # default: true; can the executing player and the target be the same player
    requireOnlineTarget: true # default: false; does the target have to be an online player
    ignoreVanished: false # default: true; are vanished players ignored from the target list. NOTE: supports PremiumVanish on Bungee side, and only works if requireOnlineTarget is true
    whitelistServers: # default: [*]; the commands will only work on the servers specified below. NOTE: omit or set the first element to `*` to enable on all servers
      - server1
      - server2
    blacklistServers: # default: []; the commands will not work on the servers specified below. NOTE: overrides whitelisted servers.
      - server3
      - server4
    items: # default: []; the items in the GUI
      '13': # the slot this item will be displayed in. NOTE: has to be a 'string', eg. '1'. The first slot is indexed 0.
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
```

_Tips:_

All commands, gui titles, item names, lores support the `{player}` and `{target}` placeholders. `{player}` is the player seeing the GUI, `{target}` is their target, if there is one.

Set the `commands` of an item to `['']` to create a _close_ interaction, empty commands are ignored. Omit the `commands` property if you don't want an item to close the GUI.

If a player does not have the permission to run the commands, you can still use the API (see below) to open the GUI for them.

Setting many `owner:<UUID/name>` playerheads will delay the GUI opening, so unless you need it to be dynamic, `texture:<texture>` is recommended.

See the example `config.yml` that is auto-generated or in the repository for some GUI ideas.

**configVersion**

This should say `1`. If there are any breaking config changes, the value will be incremented. The conversion process should be automatic, unless the release notes say otherwise.

---

## Permissions

The default permission to open a GUI is `bungeegui.gui.<guiname>`. You can override this in the config of each GUI.
The permission to reload the plugin is `bungeegui.command.reload`.

## API Usage

BungeeGUI provides an API which you can use to open and close GUIs for players and more. You can obtain the API instance with `BungeeGuiAPI.getInstance()`.

All available methods have JavaDocs provided in `BungeeGuiAPI.java`. [Click here](https://github.com/DaniFoldi/BungeeGUI/blob/main/src/main/java/com/danifoldi/bungeegui/main/BungeeGuiAPI.java) to see the available methods and how to use them.

## Notes

I developed this plugin in my free time. Please use the Issues page on GitHub (linked above) if you believe you found a bug, or would like a new feature added. I cannot guarantee any form of support, or any updates.

What I can guarantee however, is that leaving a 1* review won't get you far. Please be kind, and I will do my best to help you.

## License, terms

The plugin is licensed under the GNU GPL v3.0 license. [check here](https://github.com/DaniFoldi/BungeeGUI/blob/main/LICENSE)

For non-coders:
- don't claim the plugin as yours
- don't sell the plugin
- decompile the plugin, I don't care, it's opensource

You can view the source code at the link above, contributions and pull requests are welcome.
