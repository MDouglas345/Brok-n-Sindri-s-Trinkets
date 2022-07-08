package net.fabricmc.Items.RuneStones.PinnedStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class PinnedStoneItem extends RuneStoneItem {
    public PinnedStoneItem() {
        super();
        //TODO Auto-generated constructor stub
    }

    @Override
    public void enchantItem(ItemStack stack){
        ItemGroup group = stack.getItem().getGroup();

        if (group.equals(ItemGroup.COMBAT)){
            stack.addEnchantment(BNSCore.PinnedWeapon, 1);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack.addEnchantment(BNSCore.PinnedTool, 1);
        }
    }
}
