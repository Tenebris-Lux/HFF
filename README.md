[![Release](https://jitpack.io/v/Tenebris-Lux/HytaleFirearmFramework.svg)](https://jitpack.io/#Tenebris-Lux/HytaleFirearmFramework)

# Hytale Firearm Framework (HFF)

**A modular, ECS-based framework for firearms, crossbows and ranged weapons in Hytale**

---

## Overview

**HFF** is a flexible framework designed to simplify the creation and management of **firearms, crossbows and other
ranged weapons** in Hytale.
It uses **Hytale's Entity Component System (ECS)** to provide a modular architecture for weapon behaviour, attachments,
animations, and physics.

---

## Key Features

- **Modular Weapon System**: Define weapons as combinations of components (e.g. barrels, magazines, scopes).
- **ECS Integration**: Build for Hytale's ECS architecture.
- **Customizable Attachments**: Supports scopes, silencers, grips, and more.
- **Physics & Ballistics**: Realistic projectile behaviour, recoil, and spread.
- **Animation Support**: Smooth animations for reloading, firing, and inspecting weapons.
- **Event-Driven**: Hook into weapon events (e.g., `onFire`, `onReload`) for custom logic.

---

## Installation

### 1. Download & Server Setup

1. **Download** the latest release from the [Releases](https://github.com/Tenebris-Lux/HytaleFirearmFramework/releases)
   page.
2. **Install** the `HFF.jar` on your Hytale Server (place it in the mods folder alongside your own mod).

### 2. Developer Setup (Dependency)

To use the framework in your code, add it as a dependency.

**Runtime Dependency (`manifest.json`)**
Add `HFF` to your mods `manifest.json` to ensure the server loads the framework before your mod starts.

```json
"Dependencies": {
"HFF": "^0.1.0"
}
```

**Build Dependency**
Add the library to your project to access the classes. (Replace TAG with the correct tag in each case.)

_Option A: Maven (`pom.xml`)_

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
<groupId>com.github.Tenebris-Lux</groupId>
<artifactId>HytaleFirearmFramework</artifactId>
<version>TAG</version>
<scope>provided</scope>
</dependency>
```

_Option B: Gradle (`build.gradle`)_

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Tenebris-Lux:HytaleFirearmFramework:TAG'
}
```

### 3. Initialisation

Configure the framework in your mod's initialisation method:

```java
 public class MyMod extends JavaPlugin {
    @Override
    protected void setup() {
        HFF.initialize();
    }
}
```

### 4. Start building your weapons

---

## Configuration

The framework uses a `hff_config.json` file to define where weapon stats are loaded from.
This file can be placed in **three locations** (and are loaded in this order):

1. **Inside your zip file** (in `/hff_config.json`)
2. **Hytale's main directory** (e.g., `%appdata%\Hytale`)
3. **Inside your JAR** (in `src/main/resources/hff_config.json`)

**Example `hff_config.json`**

```json
{
  "filePath": "HFF/",
  "archivePath": "mods/hff.jar",
  "pathInArchive": "HFF/",
  "archiveFirst": true
}
```

| Field           | Description                                                                                            |
|-----------------|--------------------------------------------------------------------------------------------------------|
| `filePath`      | Path to the directory containing weapon stat files **on the server**.                                  |
| `archivePath`   | Path to a archive containing weapon stat files (supports .jar and .zip).                               |
| `pathInArchive` | Path **inside the archive** to the stat files.                                                         |
| `archiveFirst`  | If `true`, the framework will first try to load stats from the archive before checking the filesystem. |

---

## Usage

1. **Create a Weapon**

   Define your weapon in a **JSON file** (e.g., `Hff_Firearm_Template.json`):
   ```json
   {
   "RPM": 500.0,
   "ProjectileVelocity": 10.0,
   "ProjectileAmount": 1,
   "SpreadBase": 0.1,
   "MovementPenalty": 0.5,
   "MisfireChance": 0.01,
   "JamChance": 0.005,
   "VerticalRecoil": 0.5,
   "HorizontalRecoil": 0.5
   }
   ```
   Place this file in:
    - `stats_path` (e.g., `MyMod/Weapons/Stats/Hff_Firearm_Template.json`) or
    - `fallback_path` (e.g., `items/Hff_Firearm_Template.json` inside the JAR).

2. **Customise Weapon Behaviour**

   Extend or override the default interactions to customise weapon behaviour.

---

## Project Structure

```
hff/
├── src/main/
|   ├── java/
|   |   ├── lucis/lux/
|   |   |   ├── components/                     # Components
|   |   |   ├── systems/                        # ECS systems
|   |   |   ├── util                            # Utiliary Classes
|   |   |   └── HFF.java                        # Plugin Setup
|   └── resources/
|       ├── Server/
|       |   ├── Item/Items
|       |   |   └── Hff_Firearm_Template.json   # Firearm template
|       |   └── Languages/en-US/
|       |       └── items.lang                  # Translation file
|       ├── hff_config.json                     # Fallback config file
|       └── manifest.json                       # Plugin configuration file
└── README.md

```

---

## Contributing

Contributions are welcome! Open a **Pull Request** or submit an **Issue** for bugs/feature requests.

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -am 'feat: Description of feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request

---

## FAQ

1. **How do I create my own weapon?**

   Create a JSON file in the `stats_path` directory (or `fallback_path` inside your JAR) and define the attributes.
   You additionally need an item asset with the same name as the stats file. Just copy the template and rename it.

2. **Why aren't my weapon stats loading?**
    - Check the path in `hff_config.json`.
    - Ensure the JSON file is correctly formatted.

3. **How can I extend the framework?**

   Create custom interactions and systems and register them in your plugin class.

---

## License

This project is licensed under the **MIT Licence**.

---

## Notes for users

- **Logging**: The framework logs where it searches for files. Check the logs if something isn't working.
- **Fallback Mechanism**: If a file isn't found in the filesystem, the framework automatically checks the JAR.