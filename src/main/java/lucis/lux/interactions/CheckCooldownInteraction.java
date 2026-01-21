package lucis.lux.interactions;


import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Map;

public class CheckCooldownInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<CheckCooldownInteraction> CODEC = BuilderCodec.builder(CheckCooldownInteraction.class, CheckCooldownInteraction::new, SimpleInstantInteraction.CODEC).build();


    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        interactionContext.getInteractionVars().forEach((key, value) -> {
            player.sendMessage(Message.raw(key + " : " + value));
        });

        Map<String, String[]> tags = this.data.getRawTags();
        tags.forEach((key, value) -> {
            player.sendMessage(Message.raw("Found a Tag: " + key));
        });

        if (cooldownHandler == null) {
            player.sendMessage(Message.raw("Cooldownhandler reports as null"));
            return;
        }
        CooldownHandler.Cooldown cooldown = cooldownHandler.getCooldown("Firearm_Cooldown");
        player.sendMessage(Message.raw("Cooldown max: " + (cooldown != null ? cooldown.getCooldown() : "0.0")));
    }
}
