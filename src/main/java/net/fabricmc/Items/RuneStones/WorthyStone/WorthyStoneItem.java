package net.fabricmc.Items.RuneStones.WorthyStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.fabricmc.Util.Util;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class WorthyStoneItem extends RuneStoneItem {
    public WorthyStoneItem() {
        super();
        //TODO Auto-generated constructor stub
    }

    @Override
    public boolean enchantItem(ItemStack stack, PassiveDwarf dwarf){
        ItemGroup group = stack.getItem().getGroup();

        if (group.equals(ItemGroup.COMBAT)){
            stack = Util.setEnchantment(stack, BNSCore.WorthyWeapon, dwarf.improveLevel);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack = Util.setEnchantment(stack, BNSCore.WorthyTool, dwarf.improveLevel);
        }

        return true;
    }
}
