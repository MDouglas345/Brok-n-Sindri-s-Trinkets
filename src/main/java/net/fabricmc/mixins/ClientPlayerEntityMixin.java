package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.Util.IClientPlayerEntity;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements IClientPlayerEntity {

    private boolean shouldmove = true;
    
    @Inject(at = @At("HEAD"), method = "tickMovement()V", cancellable = true)
    public void stopInput(CallbackInfo info){
        if (!shouldmove){
            info.cancel();;
        }
  
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
