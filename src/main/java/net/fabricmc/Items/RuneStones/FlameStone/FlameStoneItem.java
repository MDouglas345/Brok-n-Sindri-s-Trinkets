package net.fabricmc.Items.RuneStones.FlameStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class FlameStoneItem extends RuneStoneItem {

    public FlameStoneItem() {
        super();
        //TODO Auto-generated constructor stub
    }

    @Override
    public void enchantItem(ItemStack stack){
        ItemGroup group = stack.getItem().getGroup();

        if (group.equals(ItemGroup.COMBAT)){
            stack.addEnchantment(BNSCore.FlameWeapon, 1);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack.addEnchantment(BNSCore.FlameTool, 1);
        }
    }
    
    
}
