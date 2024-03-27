package com.netrolite.tnthailstorm;

import com.netrolite.tnthailstorm.random.RandNum;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class TntSpawner {
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private static int tickCounter = 0;
  private static RandNum rand = new RandNum();
  private static int startHailstormAfterTicks = getHailstormWaitTimeTicks();

  public static void spawnTntOnInterval() {
    ServerTickEvents.END_WORLD_TICK.register(
        (world) -> {
          tickCounter++;
          if (tickCounter < startHailstormAfterTicks) return;
          tickCounter = 0;
          startHailstormAfterTicks = getHailstormWaitTimeTicks();
          ArrayList<String> playersToSkip = new ArrayList<>();

          getPlayers(world)
              .forEach(
                  (player) -> {
                    if (playersToSkip.contains(player.getUuidAsString())) return;

                    PlayerEntity closestPlayer = null;
                    Double closestPlayerDistance = Double.POSITIVE_INFINITY;
                    for (PlayerEntity p : getPlayers(world)) {
                      boolean isSelf = player.getUuidAsString().equals(p.getUuidAsString());
                      if (isSelf) continue;

                      double distance = player.distanceTo(p);
                      if (distance < closestPlayerDistance) {
                        closestPlayer = p;
                        closestPlayerDistance = distance;
                      }
                    }

                    if (closestPlayerDistance < 20) {
                      playersToSkip.add(closestPlayer.getUuidAsString());
                    }

                    for (int j = 0; j < 100; j++) {
                      scheduler.schedule(
                          () -> {
                            BlockPos playerPos = player.getBlockPos();

                            int tntSpawnOffsetX = rand.genInt(-10, 10);
                            int tntSpawnOffsetY = rand.genInt(5, 15);
                            int tntSpawnOffsetZ = rand.genInt(-10, 10);

                            BlockPos tntSpawnPos =
                                new BlockPos(
                                    playerPos.getX() + tntSpawnOffsetX,
                                    playerPos.getY() + tntSpawnOffsetY,
                                    playerPos.getZ() + tntSpawnOffsetZ);

                            boolean canSpawnTnt = world.getBlockState(tntSpawnPos).isAir();

                            while (!canSpawnTnt) {
                              tntSpawnPos = tntSpawnPos.up(1);
                              canSpawnTnt = world.getBlockState(tntSpawnPos).isAir();
                            }

                            final BlockPos finalTntSpawnPos = tntSpawnPos;
                            Entity tnt = new CustomTntEntity(EntityType.TNT, world);

                            tnt.setPos(
                                (double) finalTntSpawnPos.getX(),
                                (double) finalTntSpawnPos.getY(),
                                (double) finalTntSpawnPos.getZ());
                            tnt.setVelocity(
                                rand.genFloat((float) -1.5, (float) 1.5),
                                rand.genFloat((float) -1, (float) -0.1),
                                rand.genFloat((float) -1.5, (float) 1.5));
                            tnt.setUuid(UUID.randomUUID());

                            world.spawnEntity(tnt);
                          },
                          rand.genInt(0, 20),
                          TimeUnit.SECONDS);
                    }
                  });
          playersToSkip.clear();
        });
  }

  private static int getHailstormWaitTimeTicks() {
    return rand.genInt(800, 1200);
  }

  private static List<ServerPlayerEntity> getPlayers(ServerWorld world) {
    return world.getServer().getPlayerManager().getPlayerList();
  }
}
