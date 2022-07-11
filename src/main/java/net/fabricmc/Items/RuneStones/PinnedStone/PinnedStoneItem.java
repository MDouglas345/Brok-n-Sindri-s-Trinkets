package net.fabricmc.Items.RuneStones.PinnedStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.fabricmc.Util.Util;
import net.minecraft.entity.passive.PassiveEntity.PassiveData;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class PinnedStoneItem extends RuneStoneItem {
    public PinnedStoneItem() {
        super();
        //TODO Auto-generated constructor stub
    }

    @Override
    public boolean enchantItem(ItemStack stack, PassiveDwarf dwarf){
        ItemGroup group = stack.getItem().getGroup();

        if (group.equals(ItemGroup.COMBAT)){
            stack = Util.setEnchantment(stack, BNSCore.PinnedWeapon, dwarf.improveLevel);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack = Util.setEnchantment(stack, BNSCore.PinnedTool, dwarf.improveLevel);
        }

        return true;
    }
}
