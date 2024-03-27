package com.netrolite.tnthailstorm;

import com.netrolite.tnthailstorm.random.RandNum;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TntSpawner {
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private static int ticks = 0;
  private static RandNum rand = new RandNum();
  private static int startHailstormAfterTicks = genHailstormAfterTicks();
  private static boolean hasWarned = false;

  public static void spawnTntOnInterval() {
    ServerTickEvents.END_WORLD_TICK.register(
        (world) -> {
          ticks++;
          warnAboutHailstorm(world);
          if (ticks < startHailstormAfterTicks) return;
          // reset some variables on hailstorm start
          ticks = 0;
          hasWarned = false;
          startHailstormAfterTicks = genHailstormAfterTicks();

          System.out.println("will start hailstorm after " + startHailstormAfterTicks + " ticks");

          ArrayList<String> playersToSkip = new ArrayList<>();
          System.out.println("getPlayers(world) " + getPlayers(world));
          System.out.println("playersToSkip " + playersToSkip);

          getPlayers(world)
              .forEach(
                  (player) -> {
                    System.out.println("playersToSkip " + playersToSkip);
                    System.out.println("curPlayer " + player.getUuidAsString());
                    System.out.println(
                        "will skip? " + playersToSkip.contains(player.getUuidAsString()));
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

                    System.out.println("closestPlayer " + closestPlayer);
                    System.out.println("closestPlayerDistance " + closestPlayerDistance);

                    if (closestPlayerDistance < 20) {
                      System.out.println("closest player is close enough");
                      playersToSkip.add(closestPlayer.getUuidAsString());
                    }

                    for (int j = 0; j < 40; j++) {
                      scheduler.schedule(
                          () -> {
                            System.out.println("scheduling tnt callback");
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
                              System.out.println("canSpawnTnt " + canSpawnTnt);
                            }
                            System.out.println("canSpawnTnt " + canSpawnTnt);

                            final BlockPos finalTntSpawnPos = tntSpawnPos;
                            Entity tnt = new TntEntity(EntityType.TNT, world);

                            tnt.setPos(
                                (double) finalTntSpawnPos.getX(),
                                (double) finalTntSpawnPos.getY(),
                                (double) finalTntSpawnPos.getZ());
                            tnt.setVelocity(
                                rand.genFloat((float) -1.5, (float) 1.5),
                                rand.genFloat((float) -1, (float) -0.1),
                                rand.genFloat((float) -1.5, (float) 1.5));
                            System.out.println("tnt before spawning " + tnt);
                            System.out.println("isClient before spawning " + world.isClient());

                            boolean spawnResult = world.spawnEntity(tnt);
                            System.out.println("spawnResult " + spawnResult);
                          },
                          rand.genInt(0, 20),
                          TimeUnit.SECONDS);
                    }
                  });
          playersToSkip.clear();
        });
  }

  private static int genHailstormAfterTicks() {
    return rand.genInt(2400, 3600);
  }

  private static void warnAboutHailstorm(ServerWorld world) {
    System.out.println("warnAboutHailstorm:");
    System.out.println("ticks " + ticks);
    System.out.println("startHailstormAfterTicks " + startHailstormAfterTicks);
    if (hasWarned) return;
    if (ticks >= (startHailstormAfterTicks - 80)) {
      List<ServerPlayerEntity> players = getPlayers(world);
      players.forEach(
          player -> {
            player.networkHandler.sendPacket(
                new TitleS2CPacket(Text.translatable("hailstorm_approaching")));
          });
      hasWarned = true;
    }
  }

  private static List<ServerPlayerEntity> getPlayers(ServerWorld world) {
    return world.getServer().getPlayerManager().getPlayerList();
  }
}
