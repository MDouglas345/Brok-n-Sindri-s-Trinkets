package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.ConfigRegistery;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    
    @Inject(at = @At("TAIL"), method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;")
    public void place(ItemPlacementContext context, CallbackInfoReturnable info){
       // BNSCore.LOGGER.info(context.getPlayer().toString() + "Has placed a block!");
        World world = context.getWorld();
        if (!world.isClient && ConfigRegistery.configuration.getBoolean("AntiGrief")){
            BNSCore.pushPlayerBlock((ServerWorld) context.getWorld(), context.getBlockPos().toString(), true);
        }
    }
}
