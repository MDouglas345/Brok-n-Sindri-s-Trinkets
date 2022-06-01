package net.fabricmc.Util;

import net.minecraft.enchantment.Enchantment;

public class EnchantmentData {
    public Enchantment enchantment = null;
    public int level = 0;

    public EnchantmentData(Enchantment e, int l){
        enchantment = e;
        level = l;
    }
    
}
