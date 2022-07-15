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
        enchantmentToUseWeapon = BNSCore.FlameWeapon;
        enchantmentToUseMiner = BNSCore.FlameTool;
    }

    
    
}
