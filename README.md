# Mine Cells Reforged

An unofficial Forge 1.20.1 port of [Mine Cells](https://github.com/Mim1q/MineCells) by Mim1q, a Fabric mod that brings
content from Dead Cells into Minecraft. The port follows Mine Cells 2.0.0 for Minecraft 1.20.1 and aims to behave the
same as the original. The mod id stays `minecells`, so datapacks, tags and resource packs made for Mine Cells keep working.

This is a pre-release. It is not affiliated with or endorsed by Mim1q.

## What works

- All blocks, items, weapons, shields, bows/crossbows, spells and the Cell Crafter, with the original recipes, loot tables,
  tags and advancements.
- All mobs and both bosses (Conjunctivius and Concierge) with their models, animations, AI, elite variants and spawner runes.
- The five dimensions (Prisoners' Quarters, Promenade of the Condemned, Insufferable Crypt, Ramparts, Black Bridge) with
  the grid-based structure generation, doorways, per-player runs, the run border and fall protection.
- Status effects, particles, sounds, screen shake, custom boss bars and the creative menu layout of the original.
- Common and client config (`minecells-common.toml`, `minecells-client.toml`), with the synced values sent to clients.
- Works on dedicated servers.
- Optional integrations: Patchouli guidebook, EMI, JEI, REI (Cell Crafter recipes) and Better Combat weapon attributes.
  None of them are required.

In-game testing is still ongoing, so expect some bugs. Please report them in this repository.

## Requirements

- Minecraft 1.20.1
- Forge 47.x

## Building

```bash
./gradlew build
```

On Windows use `gradlew.bat build`. The jar is written to `build/libs/minecells-reforged-<version>.jar`.
JDK 17 is required. The wrapper sets `JAVA_TOOL_OPTIONS=-Xmx2048m` when it is not set, which is enough for the build.

Development runs: `./gradlew runClient` and `./gradlew runServer`. Add `-PwithBetterCombat` or `-PwithPatchouli` to load
those mods in the dev environment.

## License

- Code: MIT, like the original Mine Cells source.
- Assets (textures, models, sounds, structures and other resources from Mine Cells): All Rights Reserved by Mim1q.

Because of the asset license, this port is not publicly redistributed until Mim1q grants permission.

## Credits

Original mod, assets and design by Mim1q. Forge port by Lexeef.
