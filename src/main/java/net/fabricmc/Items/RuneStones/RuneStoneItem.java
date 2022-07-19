package net.fabricmc.Items.RuneStones;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.ItemRegistry;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.fabricmc.Util.Util;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RuneStoneItem extends Item {

    protected Enchantment enchantmentToUseWeapon;
    protected Enchantment enchantmentToUseMiner;

    public RuneStoneItem() {
        super(new Settings().fireproof().group(ItemGroupRegistry.RUNE_STONE).maxCount(1));
        
    }

    

    public boolean enchantItem(ItemStack stack, PassiveDwarf dwarf){
        Item item = stack.getItem();

        if (Util.isItemValidWeapon(item)){
            stack = Util.setEnchantment(stack, enchantmentToUseWeapon, dwarf.improveLevel);
            return true;
        }

        if (Util.isItemValidMiner(item)){
            stack = Util.setEnchantment(stack, enchantmentToUseMiner, dwarf.improveLevel);
            return true;
        }

        return false;
    }
    
}
