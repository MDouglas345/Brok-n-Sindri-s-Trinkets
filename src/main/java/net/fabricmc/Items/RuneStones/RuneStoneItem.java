package net.fabricmc.Items.RuneStones;

import net.fabricmc.Items.ItemRegistry;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.minecraft.item.Item;

public class RuneStoneItem extends Item {

    public RuneStoneItem() {
        super(new Settings().fireproof().group(ItemGroupRegistry.RUNE_STONE).maxCount(1));
        
    }
    
}
