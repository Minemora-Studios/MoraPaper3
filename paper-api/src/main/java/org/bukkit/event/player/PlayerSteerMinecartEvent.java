package org.bukkit.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Holds information for player movement events
 */
public class PlayerSteerMinecartEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Minecart vehicle;
    private boolean cancelled;

    @ApiStatus.Internal
    public PlayerSteerMinecartEvent(@NotNull final Player player, @NotNull final Minecart vehicle) {
        super(player);

        this.vehicle = vehicle;
    }

    /**
     * Retrieves the vehicle involved in the event.
     *
     * @return the vehicle entity associated with the event
     */
    public Minecart getVehicle() {
        return this.vehicle;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If a move or teleport event is cancelled, the player will be moved or
     * teleported back to the Location as defined by getFrom(). This will not
     * fire an event
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If a move or teleport event is cancelled, the player will be moved or
     * teleported back to the Location as defined by getFrom(). This will not
     * fire an event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
