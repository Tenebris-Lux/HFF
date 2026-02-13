package lucis.lux.hff.util;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * The {@code ComponentRefResult} records encapsulates the result of retrieving or creating a component
 * for an entity in the HFF (Hytale Firearm Framework) plugin. It provides access to the component itself,
 * its entity reference, and flags indicating whether the component was newly created or is disabled.
 *
 * <p>This record is typically used in utility classes like {@link EnsureEntity} to return the result
 * of ensuring that a component exists and is valid for a given entity or item.</p>
 *
 * <p>The record contains the following fields:</p>
 * <ul>
 *     <li>{@code component}: The component instance that was retrieved or created.</li>
 *     <li>{@code ref}: The reference to the entity that owns the component.</li>
 *     <li>{@code newlyCreated}: A flag g indicating whether the component was newly created during this operation.</li>
 *     <li>{@code disabled}: A flag indicating whether the component is currently disabled.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 *     ComponentRefResult&lt;FirearmStatsComponent&gt; result = EnsureEntity.get(interactionContext, FirearmStatsComponent.class);
 *     if (result.newlyCreated()) {
 *         // Handle newly created component
 *     }
 *     if (result.disabled()) {
 *         // Handle disabled component
 *     }
 * </pre>
 *
 * @param component    The component instance.
 * @param ref          The reference to the entity that owns the component.
 * @param newlyCreated Whether the component was newly created.
 * @param disabled     Whether the component is disabled.
 * @param <K>          The type of the component, which must extend {@link Component}.
 * @see EnsureEntity
 * @see Component
 * @see Ref
 */
public record ComponentRefResult<K extends Component<EntityStore>>
        (K component, Ref<EntityStore> ref, boolean newlyCreated, boolean disabled) {
}
