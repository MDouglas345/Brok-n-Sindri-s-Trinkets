package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.Util.IMovementStopper;
import net.minecraft.client.input.KeyboardInput;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin implements IMovementStopper {
    
    private boolean shouldMove;

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    public void tick(boolean slowdown, CallbackInfo info){
        if (!shouldMove){
            info.cancel();
        }
    }

    @Override
    public void toggleShouldMove() {
        // TODO Auto-generated method stub
        shouldMove = !shouldMove;
    }

    @Override
    public void setShouldMove(boolean move) {
        // TODO Auto-generated method stub
        shouldMove = move;
    }
}
