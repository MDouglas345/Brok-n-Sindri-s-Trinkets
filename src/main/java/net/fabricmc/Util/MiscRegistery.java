package net.fabricmc.Util;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.ConfigReader;
import net.fabricmc.Config.ConfigRegistery;
import net.fabricmc.Enchantments.FlameEnchantment.FlameEnchantment;
import net.fabricmc.StaticFireBlock.StaticFireBlock;

public class MiscRegistery {

    public static void register(){
        ((StaticFireBlock)BNSCore.ADV_STATIC_FIRE_BLOCK).registerFireColor();
		((StaticFireBlock)BNSCore.BASE_STATIC_FIRE_BLOCK).registerFireColor();
        
        ((FlameEnchantment) BNSCore.FlameTool).setGreen(ConfigRegistery.configuration.getBoolean("GreenFire"));
        ((FlameEnchantment) BNSCore.FlameWeapon).setGreen(ConfigRegistery.configuration.getBoolean("GreenFire"));
    }
    
}
