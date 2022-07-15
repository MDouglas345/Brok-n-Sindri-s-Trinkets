package net.fabricmc.Items.RuneStones.LightningStone;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.fabricmc.Util.Util;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class LightningStoneItem extends RuneStoneItem {

    public LightningStoneItem() {
        super();
        //TODO Auto-generated constructor stub
        enchantmentToUseWeapon = BNSCore.LightningWeapon;
        enchantmentToUseMiner = BNSCore.LightningTool;
    }

    
    
}
