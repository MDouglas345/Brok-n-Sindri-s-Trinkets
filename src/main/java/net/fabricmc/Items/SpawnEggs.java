package net.fabricmc.Items;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.EntityRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SpawnEggs {
    public static Item BROK_SPAWN_EGG = new SpawnEggItem(EntityRegistry.BROK, 0x0008b, 0xffd700, new Settings().maxCount(1).group(ItemGroup.MISC));
    public static Item SINDRI_SPAWN_EGG = new SpawnEggItem(EntityRegistry.SINDRI, 0xd4af37b, 0xffd700, new Settings().maxCount(1).group(ItemGroup.MISC));

}
