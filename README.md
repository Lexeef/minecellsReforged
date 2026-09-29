# Mine Cells Reforged

An unofficial Forge 1.20.1 port of [Mine Cells](https://github.com/Mim1q/MineCells) by Mim1q, a Fabric mod that brings content from Dead Cells into Minecraft. The port follows Mine Cells 2.0.0 for Minecraft 1.20.1 and aims to behave the same as the original. The mod id stays `minecells`, so datapacks, tags, and resource packs made for Mine Cells keep working.

This is a pre-release. It is not affiliated with or endorsed by Mim1q.

## What works

- All blocks, items, weapons, shields, bows and crossbows, spells, and the Cell Crafter, with the original recipes, loot tables, tags, and advancements.
- All mobs and both bosses (Conjunctivius and Concierge), with their models, animations, behaviour, elite variants, and spawner runes.
- The five dimensions (Prisoners' Quarters, Promenade of the Condemned, Insufferable Crypt, Ramparts, Black Bridge), with grid-based structure generation, doorways, per-player runs, the run border, and fall protection.
- Status effects, particles, sounds, screen shake, custom boss bars, and the creative menu layout of the original.
- Common and client config (`minecells-common.toml`, `minecells-client.toml`), with the synced values sent to clients.
- Dedicated servers.
- Optional integrations: Patchouli guidebook, EMI, JEI, REI (Cell Crafter recipes), and Better Combat weapon attributes. None of them are required.

In-game testing is still ongoing, so expect some bugs. Please report them in this repository.

## Requirements

- Minecraft 1.20.1
- Forge 47.x (this project builds against Forge 47.4.20)
- JDK 17, to compile

## Building

```bash
./gradlew build
```

On Windows, use `gradlew.bat build`. The jar is written to `build/libs/minecells-reforged-<version>.jar`.

The Gradle wrapper sets `JAVA_TOOL_OPTIONS=-Xmx2048m` when that variable is unset. `gradle.properties` also sets `org.gradle.jvmargs=-Xmx1536m`.

Development runs:

```bash
./gradlew runClient
./gradlew runServer
```

Add `-PwithBetterCombat` or `-PwithPatchouli` to load those mods in the development environment.

## License

The Mine Cells source code is under the MIT License, Copyright (c) 2024 Mim1q. That is the `LICENSE` file in the [original repository](https://github.com/Mim1q/MineCells).

Mine Cells assets are All Rights Reserved by Mim1q (`LICENSE_ASSETS` in the original repository). The original README states that the textures and models may not be reused or redistributed without permission, and that a jar which contains those assets may not be redistributed without permission. This port includes those assets, so it is not publicly redistributed unless Mim1q grants permission.

## Credits

Original mod, assets, and design by Mim1q. Forge port by Lexeef.

## Русский

Неофициальный порт [Mine Cells](https://github.com/Mim1q/MineCells) автора Mim1q на Forge 1.20.1. Оригинал — мод для Fabric, который переносит контент Dead Cells в Minecraft. Порт следует версии Mine Cells 2.0.0 для Minecraft 1.20.1 и должен вести себя так же, как оригинал. Идентификатор мода по-прежнему `minecells`, поэтому датапаки, теги и ресурспаки для Mine Cells продолжают работать.

Это предварительная сборка. Она не связана с Mim1q и не одобрена им.

### Что работает

- Все блоки, предметы, оружие, щиты, луки и арбалеты, заклинания и Cell Crafter, с исходными рецептами, таблицами добычи, тегами и достижениями.
- Все мобы и оба босса (Conjunctivius и Concierge): модели, анимации, поведение, элитные варианты и руны призыва.
- Пять измерений (Prisoners' Quarters, Promenade of the Condemned, Insufferable Crypt, Ramparts, Black Bridge): сеточная генерация структур, порталы, отдельные забеги игроков, граница забега и защита от падения.
- Эффекты, частицы, звуки, тряска экрана, свои полоски боссов и творческое меню, как в оригинале.
- Общий и клиентский конфиг (`minecells-common.toml`, `minecells-client.toml`); синхронизируемые значения отправляются клиентам.
- Выделенные серверы.
- Необязательные интеграции: книга Patchouli, EMI, JEI, REI (рецепты Cell Crafter) и атрибуты оружия Better Combat. Ни одна из них не обязательна.

Проверка в игре ещё идёт, ошибки возможны. Сообщайте о них в этом репозитории.

### Требования

- Minecraft 1.20.1
- Forge 47.x (сборка рассчитана на Forge 47.4.20)
- JDK 17 для компиляции

### Сборка

```bash
./gradlew build
```

В Windows используйте `gradlew.bat build`. Готовый файл лежит в `build/libs/minecells-reforged-<version>.jar`.

Если переменная `JAVA_TOOL_OPTIONS` не задана, обёртка Gradle выставляет `JAVA_TOOL_OPTIONS=-Xmx2048m`. В `gradle.properties` также указано `org.gradle.jvmargs=-Xmx1536m`.

Запуск среды разработки:

```bash
./gradlew runClient
./gradlew runServer
```

Флаги `-PwithBetterCombat` и `-PwithPatchouli` подключают эти моды в среде разработки.

### Лицензия

Исходный код Mine Cells распространяется по лицензии MIT, Copyright (c) 2024 Mim1q. Это файл `LICENSE` в [оригинальном репозитории](https://github.com/Mim1q/MineCells).

Ресурсы Mine Cells защищены авторским правом Mim1q, все права сохранены (`LICENSE_ASSETS` в оригинальном репозитории). В README оригинала сказано, что текстуры и модели нельзя использовать повторно или распространять без разрешения, и что jar с этими ресурсами тоже нельзя распространять без разрешения. Этот порт включает эти ресурсы, поэтому публично не распространяется, пока Mim1q не разрешит это.

### Авторы

Оригинальный мод, ресурсы и замысел — Mim1q. Порт на Forge — Lexeef.
