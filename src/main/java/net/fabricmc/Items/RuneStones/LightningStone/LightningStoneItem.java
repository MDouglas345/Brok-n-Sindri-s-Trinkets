package net.fabricmc.Items.RuneStones.LightningStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class LightningStoneItem extends RuneStoneItem {

    public LightningStoneItem() {
        super();
        //TODO Auto-generated constructor stub
    }

    @Override
    public void enchantItem(ItemStack stack){
        ItemGroup group = stack.getItem().getGroup();

        if (group.equals(ItemGroup.COMBAT)){
            stack.addEnchantment(BNSCore.LightningWeapon, 1);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack.addEnchantment(BNSCore.LightningTool, 1);
        }
    }
    
}
