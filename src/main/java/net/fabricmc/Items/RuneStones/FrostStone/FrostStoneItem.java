package net.fabricmc.Items.RuneStones.FrostStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.fabricmc.Util.Util;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class FrostStoneItem extends RuneStoneItem{
    public FrostStoneItem(){
        super();
    }

    @Override
    public boolean enchantItem(ItemStack stack, PassiveDwarf dwarf){
        ItemGroup group = stack.getItem().getGroup();

        if (Util.ContainsSpecialThrownEnchantment(stack)){
            return false;
        }

        if (group.equals(ItemGroup.COMBAT)){
            stack = Util.setEnchantment(stack, BNSCore.FrostWeapon, dwarf.improveLevel);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack = Util.setEnchantment(stack, BNSCore.FrostTool, dwarf.improveLevel);
        }

        return true;
    }
    
}
