package net.fabricmc.Items;

import java.security.DrbgParameters.Reseed;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.DwarvenForgeBlock.DwarvenForgeBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

    
    public static void register(){
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "dwarven_forge_block"), new BlockItem(BNSCore.DWARVEN_FORGE_BLOCK, new FabricItemSettings().maxCount(1).group(ItemGroup.MISC)));
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "brok_spawn_egg"), SpawnEggs.BROK_SPAWN_EGG);
    }
}
