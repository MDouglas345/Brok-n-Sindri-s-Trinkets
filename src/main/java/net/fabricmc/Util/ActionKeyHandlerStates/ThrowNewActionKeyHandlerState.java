package net.fabricmc.Util.ActionKeyHandlerStates;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.ConfigRegistery;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.NetworkHandlerServer.handleActionKey;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class ThrowNewActionKeyHandlerState {
    // This is state 1
    public ThrowNewActionKeyHandlerState(handleActionKey master) {
       
        //TODO Auto-generated constructor stub
    }

    
    static public void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld) {
        // Create a new thrown generic item

        ItemStack itemstackOrig = player.getMainHandStack();

        if (!(itemstackOrig.getItem() instanceof SwordItem) && !(itemstackOrig.getItem() instanceof MiningToolItem)){
            return;
        }

        if (ConfigRegistery.configuration.getBoolean("ThrowEnchantment") && (EnchantmentHelper.getLevel(BNSCore.ThrowTool, itemstackOrig) == 0 && EnchantmentHelper.getLevel(BNSCore.ThrowWeapon, itemstackOrig) == 0)){
            return;
        }
				
        /**
         * Remove the idea of a "held time" and instead if the held time is over 15, the item is now torqued for
         * a max throw! Creates two finite states for a thrown item, normal throw and max throw. Also means that
         * thrown items. depending on stae, will have a fixed speed. This improves consistency.
         * 
         * normal throw - items will deflect off entities
         * 
         * max throw - items will be lodged into entities (only 1 item lodged in entity at time), if item has Pineed enchantment
         */

            
        
        GenericThrownItemEntity e = GenericThrownItemEntity.CreateNew(world, player, itemstackOrig, ticksHeld, false);
        
        if (!player.isCreative()){
            itemstackOrig.decrement(1);
        }
        
        ((LivingEntity)player).swingHand(Hand.MAIN_HAND, true);

        
        //client.getWorld().spawnEntity(e);
        world.spawnEntity(e);
        
    }
    
}
