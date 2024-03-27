package com.netrolite.tnthailstorm;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TntHailstorm implements ModInitializer {
  public static final String MOD_ID = "tnthailstorm";
  public static final Logger LOGGER = LoggerFactory.getLogger("tnthailstorm");

  @Override
  public void onInitialize() {
    TntSpawner.spawnTntOnInterval();
  }
}
