package net.fabricmc.Items.RuneStones.FrostStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class FrostStoneItem extends RuneStoneItem{
    public FrostStoneItem(){
        super();
    }

    @Override
    public void enchantItem(ItemStack stack){
        ItemGroup group = stack.getItem().getGroup();

        if (group.equals(ItemGroup.COMBAT)){
            stack.addEnchantment(BNSCore.FrostWeapon, 1);
        }

        if (group.equals(ItemGroup.TOOLS)){
            stack.addEnchantment(BNSCore.FrostTool, 1);
        }
    }
    
}
