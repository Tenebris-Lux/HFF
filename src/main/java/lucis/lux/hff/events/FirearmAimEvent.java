package lucis.lux.hff.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public abstract class FirearmAimEvent implements IEvent<Void> {
    protected final Ref<EntityStore> player;
    protected final ItemStack weapon;
    protected final boolean isAiming;

    protected FirearmAimEvent(Ref<EntityStore> player, ItemStack weapon, boolean isAiming){
        this.player = player;
        this.weapon = weapon;
        this.isAiming = isAiming;
    }

    public Ref<EntityStore> getPlayer() {
        return player;
    }

    public ItemStack getWeapon() {
        return weapon;
    }

    public boolean isAiming() {
        return isAiming;
    }

    public static class Pre extends FirearmAimEvent implements ICancellable{

        private boolean cancelled = false;

        public Pre(Ref<EntityStore> player, ItemStack weapon, boolean isAiming) {
            super(player, weapon, isAiming);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean b) {
            this.cancelled = b;
        }
    }

    public static class Post extends FirearmAimEvent{

        public Post(Ref<EntityStore> player, ItemStack weapon, boolean isAiming) {
            super(player, weapon, isAiming);
        }
    }
}
