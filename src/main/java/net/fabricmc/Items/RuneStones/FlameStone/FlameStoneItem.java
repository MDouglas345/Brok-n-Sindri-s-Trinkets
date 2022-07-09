package net.fabricmc.Items.RuneStones.FlameStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.fabricmc.Util.Util;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class FlameStoneItem extends RuneStoneItem {

    public FlameStoneItem() {
        super();
        //TODO Auto-generated constructor stub
    }

    @Override
    public boolean enchantItem(ItemStack stack, PassiveDwarf dwarf){
        ItemGroup group = stack.getItem().getGroup();

        if (Util.ContainsSpecialThrownEnchantment(stack)){
            return false;
        }

        if (group.equals(ItemGroup.COMBAT)){
            stack = Util.setEnchantment(stack, BNSCore.FlameWeapon, dwarf.improveLevel);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack = Util.setEnchantment(stack, BNSCore.FlameTool, dwarf.improveLevel);
        }

        return true;

        
    }
    
    
}
