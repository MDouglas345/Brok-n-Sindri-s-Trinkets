package net.fabricmc.Enchantments.FrostEnchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class FrostEnchantment extends Enchantment{

    /**
     * Need to find out how to make one enchantment require another0
     */
    
    public FrostEnchantment(EnchantmentTarget t) {
     
            super(Enchantment.Rarity.COMMON, t , new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    
    
}


