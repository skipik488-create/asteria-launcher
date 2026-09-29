# Asteria Client

Fabric client module for Minecraft 26.3. Press **Right Shift** in game to open
the module menu.

The first build includes:

- FPS, coordinates, ping, clock, CPS and keystrokes
- armor, potion effects, biome and direction
- movement speed and JVM memory
- toggle sprint
- persistent per-user configuration in `config/asteria-client.json`

Build with Java 25:

```shell
./gradlew build
```

The distributable JAR is written to `build/libs/`.