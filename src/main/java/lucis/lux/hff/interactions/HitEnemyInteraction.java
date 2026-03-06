package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.DamageComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class HitEnemyInteraction extends SimpleInstantInteraction {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<HitEnemyInteraction> CODEC = BuilderCodec.builder(HitEnemyInteraction.class, HitEnemyInteraction::new, SimpleInstantInteraction.CODEC).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> projectile = interactionContext.getEntity();
        Ref<EntityStore> owner = interactionContext.getOwningEntity();
        Ref<EntityStore> target = interactionContext.getTargetEntity();

        DamageComponent damage = interactionContext.getCommandBuffer().getComponent(projectile, HFF.get().getDamageComponentType());

        if (damage != null) {
            float finalDamage = damage.getDamage() * HFF.get().getConfigData().getGlobalDamageMultiplier();

            Damage.ProjectileSource source = new Damage.ProjectileSource(owner, projectile);
            DamageCause cause = DamageCause.getAssetMap().getAsset("Projectile");
            Damage damageObj = new Damage(source, cause, finalDamage);

            DamageSystems.executeDamage(target, interactionContext.getCommandBuffer(), damageObj);
        }
    }
}
