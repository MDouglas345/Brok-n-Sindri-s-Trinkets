package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.Util.IMovementStopper;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements IMovementStopper {

    private boolean shouldmove = true;

    @Shadow private Input input;
    
    @Inject(at = @At("HEAD"), method = "tickMovement()V")
    public void stopInput(CallbackInfo info){
        IMovementStopper mover = (IMovementStopper)input;
        mover.setShouldMove(shouldmove);
  
    }

    @Override
    public void toggleShouldMove() {
        shouldmove = !shouldmove;
        
    }

    @Override
    public void setShouldMove(boolean move) {
        shouldmove = move;
    }
}
