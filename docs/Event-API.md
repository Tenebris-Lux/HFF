# HFF Event API Reference

Welcome to the **Hytale FirearmFramework (HFF) Event API.**
<br>HFF is designed to seamlessly integrate with other mods. Following Hytale's native event design, HFF utilizes the *
*Pre- / Post-Event Pattern**.

## 1. The Pre- / Post-Pattern

Whenever a significant action occurs, HFF fires two distinct events:

1. `Event.Pre`: Fired before the action happens. This event is **cancelable** and its properties are **mutable**. Use
   this to block actions (like safe zones) or modify data (like increasing damage).
2. `Event.Post`: Fired _after_ the action has successfully completed. This event is **not cancelable** and its data is *
   *read-only**. Use this to trigger subsequent logic, like awarding CP, playing custom sounds, or tracking statistics.

---

## 2. Available Events

All HFF events are located in the `lucis.lux.hff.events` package.

| Event Class          | Phase   | Cancellable? | Mutable? | Description                                                                     |              
|----------------------|---------|--------------|----------|---------------------------------------------------------------------------------|
| `FirearmShootEvent`  | `.Pre`  | Yes          | Yes      | Fired right before ammo is consumed and the projectile is spawned.              |
| `FirearmShootEvent`  | `.Post` | No           | No       | Fired right after the projectile has successfully left the barrel.              |
| `FirearmHitEvent`    | `.Pre`  | Yes          | Yes      | Fired right before damage is dealt to the target. Allows modyfing final damage. |
| `FirearmHitEvent`    | `.Post` | No           | No       | Fired after damage has successfully applied to the target.                      |
| `FirearmReloadEvent` | `.Pre`  | Yes          | Yes      | Fired when a relaod sequence is initiated.                                      |
| `FirearmReloadEvent` | `.Post` | No           | No       | Fired when the reload successfully finishes and ammo is in the magazine.        |
| `FirearmJamEvent`    | `.Pre`  | Yes          | Yes      | Fired when RNG dictates a jam. Canbe cancelled to prevent the jam.              |
| `FirearmJamEvent`    | `.Post` | No           | No       | Fired aftr the weapon state has been set to jammed.                             |

---

## 3. Listening to Events (Examples)

### Example 1: Creating a "Safe Zone" (`Pre` Event)

Use a `.Pre` event to intercept and cancel an action before it happens. Here, we prevent shooting inside a specified
area.

```java
package com.myplugin.safezones;

import com.hypixel.hytale.server.core.Message;
import lucis.lux.hff.events.ShootEvent;

public class SafeZoneListener {
    public static void onPlayerShootPre(ShootEvent.Pre event) {
        var player = event.getPlayerRef();

        if (SafeZoneManager.isInsideSafeZone(player)) {
            // Cancel the event
            event.setCancelled(true);
            player.sendMessage(Message.raw("You cannot shoot inside safe zones!"));
        }
    }
}
```

---

### Example 2: RPG Levelling System (`Pre` Event)

Use a `.Pre` event to modify data before the engine processes it. Here, we can increase bullet damage based on a
player's RPG stats.

```java
package com.myplugin.rpgsystem;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.DamageComponent;
import lucis.lux.hff.events.FirearmHitEvent;

public class MyEpgPlugin extends JavaPlugin {

    @Override
    public void setup() {
        // Register the HFF Event listener using Java Method References or Lambdas
        this.getEventRegistry().register(FirearmHitEvent.Pre.class, this::onProjectileHitPre);
    }

    public void onProjectileHitPre(FirearmHitEvent.Pre event) {
        if (event.getTarget() == null) return;

        var shooter = event.getShooter();
        if (shooter != null) {
            int agilityLevel = RgbDatabase.getAgility(shooter);

            // Calculate bonus damage (+2% per Agility level)
            float currentDamage = event.getFinalDamage();
            float bonusMultiplier = 1.0f + (agilityLevel * 0.02f);

            // Apply the new damage BEFORE HFF deals it to the entity
            event.setFinalDamage(currentDamage * bonusMultiplier);
        }
    }
}
```

---

### Example 3: Awarding Experience Points (`Post` Event)

Use a `.Post` event when you only want to react to a confirmed, _successful_ action. Here, we award XP only if the
bullet actually damaged an entity.

```java
package com.myplugin.rpgsystem;

import lucis.lux.hff.HFF;
import lucis.lux.hff.events.FirearmHitEvent;

public class XPListener {

    public void onProjectileHitPost(FirearmHitEvent.Post event) {
        // Since this is a Post event, we know the hit was not cancelled by other mods
        if (event.getTarget() != null && event.getShooter() != null) {

            // The data is read-only here, we just use it to calculate XP
            float damageDealt = event.getFinalDamage();
            int xpToAward = Math.round(damageDealt * 0.5f);

            RpgDatabase.addExperience(event.getShooter(), xpToAward);
        }
    }
}
```