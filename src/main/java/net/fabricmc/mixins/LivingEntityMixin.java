package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.security.auth.callback.Callback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.IMovementStopper;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.Living;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ISavedItem {

    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<String> OWNER = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.STRING);

    DataTracker dt;

    int IndexIntoStack = -1;
 

    @Inject(at = @At("HEAD"), method = "initDataTracker()V")
    public void initDataTrackerHEAD(CallbackInfo info){
        LivingEntity e = (LivingEntity)(Object)this;
        dt = e.getDataTracker();

        dt.startTracking(STACK, new ItemStack(Items.AIR));
        dt.startTracking(OWNER, "");
    }

    @Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V")
    public void onDeathHEAD(DamageSource soure, CallbackInfo info){
        ISavedItem ISI = (ISavedItem)this;
        LivingEntity e = (LivingEntity)(Object)this;

        ItemStack item = ISI.getSavedItem();
        String name = ISI.getSavedItemOwner();

        if (item == null){return;}
        if (name == null || name == ""){return;}

        if (!e.world.isClient){
            //e.world.getUserCache().findByName(playerName).get().getId()
            ServerWorld world = (ServerWorld)e.world;
            UUID playeruuid = world.getServer().getUserCache().findByName(ISI.getSavedItemOwner()).get().getId();
            
            e.world.spawnEntity(GenericThrownItemEntity.CreateNew((ServerWorld)e.world, ISI.getSavedItemOwner(), playeruuid, e.getPos(), item));

            BNSCore.removeEntityFromStack(world, name, ISI.getIndexIntoStack());
        }

       
        
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V")
    public void writeCustomDataToNbtHEAD(NbtCompound nbt, CallbackInfo info){
        NbtCompound item = new NbtCompound();
        ItemStack itemstack = this.getSavedItem();

        itemstack.writeNbt(item);
        nbt.put("SavedItem", item);

        nbt.putString("ItemOwner", this.getSavedItemOwner());
    }

    @Inject (at = @At("HEAD"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V")
    public void readCustomDataToNbtHEAD(NbtCompound nbt, CallbackInfo info){
        this.setSavedItem(ItemStack.fromNbt((NbtCompound)nbt.get("SavedItem")));
        this.setSavedItemOwner(nbt.getString("ItemOwner"));
    }

    @Override
    public void setSavedItem(ItemStack stack) {
        

        dt.set(STACK, stack);
        
    }

    @Override
    public ItemStack getSavedItem() {
        // TODO Auto-generated method stub
        return this.dt.get(STACK);
    }

    @Override
    public void setSavedItemOwner(String name) {
        this.dt.set(OWNER, name);
        
    }

    @Override
    public String getSavedItemOwner() {
        // TODO Auto-generated method stub
        return this.dt.get(OWNER);
    }

    @Override
    public void setIndexIntoStack(int i) {
        this.IndexIntoStack = i;
        
    }

    @Override
    public int getIndexIntoStack() {
        // TODO Auto-generated method stub
        return this.IndexIntoStack;
    }
  
    
}
