package com.netrolite.tnthailstorm.item;

import com.netrolite.tnthailstorm.TntHailstorm;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems {
  public static final Item BOMB =
      registerItem(
          "bomb",
          new Item(
              new FabricItemSettings()
                  .maxCount(10)
                  .rarity(Rarity.EPIC)
                  .group(ItemGroup.COMBAT)
                  .fireproof()));

  private static Item registerItem(String name, Item item) {
    Identifier id = new Identifier(TntHailstorm.MOD_ID, name);
    return Registry.register(Registry.ITEM, id, item);
  }

  public static void registerModItems() {
    TntHailstorm.LOGGER.info("Registering mod items");
  }
}
