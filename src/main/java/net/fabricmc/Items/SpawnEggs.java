package net.fabricmc.Items;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.EntityRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpawnEggs {
    public static Item BROK_SPAWN_EGG = Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "brok_spawn_egg"), 
                                                            new SpawnEggItem(
                                                                EntityRegistry.BROK, 0x3b3635, 0x948e8d, 
                                                                new FabricItemSettings().group(ItemGroup.MISC).maxCount(1)));
}
