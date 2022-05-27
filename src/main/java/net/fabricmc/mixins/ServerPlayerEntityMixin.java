package net.fabricmc.mixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    
    @Inject(at = @At("HEAD"), method = "onDisconnect()V")
    public void onDisconnectHEAD(CallbackInfo info){
        BNSCore.LOGGER.info("In the head of onDisconnect");
        ISavedItem ISI = (ISavedItem)this;
        LivingEntity e = (LivingEntity)(Object)this;

        ItemStack item = ISI.getSavedItem();
        String name = ISI.getSavedItemOwner();

        if (item == null){return;}
        if (name == null || name == ""){return;}

        e.removeStatusEffect(BNSCore.Paralysis);

        if (!e.world.isClient){
            //e.world.getUserCache().findByName(playerName).get().getId()
            ServerWorld world = (ServerWorld)e.world;
            UUID playeruuid = world.getServer().getUserCache().findByName(ISI.getSavedItemOwner()).get().getId();
            
            //e.world.spawnEntity(GenericThrownItemEntity.CreateNew((ServerWorld)e.world, ISI.getSavedItemOwner(), playeruuid, e.getPos(), item));

            world.setBlockState(e.getBlockPos(),BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());
            GenericItemBlockEntity be = (GenericItemBlockEntity)world.getBlockEntity(e.getBlockPos());
            int id = BNSCore.pushBEOntoStack(world, name, e.getBlockPos());
            be.Initalize(item, new Quaternion(new Vec3f(0,0,0), 0, false), (float)Util.getRandomDouble(100, 200),  id, new ClientIdentification(name, playeruuid));
            
            BNSCore.removeEntityFromStack(world, name, ISI.getIndexIntoStack());

            ISI.reset();
        }
    }
}
