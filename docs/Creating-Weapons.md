# HFF Modder Guide: Creating Weapons, Ammo & Attachments

Welcome to the **Hytale Firearm Framework (HFF)**! This guide will show you, how to create fully functional, highly detailed firearms using only Hytale's native JSON files.

HFF uses custom **Item Components** (`hff:firearm_stats`, `hff:ammo`, `hff:attachment`) to turn standard Hytale items into complex weapons. To use this feature place the weapons and ammunition in the `HFF/Items` folder and the associated assets in `HFF/Assets`.

---

## 1. Creating a Firearm

To create a firearm, you need to add the `hff:firearm_stats` component to your item's JSON file. This defines how the weapon behaves before any attachments are added.

### Example JSON (`HFF/Items/Weapons/MyAssaultRifle.json`)

```json
{
  "TranslationProperties": {
    "Name": "M4A1 Assault Rifle"
  },
  "Categories": [
    "Items.HFF.Weapons"
  ],
  "MaxStack": 1,
  "Components": {
    "hff:firearm_stats": {
      "calibre": "5.56x45mm",
      "fireMode": "SELECT_FIRE",
      "firearmClass": "MODERN",
      "rpm": 800.0,
      "reloadTimeSeconds": 2.5,
      "projectileCapacity": 30,
      "projectileVelocity": 150.0,
      "optimalRange": 50.0,
      "maxRange": 150.0,
      "minDamageMultiplier": 0.3,
      "spreadBase": 0.2,
      "movementPenalty": 1.5,
      "verticalRecoil": 0.8,
      "horizontalRecoil": 0.3,
      "jamChance": 0.001,
      "misfireChance": 0.0
    }
  }
}
```

### Property Breakdown

| Property                  | Description                                                                                          | Example                  |
|---------------------------|------------------------------------------------------------------------------------------------------|--------------------------|
| `calibre`                 | The ammo type this weapon accepts.                                                                   | `"9x19mm"`, `"12-gauge"` |
| `fireMode`                | The firing behaviour. Options: `AUTOMATIC`, `SEMI_AUTOMATIC`, `BURST`, `SELECT_FIRE`, `MANUAL`, etc. | `"AUTOMATIC"`            |
| `rpm`                     | Rounds per minute. Determines the delay between shots.                                               | `600.0`                  |
| `projectileCapacity`      | Magazine size.                                                                                       | `30`                     |
| `projectileAmount`        | Bullets fired per trigger pull (e.g., 8 for a shotgun).                                              | `1`                      |
| `optimalRange`/`maxRange` | Distance in blocks before damage falloff starts/ends.                                                | `50.0`/`150.0`           |
| `minDamageMultiplier`     | The remaining damage percentage (0.0 to 1.0) at `maxRange`.                                          | `0.3` (30% damage)       |
| `jamChance`               | Probability (0.0 to 1.0) of a weapon jam per shot. Cleared by pressing Reload.                       | `0.005` (0.5%)           |

---

## 2. Registering the Interactions (CRITICAL)

For HFF to control your weapon, you **must** bind the item's inputs to HFF's Java interaction classes. Add this `Interactions` block to your weapon's JSON:

```json
  "Interactions": {
    "Primary": {
      "Interactions": [
        {
          "Type": "hff:check_cooldown",
          "CancelOnItemChange": true,
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
    "Ability1": {
      "Interactions": [
        {
          "Type": "hff:openHFFMenu",
          "CancelOnItemChange": true
        }
      ]
    },
    "Ability2": {
      "Interactions": [
        {
          "Type": "hff:toggleFireMode",
          "CancelOnItemChange": true
        }
      ]
    },
    "Ability3": {
      "Interactions": [
        {
          "Type": "hff:reload",
          "CancelOnItemChange": true,
          "HorizontalSpeedMultiplier": 0.5
        }
      ]
    }
  }
```

- **Primary (Left Click):** Shoots the weapon.
- **Secondary (Right Click):** Toggles Aim Down Sights (ADS).
- **Ability1 ('Q' Key):** Opens the interactive Attachment Menu (HyUI).
- **Ability2 ('E' Key):** Toggles the fire mode of arms with `SELECT_FIRE`.
- **Ability3 ('R' Key):** Reloads the weapon or clears a jam.

*(Note: all of this keys depend on the users control configuration and can be changed in the users settings.)*

---

## 3. Creating Ammunition

Ammunition requires the `hff:ammo` component.<br>
**Important:** The Ammo *Item* (what sits in your inventory) defines the damage, but it references a *Projectile Entity* (the physical bullet that flies through the air).

### Example JSON (`HFF/Items/Ammo/556_Tracer.json`)

```json
{
  "TranslationProperties": {
    "Name": "5.56x45mm Tracer Rounds"
  },
  "MaxStack": 60,
  "Components": {
    "hff:ammo": {
      "calibre": "5.56x45mm",
      "projectileId": "hff_tracer_projectile",
      "damage": 25.0
    }
  }
}
```

*(Note: To reload, the player must place this item in their **Utility Slot**!)*

---

## 4. Creating Attachments

Attachments modify a weapon's stats dynamically. They use the `hff:attachment` component. Multipliers scale the weapon's base stats (e.g., `0.8` means a 20% reduction, `1.1` means a 10% increase).

### Example JSON (`HFF/Items/Attachments/Silencer.json`)

```json
{
  "TranslationProperties": {
    "Name": "Tactical Suppressor"
  },
  "MaxStack": 1,
  "Components": {
    "hff:attachment": {
      "type": "MUZZLE",
      "recoilMultiplier": 0.85,
      "spreadMultiplier": 0.9,
      "velocityMultiplier": 0.95,
      "reloadTimeMultiplier": 1.0,
      "rpmMultiplier": 1.0,
      "extraMagazineCapacity": 0
    }
  }
}
```

### Attachment Types (Slots)

Valid `type`values for attachments are:

- `MUZZLE` (Silencers, Compensators)
- `OPTIC` (Red Dots, Scopes)
- `UNDERBARREL` (Grips, Bipods)
- `MAGAZINE` (Extended Mags)
- `STOCK` (Shoulder Stocks)

To apply an attachment, place it in the Utility Slot, press the `Q` key (`ability1`) while holding your weapon to open the HFF Menu, and click the corresponding slot!