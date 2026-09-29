# Fabric and Forge differences

This repository is a Forge port of [Mine Cells](https://github.com/Mim1q/MineCells) by Mim1q. The Fabric tree it follows is `minecells-2.0.0+1.20.1`. The mod id stays `minecells`, and registry ids are unchanged, so datapacks, tags, and existing worlds still match the original.

## Loader and mappings

Fabric Mine Cells is a Fabric Loader mod, mapped with Yarn. This project is Forge 47.4.20 for Minecraft 1.20.1, mapped with Mojang's official names.

The Fabric entrypoint implements `ModInitializer`. Here the entrypoint is `MineCells`, annotated with `@Mod`. Content is registered with Forge `DeferredRegister` instead of Fabric `Registry.register`.

There are no mixins. Hooks that Fabric implements as mixins are Forge events, client-only handlers, or normal method overrides. Client-only classes are loaded on the client distribution, so a dedicated server never loads them.

Names you will see when reading both trees:

| Yarn | Mojmap |
| --- | --- |
| `Identifier` | `ResourceLocation` |
| `World` / `ServerWorld` | `Level` / `ServerLevel` |
| `Text` / `Formatting` | `Component` / `ChatFormatting` |
| `Vec3d` | `Vec3` |

## Networking and config

Packets go through one Forge `SimpleChannel` (`MineCellsNetwork`). Server-to-client packets are sent with `MineCellsNetwork.sendToPlayer`, which does nothing for fake players. Packet classes do not mention client types; the client bodies are in `MineCellsClientPacketHandlers`.

Fabric owo-lib config is two Forge files, `minecells-common.toml` and `minecells-client.toml`. Common options that the Fabric config annotates with `@Sync(OVERRIDE_CLIENT)` are mirrored in `MineCellsSyncedConfig` and sent to remote clients on login and when the common config reloads.

`autoWipeData` and `experimentalMusicLooping` are defined on the Forge specs. Nothing in this port reads them.

## UI and language

Forge language files accept plain strings only. Colored fragments that Fabric builds with owo text arrays are applied in code by `MineCellsText.highlight`.

The owo creative tabs are `MineCellsCreativeTabs` plus `MineCellsCreativeInventoryHandler`. That replaces the Fabric item-group mixins.

Entities use vanilla `EntityModel` and `ModelPart` layers. The Fabric build script does not depend on GeckoLib, and this port does not either.

## Data kept out of the jar

`build.gradle` excludes:

- `data/minecells/loot_tables/advancements/book.json`. Loot tables cannot carry a Forge mod-loaded condition, so `PatchouliCompat` grants the guidebook.
- `data/bewitchment/**` and `data/rpgdifficulty/**`.
- `data/minecraft/tags/fluids/**`. Sewage swimming and drowning come from the Forge fluid type. Adding sewage to `#minecraft:water` would make it behave like water.

The display name is Mine Cells Reforged. The mod id is still `minecells`.

EMI, JEI, and REI are compile-only recipe viewers. Patchouli is compile-only unless a development run is started with `-PwithPatchouli`. Better Combat reads `data/minecells/weapon_attributes` and is not required; `-PwithBetterCombat` loads it in a development run.
