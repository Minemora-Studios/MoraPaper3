package net.minemora;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MoraPaper {

    public static ServerPlayer createDummyServerPlayer(MinecraftServer server, ServerLevel level, GameProfile gameProfile) {
        return new ServerPlayer(server, level, gameProfile);
    }

    public static void startNearestPlayerUpdater(ServerLevel level) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            List<ServerPlayer> playersSnapshot = new ArrayList<>(level.players());

            double distance = Math.clamp(35-((15.0* playersSnapshot.size())/80), 20, 35);
            Mob.distanceSqrToDisableAi = (int) (distance*distance);

            for (Entity entity : level.getEntitiesAsync()) {
                if (entity instanceof Mob mob) {
                    updateNearestPlayerForMob(mob, playersSnapshot);
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private static void updateNearestPlayerForMob(Mob mob, List<ServerPlayer> players) {
        double minDistance = Double.MAX_VALUE;
        double minDy = Double.MAX_VALUE;
        double minDxSqr = Double.MAX_VALUE;
        double minDySqr = Double.MAX_VALUE;
        double minDzSqr = Double.MAX_VALUE;

        Player nearestPlayer = null;

        for (Player player : players) {
            if (!player.isSpectator() && player.affectsSpawning) {

                double dx = player.getX() - mob.getX();
                double dy = player.getY() - mob.getY();
                double dz = player.getZ() - mob.getZ();
                double dxSqr = dx * dx;
                double dySqr = dy * dy;
                double dzSqr = dz * dz;
                double distance = dxSqr + dySqr + dzSqr;

                if (distance < minDistance) {
                    minDistance = distance;
                    minDy = dy;
                    minDxSqr = dxSqr;
                    minDySqr = dySqr;
                    minDzSqr = dzSqr;

                    nearestPlayer = player;
                }
            }
        }

        mob.nearestPlayerData.uuid = nearestPlayer != null ? nearestPlayer.gameProfile.getId() : null;
        mob.nearestPlayerData.distanceSqr = minDistance;
        mob.nearestPlayerData.dy = Math.abs(minDy);
        mob.nearestPlayerData.dxSqr = minDxSqr;
        mob.nearestPlayerData.dySqr = minDySqr;
        mob.nearestPlayerData.dzSqr = minDzSqr;
    }

    public static class PlayerDistanceData {
        public @Nullable java.util.UUID uuid = null;
        public double distanceSqr = Double.MAX_VALUE;
        public double dy = Double.MAX_VALUE;
        public double dxSqr = Double.MAX_VALUE;
        public double dySqr = Double.MAX_VALUE;
        public double dzSqr = Double.MAX_VALUE;
    }
}
