# HFF Architecture & Data Flow Overview

Welcome to the architectural documentation of the **Hytale Firearm Framework (HFF)**.
This document explains the core design patterns, data flow, and lifecycle of firearms within the framework. HFF is
designed to be **highly performant, data-driven, and modular**,
strictly adhering to Hytale's ECS (Entity Component System) and interaction paradigms.

---

## 1. Core Design: The Flyweight Pattern

In games with thousands of potential items, storing the full data of a weapon on every single item instance in the world
would lead to massive memory bloat and desyncs. HFF solves this using the **Flyweight Patter**.

We strictly separate **Intrinsic (Static) Data** from **Extrinsic (Dynamic) Data**:

### A. Intrinsic Data: `FirearmStats` & Registries

When the Hytale server starts, HFF parses all JSON item files in `HFF` directories. It extracts the `hff:firearm_stats`,
`hff:ammo`, and `hff:attachment` components and stores them in static, memory-efficient Maps:

- `FirearmRegistry`
- `AmmoRegistry`
- `AttachmentRegistry`

These objects (e.g., `FirearmStats`) are **100% immutable**. A standard M4A1 assault rifle will always have a base RPM
of 800 here.

### B. Extrinsic Data: `FirearmState`

When a player picks up or equips a weapon for the first time, HFF generates a unique `UUID` and attaches it to the
Hytale `ItemStack` metadata.
This UUID maps to a `FirearmSTate` object in the `FirearmStateManager`.<br>
The `FirearmState` tracks the _current_ condition of this specific weapon instance:

- `loadedProjectiles` (LinkedList of ammo IDs currently in the magazine)
- `activeAttachments` (Map of currently installed attachments)
- `isJammed` (Boolean)
- `currentFireMode` (Enum)

**Result:** The Hytale ItemStack only stores a tiny UUID, while HFF handles the complex logic in the backend.

---

## 2. The StatCalculator (Dynamic Modification)

How do attachments change weapon behaviour? HFF never alters the base `FirearmStats` in the Registry. Instead, it uses
the `StatCalculator` at runtime.

Before a weapon is fired or its stats are displayed in the UI, the `StatCalculator` is called:

1. It retrieves the immutable `FirearmStats` from the Registry.
2. It retrieves the `FirearmState` (installed attachments)
3. It multiplies all relevant values (e.g., `BaseRecoil * OpticModifier * MuzzleModifier`).
4. It returns a temporary, calculated `FirearmStats` object that is used solely for this specific action.

---

## 3. The Lifecycle of a Shot

Understanding how a bullet travels from a mouse click to hitting a target is crucial for modding HFF.

### Step 1: Input & Interaction (`ShootFirearmInteraction`)

- The player presses the Primary Button (Left click).
- Hytale natively checks the `Interactions` block of the held item and triggers `ShootFirearmInteraction`.
- HFF checks the `FirearmState` for jamming, burst status, and loaded ammo.
- The next projectile ID is consumed from the `LinkedList` magazine.

### Step 2: Spawning the Projectile

- HFF looks up the `AmmoData` in the `AmmoRegistry` using the consumed projectile ID.
- The physical Hytale projectile entity is spawned via the `ProjectileModule`.

### Step 3: The Data Backpack (`DamageCOmponent`)

- Crucially, HFF attaches a custom component to the newly spawned flying projectile entity.
- This `DamageComponent` acts as a backpack containing the bullet's base damage, optimal range, and maximum range.

### Step 4: Hit Detection (`HitEnemyInteraction`)

- When the projectile collides with a block or entity, Hytale triggers the `HitEnemyInteraction`.
- HFF opens the "backpack" (`DamageComponent`).
- It calculates the distance flown (from start position to hit position).
- It mathematically reduces the damage based on the distance.
- Finally, it sends the calculated damage to Hytale's `DamageSystems`.

---

## 4. UI Architecture (HyUI)

HFF uses **HyUI** to bypass Hytale's single Custom-HUD limitation and to provide a rich HTML/CSS (HYUML) layout system.

- **HUD (`HFFUiManager`:** Uses HyUI's `HudBuilder` to render a non-intrusive Ammo & Firemode counter. it updates
  dynamically via ID targeting.
- **Interactive Menus (`WeaponMenuPage`):** Uses HyUI's `PageBuilder` to create full-screen, interactive customization
  screens.
- **Zero-Desync Inventory:** To prevent item duplication glitches, HFF dies _not_ use drag-and-drop UI slots for
  attachments. Instead, players place the attachment in their standard Hytale **Utility Slot** and click a button in the
  HyUI interface to install it. HFF safely consumes the item via backend logic.

  <br>**Thread Safety Warning for Developers:** Hytale's UI interactions and packet listeners (`SyncInteractionChains`)
  run on the Network Thread. Whenever you modify inventory, edit a `FirearmState`, or spawn entities, you **must** wrap
  logic in `world.execute(() -> { ... });` to push it to the world thread.