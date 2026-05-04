# Mine Cells Reforged

This is a Forge 1.20.1 porting project for Mine Cells.

## Build

```bash
./gradlew build
```

On Windows, `gradlew.bat build` can also be used.

## Status

This project is currently a work in progress. The Forge project skeleton and metadata have been converted from the Forge MDK example to the `minecells` mod id, but the original Fabric systems are not fully ported yet.

## Known missing features

- Fabric registries are not fully replaced with Forge deferred registries.
- Fabric/owo networking is not yet replaced with Forge SimpleChannel.
- owo-lib config/UI systems are not yet replaced.
- Worldgen, dimensions, structures, and portals still need full Forge validation.
- Client renderers, screens, particles, and overlays still need Forge client-event registration.
- Mixins still need review/remapping/replacement for Forge and Mojang official mappings.

## Known issues

- The original source uses Yarn mappings, while this Forge workspace uses Mojang official mappings.
- Dedicated server safety still needs verification after client code is ported.
- Assets are copied for local porting/testing only until redistribution permissions are confirmed.

## Required dependencies

Dependencies are still being evaluated. The original Fabric project uses Fabric API, owo-lib, Patchouli, EMI/REI integrations, and `gimm1q`; Forge equivalents or optional integrations still need to be implemented.

## License notes

The upstream source repository contains an MIT license for code. The upstream asset license file marks assets as All Rights Reserved by Mim1q. Do not prepare public redistribution until asset redistribution permission is confirmed.
