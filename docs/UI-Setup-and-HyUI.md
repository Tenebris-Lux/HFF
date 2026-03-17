# HFF Modder Guide: UI Setup & HyUI Integration

The **Hytale FirearmFramework (HFF)**  features a dynamic, fully functional Graphical User Interface (GUI). This includes a persistent **Ammo & fire mode HUD** and an interactive **Weapon Customization Menu**.

To bypass Hytale's native limitation of only allowing one single Custom HUD slot at a time, HFF relies on the powerful **HyUI** library. This ensures that our weapon HUD smoothly coexists with minimaps, RPG scoreboards, or other modded interfaces.

---

## 1- Setting up HyUI (For Developers)

If you are developing an addon for HFF or building your own modpack, you need to include HyUI in your project dependencies.

### Maven (`pom.xml`)

HFF uses CurseMaven to fetch the library directly from CurseForge:

```xml
<repositories>
    <repository>
        <id>cursemaven</id>
        <url>https://www.cursemaven.com</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>curse.maven</groupId>
        <artifacrId>hyui-1431415</artifacrId>
        <version>file-id</version>
    </dependency>
</dependencies>
```

### Required Folder Structure

Even though HFF generates its UI dynamically using Java strings (HYUML), HyUI requires the following resource folders to exist in your project for loading background patches, icons, and fonts:

```text
src/main/resources/
└── Common/
    └── UI/
        └── Custom/
            ├── Huds/
            └── Pages/
```

---

## 2. The Ammo HUD

The Ammo HUD sits in the bottom right corner of the screen and displays your current ammo count and fire mode (e.g., `30 / 30 [AUTO]`).

- **Automatic Display:** The HUD automatically opens when you equip an HFF-registered firearm.
- **Live Updates:** It instantly updates whenever you shoot, reload, or toggle your fire mode (`SELECT_FIRE`).
- **Multi-HUD Compatible:** Thanks to HyUI's `HudBuilder`, it will seamlessly stack with other HUDs without overwriting them.

---

## 3. The interactive Weapon Menu

The Weapon Menu is a full-screen, interactive page where players can view their weapon's live stats (RPM, Recoil, Capacity) and install attachments.

### Accessing the Menu

By default, the menu is bound to the `Ability1` interaction. To open in-game:

1. Hold a valid HFF firearm in your main hand.
2. Press the kex assigned to `Ability1` (usually 'Q').

### Installing Attachments

HFF uses a **Zero-Desync Inventory System**. To install an attachment (like a Red Dot Sight or a Silencer):

1. Place the attachment item into your **Utility Slot** (Offhand).
2. Open the Weapon Menu (`Ability1`).
3. Click the corresponding slot button (e.g., "Optic" or "Muzzle").
4. The attachment will be installed, the stats will update live, and the item will be removed from your Utility Slot.

To remove an attachment, simply empty your Utility Slot and click the attachment button in the menu again. The item will be safely returned to your main inventory.

---

## Important Note on Thread Safety

If you are extending HFF's UI logic via custom interactions, remember that UI events (like button clicks) or packet listeners (`SyncInteractionChains`) run on the **Network Thread**.

Whenever you want to modify a `FirearmState`, spawn items, or open a HyUI page, you **must** push the execution back to the Hytale **World Thread** using `world.execute()`:

```java
// Example: Safely opening the menu from a packet listener or custom event
playerRef.getStore().getExternalData().getWorld().execute(() -> {
    WeaponMenuPage.open(playerRef, store, weaponItem, stats, state);
});
```