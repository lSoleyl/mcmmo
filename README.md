# MCMMO-Forge
I started this project as an MCMMO enthusiast out of frustration that I cannot add MCMMO to my Forge Modpack.
I do not intend to write a mod, which fully supports all features, MCMMO-Bukkit currently has nor am I going to support other minecraft versions than 1.7.10. 

This is my first Minecraft modding project and I am doing it in my spare time. As this is good practice for me, I am reimplementing
this mod only based on the [MCMMO-Wiki](http://mcmmo.wikia.com/wiki/McMMO_Wiki) pages and I don't intend to reuse any of the 
[original source code](https://github.com/mcMMO-Dev/mcMMO). With this in mind, don't expect this project to be finished in the near future.

Starting with version 1.1.0 this mod will also work in SP, but for now the XP is stored in a world independent file, so the skill progress will shared amongst all worlds.

## Latest Release
[Version 1.3.1](https://github.com/lSoleyl/mcmmo/releases/tag/v1.3.1) is for now, what I consider a good working version. I developed this mod for my own minecraft forge server, which was first running Gregtech and now [FTB Infinity Evolved (v3.0.2)](https://www.curseforge.com/minecraft/modpacks/ftb-infinity-evolved) so this mod has support for the weapons from [Tinkers' Construct](https://www.curseforge.com/minecraft/mc-mods/tinkers-construct) and Gregtech but not for any other weapon based mod. This just means that you won't level up with these weapons and you cannot activate the MCMMO skills with them.

I didn't include the mining related skills into this mod as they are hard to implement to prevent abuse (eg. mining the same block of sand over and over again) and tend to either be completely useless because the drops are worthless or totally overpowered and thus make any other resource gathering mod on the server obsolete.

## Commands
Run the `/mcmmo` command ingame to get description of all available console commands for this mod.

All subcommands for this mod have autocompletion to save you valuable typing time - just enter the first few letters of the command and press TAB to auto-complete it ;)

## Config
The mod generates a configuration file at `config/mcmmo.cfg` where the level progression may be adjusted. The level progression is based on the simple formula: `nextLevelXP = baseXP + slopeXP*level` which is the same formula the original mcMMO uses. Both `baseXP` and `slopeXP` can be changed in the config file to adjust the effort needed to level up. These values can be adjusted before every server restart as needed. Since the mod only stores the players total xp, his current level and skills will be automatically readjusted if one of these values change.

The Experience is stored inside a separate file `saves/mcmmo/xp.json`. This file is world independent, which has the advantage/disadvantage that all skills are shared across all worlds running on the same server.

## Skills

The currently implemented skills are:

 * Archery
 * Axes
 * Combat
 * Diving
 * Firefighting
 * Parkour
 * Poison
 * Swords
 * Unarmed

For detailed information about the skills, run `/mcmmo help <skillname>` ingame or `/mcmmo <skillname>` to view your current stats regarding that skill.

## Development
I currently don't plan on extending the current features of the mod, but If you want to change the mod to your needs, feel free to do so. Fork this repo and change the source the way you like. 

I tried to keep the source understandable and well commented and I added some currently unused classes for those, who plan on implementing mining based skills (`MiningSkill` & `BlockListener`) - the new skill only needs to be added into the `Skill`-Enum and the `SkillRegistry` to be usable.
