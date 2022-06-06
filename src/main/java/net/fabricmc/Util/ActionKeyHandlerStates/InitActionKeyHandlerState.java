package net.fabricmc.Util.ActionKeyHandlerStates;

import net.fabricmc.Util.NetworkHandlerServer.handleActionKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class InitActionKeyHandlerState {
    // This is state 0
   

   
    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld) {
        // TODO Auto-generated method stub
        if (ticksHeld > 5){
            // Move to handle a new thrown item
            ThrowNewActionKeyHandlerState.handle(server, player, world, ticksHeld);
            return;
        }
        // Move to handle a recall request
        RecallInitActionKeyHandlerState.handle(server, player, world, ticksHeld);
    }
    
}
