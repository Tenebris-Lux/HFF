# HFF Master JSON Cheat Sheet

This cheat sheet contains all properties, data types, and default values for creating content in the **Hytale Firearm
Framework (HFF)**

## 1. Firearm Stats (`hff:firearm_stats`)

Added to the `Components` block of your weapon item. Defines the base behaviour of the gun before any attachments are
applied.

| Property              | Type    | Default     | Description                                                          | Examples                  |
|-----------------------|---------|-------------|----------------------------------------------------------------------|---------------------------|
| `firearmClass`        | String  | `"OTHER"`   | Categorization for events/weather.                                   | `MODERN`, `MATCHLOCK`     |
| `firearmType`         | String  | `"OTHER"`   | General category of the weapon.                                      | `SHOTGUN`, `REVOLVER`     |
| `fireMode`            | String  | `"OTHER"`   | The mode of fire for the firearm.                                    | `SEMI_AUTOMATIC`, `BURST` |
| `calibre`             | String  | `"default"` | The ammo ID string required to reload this weapon.                   | `5.56x45mm`               |
| `rpm`                 | Float   | `1.0`       | Rounds Per Minute. Dictates firing speed / cooldown.                 | `600.0`                   |
| `reloadTime`          | Float   | `1.0`       | Time it takes to complete a reload/unjam action in seconds.          | `2.5`                     |
| `projectileCapacity`  | Integer | `1`         | Maximum amount of ammunition the (internal) magazine can hold.       | `30`                      |
| `projectileAmount`    | Integer | `1`         | Projectiles spawned per shot.                                        | `8` for shotguns          |
| `projectileVelocity`  | Float   | `1.0`       | Base velocity multiplier (scales the ProjectileConfig LaunchForce).  | `150.0`                   |
| `spreadBase`          | Float   | `0.0`       | Base inaccuracy of the weapon (0.0 = perfect accuracy).              | `0.1`                     |
| `movementPenalty`     | Float   | `0.0`       | Additional spread added while the player is moving.                  | `0.5`                     |
| `verticalRecoil`      | Float   | `0.0`       | Upward camera kick per shot.                                         | `0.5`                     |
| `horizontalRecoil`    | Float   | `0.0`       | Sideways camera kick per shot.                                       | `0.2`                     |
| `jamChance`           | Float   | `0.0`       | Probability (0.0 - 1.0) of a mechanical jam per shot.                | `0.005`                   |
| `misfireChance`       | Float   | `0.0`       | Probability (0.0 - 1.0) of a click-no-shoot (e.g., wet blackpowder). | `0.02`                    |
| `optimalRange`        | Float   | `15.0`      | Distance (blocks) before damage falloff begins.                      | `30.0`                    |
| `maxRange`            | Float   | `50.0`      | Distance (blocks) where minimum damage is reached.                   | `100`                     |
| `minDamageMultiplier` | Float   | `0.2`       | The percentage of damage (0.0 - 1.0) remaining at `maxRange`.        | `0.01`                    |

---

## 2. Ammunition (`hff:ammo`)

Added to the `Components` block of an item. Represents the magazine/bullet in the inventory. To reload, players place
this item in their Utility slot.

| Property       | Type   | Default                | Description                                              | Examples    |
|----------------|--------|------------------------|----------------------------------------------------------|-------------|
| `calibre`      | String | `"default"`            | Must match the `calibre` of the weapon.                  | `5.56x45mm` |
| `projectileId` | String | `"Example_Projectile"` | The id of the Hytale ProjectileConfig Asset to spawn.    | `"tracer"`  |
| `damage`       | Float  | `1.0`                  | Base damage dealt on impact (before falloff/multipliers) | `25.0`      |

---

## 3. Attachments (`hff:attachments`)

Added to the `Components` block of an item. Dynamically scales weapon stats.

| Property                | Type    | Default   | Description                                                      | Examples |
|-------------------------|---------|-----------|------------------------------------------------------------------|----------|
| `type`                  | String  | `"OPTIC"` | The slot: `MUZZLE`, `OPTIC`, `UNDERBARREL`, `MAGAZINE`, `STOCK`. | `MUZZLE` |
| `recoilMultiplier`      | Float   | `1.0`     | Multiplies vertical and horizontal recoil.                       | `0.85`   |
| spreadMultiplier        | Float   | `1.0`     | Multiplies base spread.                                          | `0.8`    |
| `velocityMultiplier`    | Float   | `1.0`     | Multiplies bullet speed.                                         | `1.1`    |
| `reloadTimeMultiplier`  | Float   | `1.0`     | Multiplies reload time.                                          | `0.95`   |
| `rpmMultiplier`         | Float   | `1.0`     | Multiplies firing speed.                                         | `1.2`    |
| `extraMagazineCapacity` | Integer | `0`       | _Flat addition_ to maximum ammo capacity.                        | `10`     |

---

## 4. Hytale Projectile Configuration (`ProjectileConfig`)

This is a **native Hytale Asset** (located in `HFF/ProjectileConfigs/{id}.json`). The `hff:ammo` component references
this file via `projectileId`.

**Critical:** For HFF to calculate damage and falloff, the `ProjectileImpact` interaction must point to the HFF Hit
Interaction!

### Core Properties

| Property                 | Type   | Example                | Description                                  |
|--------------------------|--------|------------------------|----------------------------------------------|
| `Model`                  | String | `"Bullet_Blunderbuss"` | The visual 3D model of the bullet in flight. |
| `LaunchForce`            | Float  | `100.0`                | Initial speed of the projectile.             |
| `ProjectileSoundEventId` | String | `"bullet_whiz"`        | Looping sound attached to the flying bullet. |

### Interactions Block (Linking to HFF)

To ensure HFF handles the damage correctly, map the impact event to your HFF Hit Interaction:

```json
{
  "Interactions": {
    "ProjectileHit": {
      "Interactions": [
        {
          "Type": "hff:hitEnemy"
        },
        "Common_Projectile_Despawn"
      ]
    },
    "ProjectileMiss": {
      "Interactions": [
        "Common_Projectile_Despawn"
      ]
    }
  }
}
```

### Physics Block (`"Type": "Standard"`)

| Property              | Type    | Example            | Description                                                        |
|-----------------------|---------|--------------------|--------------------------------------------------------------------|
| `Gravity`             | Float   | `9.8`              | Downward acceleration (Use `0.0`for lasers/magic`)                 |
| `Density`             | Float   | `700.0`            | Mass of the projectile (affects drag).                             |
| `TerminalVelocityAir` | Float   | `150.0`            | maximum speed the bullet can dravel in the air.                    |
| `Bounciness`          | Float   | `0.0`              | Energy retained on bounce (0.0 - 1.0). Use `0.8` for grenades.     |
| `BounceCount`         | Integer | `-1`               | Max bounces before despawn (-1 = unlimited).                       |
| `SticksVertically`    | Boolean | `true`             | If true, sticks to walls (great for arrows).                       |
| `ComputeYaw`/`Pitch`  | Boolean | `true`             | Automatically rotates the bullet model to face its flight path.    |
| `RotationMode`        | String  | `"VelocityDamped"` | How rotation is calculated (`VelocityDamped`, `Velocity`, `None`). |