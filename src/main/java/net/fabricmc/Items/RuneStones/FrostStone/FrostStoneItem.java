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
        enchantmentToUseWeapon = BNSCore.FrostWeapon;
        enchantmentToUseMiner = BNSCore.FrostTool;
    }

   
    
}
