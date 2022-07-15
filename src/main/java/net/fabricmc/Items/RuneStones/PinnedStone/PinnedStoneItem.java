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
        enchantmentToUseWeapon = BNSCore.PinnedWeapon;
        enchantmentToUseMiner = BNSCore.PinnedTool;
        //TODO Auto-generated constructor stub
    }

    
}
