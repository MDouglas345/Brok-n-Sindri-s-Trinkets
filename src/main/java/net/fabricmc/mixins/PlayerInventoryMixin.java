package net.fabricmc.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    
    @Shadow private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow public PlayerEntity player;

    public List<ItemStack> SavedItems = new ArrayList<>();


    @Inject(at = @At("HEAD"), method = "dropAll()V")
    public void dropAllHead(CallbackInfo info) {
        BNSCore.LOGGER.info("In the tail of dropAll");
        BNSCore.LOGGER.info(combinedInventory.toString());
        PlayerInventory pi = (PlayerInventory)(Object)this;
        for (DefaultedList<ItemStack> list : combinedInventory){

            for (int i = 0; i < list.size(); ++i) {
                ItemStack stack = (ItemStack)list.get(i);
               
                if (EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, stack) >= 1 || EnchantmentHelper.getLevel(BNSCore.WorthyTool, stack) >= 1){
                    //SavedItems.add(stack.copy());
                    pi.player.world.spawnEntity(GenericThrownItemEntity.CreateNew((ServerWorld) pi.player.world, pi.player, stack, 1, true));
                    BNSCore.LOGGER.info(stack.toString());
                    list.set(i, ItemStack.EMPTY);
                }
            }

        } 
    }

    @Inject(at = @At("TAIL"), method = "dropAll()V")
    public void dropAllTail(CallbackInfo info) {
        BNSCore.LOGGER.info("In the head of dropAll");
        PlayerInventory pi = (PlayerInventory)(Object)this;

        for (ItemStack item : this.SavedItems){
            BNSCore.LOGGER.info(item.toString());
            pi.player.getInventory().insertStack(item);
        }
         
        
        
    }
}



