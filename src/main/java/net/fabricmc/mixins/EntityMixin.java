package net.fabricmc.mixins;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.IDedUUID;
import net.fabricmc.Util.ISavedItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "discard()V")
    public void discardHEAD(CallbackInfo info){
        Entity entity = (Entity)(Object)this;
        if (entity.world.isClient){return;}

        if (entity instanceof LivingEntity){
            /**
             * if the entity is being discarded, check for a saved item and chuck it somewhere safe
             */
            ISavedItem ent = (ISavedItem)(Object)this;
            if (ent.getSavedItem().getItem().equals(Items.AIR)){
                return;
            }
            
            GenericThrownItemEntity thrownentity = GenericThrownItemEntity.CreateNew((ServerWorld) entity.world, ent.getSavedItemOwner(), null, entity.getPos(), ent.getSavedItem());
            thrownentity.throwRandomly();
            entity.world.spawnEntity(thrownentity);

            BNSCore.removePinnedEntity((ServerWorld)entity.world,entity.getUuid());

            BNSCore.pushEntityOntoStack((ServerWorld)entity.world, ent.getSavedItemOwner(), new IDedUUID(0, thrownentity.getUuid(), thrownentity.getBlockPos()));
        }
    }
}
