package net.fabricmc.Items.ItemGroup;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Items.ItemRegistry;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {
    public static ItemGroup RUNE_STONE;
    public static ItemGroup DWARVEN_BLOCKS;


    public static void register(){
        RUNE_STONE = FabricItemGroupBuilder.create(new Identifier(BNSCore.ModID, "rune_stones"))
                                .icon(() -> new ItemStack(Items.DIAMOND_BLOCK)).build();

        DWARVEN_BLOCKS = FabricItemGroupBuilder.create(new Identifier(BNSCore.ModID, "dwarven_blocks"))
                                .icon(() -> new ItemStack(BNSCore.DWARVEN_FORGE_BLOCK)).build();
    }
    
}
