package net.fabricmc.Items;

import java.security.DrbgParameters.Reseed;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.DwarvenForgeBlock.DwarvenForgeBlock;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.fabricmc.Items.RuneStones.RuneStoneItemRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

    
    public static void register(){
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "dwarven_forge_block"), new BlockItem(BNSCore.DWARVEN_FORGE_BLOCK, new FabricItemSettings().maxCount(1).group(ItemGroupRegistry.DWARVEN_BLOCKS)));

        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "brok_spawn_egg"), SpawnEggs.BROK_SPAWN_EGG);
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "sindri_spawn_egg"), SpawnEggs.SINDRI_SPAWN_EGG);
        
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "lightning_stone"), RuneStoneItemRegistry.LIGHTNING_STONE);
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "flame_stone"), RuneStoneItemRegistry.FLAME_STONE);
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "frost_stone"), RuneStoneItemRegistry.FROST_STONE);
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "worthy_stone"), RuneStoneItemRegistry.WORTHY_STONE);
        Registry.register(Registry.ITEM, new Identifier(BNSCore.ModID, "pinned_stone"), RuneStoneItemRegistry.PINNED_STONE);
    }
}
