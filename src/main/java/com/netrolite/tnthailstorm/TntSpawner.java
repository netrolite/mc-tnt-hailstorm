package com.netrolite.tnthailstorm;

import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class TntSpawner {
  public void spawnTntOnInterval() {
    System.out.println("spawn tnt on interval called");
    ServerTickEvents.END_WORLD_TICK.register(
        (world) -> {
          if (!(world.getServer().getTicks() % 150 == 0)) return;

          List<ServerPlayerEntity> players = world.getPlayers();

          Random rand = new Random();
          int tntSpawnOffset = rand.nextInt(50);

          players.forEach(
              (player) -> {
                BlockPos playerPos = player.getBlockPos();
                BlockPos tntSpawnPos = playerPos.offset(player.getHorizontalFacing(), 5);
                TntEntity tnt = new TntEntity(EntityType.TNT, world);
                tnt.setPos(tntSpawnPos.getX(), tntSpawnPos.getY(), tntSpawnPos.getZ());
                world.spawnEntity(tnt);
              });
        });
  }
}
