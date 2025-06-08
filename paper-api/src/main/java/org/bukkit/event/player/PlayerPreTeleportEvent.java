package org.bukkit.event.player;

import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * MoraPaper - An event called when the teleport is triggered, before it being canceled
 * by a passenger, etc.
 */
public class PlayerPreTeleportEvent extends PlayerMoveEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Set<TeleportFlag> teleportFlags = new HashSet<>();
    private final PlayerTeleportEvent.TeleportCause cause;

    @ApiStatus.Internal
    public PlayerPreTeleportEvent(@NotNull final Player player, @NotNull final Location from, @Nullable final Location to, @NotNull final PlayerTeleportEvent.TeleportCause cause) {
        super(player, from, to);
        this.cause = cause;
    }

    /**
     * Gets the cause of this teleportation event
     *
     * @return Cause of the event
     */
    @NotNull
    public PlayerTeleportEvent.TeleportCause getCause() {
        return this.cause;
    }

    public void addFlag(TeleportFlag flag) {
        this.teleportFlags.add(flag);
    }

    public Set<TeleportFlag> getFlags() {
        return this.teleportFlags;
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
