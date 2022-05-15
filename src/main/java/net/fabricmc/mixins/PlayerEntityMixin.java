package net.fabricmc.mixins;

import java.util.Stack;
import java.util.UUID;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Util.IPlayerEntityItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IPlayerEntityItems{

    /**
     * Need to store these stacks into the nbt for the player, and read them back
     * hope this doesnt eat up alot of memory...
     */
    private Stack<BlockPos>     ReturnableItemPositions     = new Stack<BlockPos>();
    private Stack<UUID>      ReturnableThrownItems          = new Stack<UUID>();

    @Override
    public void addReturnableItem(BlockPos pos){
        
        ReturnableItemPositions.push(pos);
    }

    @Override
    public void addReturnableThrownEntity(UUID i){
        ReturnableThrownItems.push(i);
    }


    @Override
    public BlockPos getReturnableItem(){
        return ReturnableItemPositions.pop();

    }

    @Override
    public UUID getReturnableThrownEntityID(){
        return ReturnableThrownItems.pop();
    }


    @Override
    public Stack<BlockPos> getStack(){
        return ReturnableItemPositions;
    }

@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void injectMethod(NbtCompound nbt, CallbackInfo info) {
       
            BNSCore.LOGGER.info("Infiltrated the player entity~");
        
    }
}
