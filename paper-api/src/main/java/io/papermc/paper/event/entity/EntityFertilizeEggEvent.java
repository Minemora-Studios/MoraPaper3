package io.papermc.paper.event.entity;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Called when two entities mate and the mating process results in a fertilization.
 * Fertilization differs from normal breeding, as represented by the {@link EntityBreedEvent}, as
 * it does not result in the immediate creation of the child entity in the world.
 * <p>
 * An example of this would be:
 * <ul>
 * <li>A frog being marked as "is_pregnant" and laying {@link Material#FROGSPAWN} later.</li>
 * <li>Sniffers producing the {@link Material#SNIFFER_EGG} item, which needs to be placed before it can begin to hatch.</li>
 * <li>A turtle being marked with "HasEgg" and laying a {@link Material#TURTLE_EGG} later.</li>
 * </ul>
 * <p>
 * The event hence only exposes the two parent entities in the fertilization process and cannot provide the child entity, as it will only exist at a later point in time.
 */
@NullMarked
public class EntityFertilizeEggEvent extends EntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final LivingEntity father;
    private final @Nullable Player breeder;
    private final @Nullable ItemStack bredWith;
    private int experience;

    private boolean cancelled;

    @ApiStatus.Internal
    public EntityFertilizeEggEvent(final LivingEntity mother, final LivingEntity father, final @Nullable Player breeder, final @Nullable ItemStack bredWith, final int experience) {
        super(mother);
        this.father = father;
        this.breeder = breeder;
        this.bredWith = bredWith;
        this.experience = experience;
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }

    /**
     * Provides the entity in the fertilization process that will eventually be responsible for "creating" offspring,
     * may that be by setting a block that later hatches or dropping an egg that has to be placed.
     *
     * @return The "mother" entity.
     */
    public LivingEntity getMother() {
        return this.getEntity();
    }

    /**
     * Provides the "father" entity in the fertilization process that is not responsible for initiating the offspring
     * creation.
     *
     * @return the other parent
     */
    public LivingEntity getFather() {
        return this.father;
    }

    /**
     * Gets the Entity responsible for fertilization. Breeder is {@code null} for spontaneous
     * conception.
     *
     * @return The Entity who initiated fertilization.
     */
    public @Nullable Player getBreeder() {
        return this.breeder;
    }

    /**
     * The ItemStack that was used to initiate fertilization, if present.
     *
     * @return ItemStack used to initiate fertilization.
     */
    public @Nullable ItemStack getBredWith() {
        return this.bredWith;
    }

    /**
     * Get the amount of experience granted by fertilization.
     *
     * @return experience amount
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * Set the amount of experience granted by fertilization.
     * If the amount is negative or zero, no experience will be dropped.
     *
     * @param experience experience amount
     */
    public void setExperience(final int experience) {
        this.experience = experience;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
