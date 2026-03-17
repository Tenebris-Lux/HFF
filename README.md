[![Release](https://jitpack.io/v/Tenebris-Lux/HytaleFirearmFramework.svg)](https://jitpack.io/#Tenebris-Lux/HytaleFirearmFramework)

# Hytale Firearm Framework (HFF)

**A high-performance framework for firearms, crossbows, and ranged weapons in Hytale.**

---

## Overview

**HFF** is a flexible framework designed to simplify the creation and management of **firearms, crossbows and other
ranged weapons** in Hytale.
Instead of relying on heavy ECS entities for inventory items, HFF uses a highly optimized **Flyweight Architecture**:

- **Static weapon stats** (e.g., RPM, recoil) are stored centrally in registries.
- **Dynamic item states** (e.g., ammo queues) are stored as a reference to the specific item stacks.
- **Result:** Maximum server performance with support for complex mechanics like mixed-ammo magazines and advanced
  ballistics.

---

## Key Features

| Feature                    | Description                                                            |
|----------------------------|------------------------------------------------------------------------|
| **Flyweight Architecture** | Seperates static and dynamic reloadEventData for optimal memory usage. |
| **Advanced Ammo System**   | Supports mixed magazines (e.g., alternating tracer/AP rounds).         |
| **Custom Attachments**     | Modify weapon stats dynamically using built-in record builders.        |
| **Physics & Ballistics**   | Realistic projectile behaviour, recoil, and spread.                    |
| **Event-Driven**           | Hook into weapon events (e.g., `onFire`, `onReload`) for custom logic. |
| **Animation Support**      | Smooth animations for reloading, firing, and inspecting weapons.       |

---

## Installation

### For Users

1. **Download** the latest release from [Releases](https://github.com/Tenebris-Lux/HFF/releases).
2. Place `HFF.jar` in your server's `mod/` folder.

### For Developers

Add HFF as a dependency to your project:

**Maven (`pom.xml`)**

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
<groupId>com.github.Tenebris-Lux</groupId>
<artifactId>HFF</artifactId>
<version>0.3.0-Testing</version>
<scope>provided</scope>
</dependency>
```

**Gradle (`build.gradle`)**

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Tenebris-Lux:HFF:0.3.0-Testing'
}
```

---

## Usage

**Create a Weapon**

Define your weapon in a **JSON file** (e.g., `Hff_Firearm_Template.json`):

   ```json
   {
  "Name": "hff_gun_template",
  "TranslationProperties": {
    "Name": "item.hff.gun_template.name",
    "Description": "item.hff.gun_template.desc"
  },
  "Categories": [
    "Items.HFF"
  ],
  "Icon": "Icons/ItemsGenerated/Weapon_Gun_Blunderbuss.png",
  "Model": "Items/Weapons/Gun/Blunderbuss.blockymodel",
  "Texture": "Items/Weapons/Gun/Blunderbuss_Texture.png",
  "Rarity": "Technical",
  "MaxStack": 1,
  "PlayerAnimationsId": "Rifle",
  "IconProperties": {
    "Scale": 0.38,
    "Translation": [
      -14,
      -25
    ],
    "Rotation": [
      320,
      90,
      0
    ]
  },
  "Components": {
    "hff:firearm_stats": {
      "reloadTimeSeconds": 2.5,
      "rpm": 600.0,
      "projectileVelocity": 100.0,
      "projectileAmount": 1,
      "projectileCapacity": 10,
      "spreadBase": 0.1,
      "movementPenalty": 0.5,
      "misfireChance": 0.0,
      "jamChance": 0.0,
      "verticalRecoil": 0.5,
      "horizontalRecoil": 0.2,
      "firearmClass": "OTHER",
      "firearmType": "OTHER",
      "fireMode": "OTHER",
      "disabled": false,
      "caliber": "default"
    }
  },
  "Interactions": {
    "Primary": {
      "Interactions": [
        {
          "Type": "hff:check_cooldown",
          "Next": {
            "Type": "hff:shoot_firearm"
          },
          "Failed": "Simple"
        }
      ]
    },
    "Secondary": {
      "Interactions": [
        {
          "Type": "hff:toggle_aim"
        }
      ]
    },
    "Use": {
      "Interactions": [
        {
          "Type": "hff:reload"
        }
      ]
    }
  }
}

```

Place this file in `src/main/[yourMod]/resources/HFF/Items/`.

**Configuration**

`hff_config.json` Example

```json
{
  "filePath": "MyMod/HFF/Items/",
  "archivePath": "mods/my_mod.jar",
  "pathInArchive": "HFF/Items/",
  "archiveFirst": true,
  "debugMode": true
}
```

| Option        | Description                                |
|---------------|--------------------------------------------|
| filePath      | Path to weapon stats in the filesystem.    |
| archivePath   | Path to your mod JAR.                      |
| pathInArchive | Path to weapon stats inside the JAR.       |
| archiveFirst  | Prioritize resources from the JAR if true. |
| debugMode     | Enable detailed logs for debugging.        |

---

## Project Structure

```text
hff/
├── src
│ └── main
│   ├── java
│   │ └── lucis
│   │     └── lux
│   │         └── hff
│   │             ├── commands
│   │             │ ├── ShowFirearmRegistryCommand.java
│   │             │ ├── ShowProjectilesCommand.java
│   │             │ └── ShowUUIDCommand.java
│   │             ├── components
│   │             │ ├── AimComponent.java
│   │             │ ├── interfaces
│   │             │ │ └── FirearmAttachment.java
│   │             │ └── ReloadingComponent.java
│   │             ├── reloadEventData
│   │             │ ├── AmmoData.java
│   │             │ ├── AmmoRegistry.java
│   │             │ ├── ConfigManager.java
│   │             │ ├── FirearmRegistry.java
│   │             │ ├── FirearmState.java
│   │             │ ├── FirearmStateManager.java
│   │             │ ├── FirearmStats.java
│   │             │ └── HFFAssetPackGenerator.java
│   │             ├── enums
│   │             │ ├── FirearmClass.java
│   │             │ ├── FirearmType.java
│   │             │ └── FireMode.java
│   │             ├── events
│   │             │ ├── OnCheckTimeout.java
│   │             │ └── OnShoot.java
│   │             ├── HFF.java
│   │             ├── interactions
│   │             │ ├── CheckCooldownInteraction.java
│   │             │ ├── ReloadInteraction.java
│   │             │ ├── ShootFirearmInteraction.java
│   │             │ └── ToggleAimInteraction.java
│   │             ├── listeners
│   │             │ └── FirearmUuidInitializer.java
│   │             ├── network
│   │             ├── storage
│   │             │ └── FirearmStateStorage.java
│   │             ├── systems
│   │             │ ├── AimSystem.java
│   │             │ └── ReloadSystem.java
│   │             ├── ui
│   │             │ ├── AimHUD.java
│   │             │ └── EmptyHUD.java
│   │             └── util
│   └── resources
│       ├── Common
│       │ └── UI
│       │     └── Custom
│       │         └── HUD
│       │             ├── aim.ui
│       │             └── crosshair.png
│       ├── HFF
│       │ ├── Assets
│       │ │ ├── Icons
│       │ │ ├── Models
│       │ │ ├── Sounds
│       │ │ └── Textures
│       │ ├── Items
│       │ │ ├── hff_Ammunition_Template.json
│       │ │ └── hff_Firearm_Template.json
│       │ └── ProjectileConfigs
│       │     └── hff_Example_Projectile.json
│       └── manifest.json       # Plugin configuration file
└── README.md

```

---

## FAQ

Q: **How do I create a weapon with mixed ammo?**

A: You don't have to implement anything. Just prepare the ammunition in the hotbar before reloading.

Q: **Why aren't my weapon stats loading?**
A:

- Check the path in `hff_config.json`.
- Verify JSON syntax and file locations.

Q: **How do I add custom attachments?**

A: Not yet! (っ- ‸ - ς)

---

## Contributing

Contributions are welcome! Open a **Pull Request** or submit an **Issue** for bugs/feature requests.

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-idea`).
3. Commit your changes (`git commit -am 'feat: Description of feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request

---

## License

This project is licensed under the [**MIT Licence**](./LICENSE).

---

## Notes for users

- **Logging**: The framework logs where it searches for files. Check the logs if something isn't working.
- **Fallback Mechanism**: If a file isn't found in the filesystem, the framework automatically checks the JAR.

## Attributions

<a href="https://www.flaticon.com/free-icons/crosshair" title="crosshair icons">Crosshair icons created by Metami
septiana - Flaticon</a>
