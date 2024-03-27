package com.netrolite.tnthailstorm;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;

/** CustomTntEntity */
public class CustomTntEntity extends TntEntity {
  public CustomTntEntity(EntityType<? extends TntEntity> entityType, World world) {
    super(entityType, world);
  }

  public void explode() {
    float explosionPower = 8F;
    this.world.createExplosion(
        this,
        this.getX(),
        this.getBodyY(0.0625),
        this.getZ(),
        explosionPower,
        DestructionType.BREAK);
  }
}
