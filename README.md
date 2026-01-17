# Hytale Firearm Framework (HFF)

**A modular, ECS-based framework for firearms, crossbows and ranged weapons in Hytale**

---

## Overview

**HFF** is a flexible, **Entity Component System (ECS)**-compatible framework designed to simplify the creation and management of firearms, crossbows and other ranged weapons in **Hytale**. It provides a modular architecture for weapon behavior, attachments, animations, and physics, making it easy to integrate into custom mods or game projects.

---

## Features

- **Modular Weapon System**: Define weapons as combinations of components (e.g. barrels, magazines, scopes).
- **ECS Integration**: Build for Hytale's ECS architecture.
- **Customizable Attachments**: Supports scopes, silencers, grips, and more.
- **Physics & Ballistics**: Realistic projectile behavior, recoil, and spread.
- **Animation Support**: Smooth animations for reloading, firing, and inspecting weapons.
- **Event-Driven**: Hook into weapon events (e.g., `onFire`, `onReload`) for custom logic.

---

## Installation

1. **Download** the latest release from the [Releases](https://github.com/Tenebris-Lux/HytaleFirearmFramework/releases) page.
2. **Add the JAR** to your Hytale mod project.
3. **Configure** the framework in your mod's initialization:

    ```java
    HFF.initialize();
    ```

4. **Start building** your weapons!

---

## Usage

1. **Create Weapons** as Assets with the help of the given Template.
2. **Customize Behavior** by overriding default events and/or systems.

---

## Customization

### Components

Extend or create new components:

```java
public class CustomScopeComponent extends AttachmentComponent {
    public CustomScopeComponent(float zoomLevel){
        super(zoomLevel);
    }
}
```

---

## Project Structure

```
hff/
├── src/main/
|   ├── java/
|   |   ├── lucis/lux/
|   |   |   ├── components/                     # Components
|   |   |   ├── systems/                        # ECS systems
|   |   |   └── HFF.java                        # Plugin Setup
|   └── resources/
|       ├── Common/
|       |   ├── Blocks/                         # Block assets
|       |   ├── BlockTextures/                  # Texture assets
|       |   └── Icons/                          # Icon assets
|       ├── Server/
|       |   ├── Item/Items
|       |   |   └── template_firearm.json       # Firearm template
|       |   └── Languages/en-US/
|       |       └── items.lang                  # Translation file
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

## License

This project is licensed under the **MIT Licence**.