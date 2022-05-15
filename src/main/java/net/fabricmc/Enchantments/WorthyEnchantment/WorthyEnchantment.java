package net.fabricmc.Enchantments.WorthyEnchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class WorthyEnchantment extends Enchantment{

    
    
    public WorthyEnchantment(EnchantmentTarget t) {
     
            super(Enchantment.Rarity.COMMON, t , new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    
    
}


