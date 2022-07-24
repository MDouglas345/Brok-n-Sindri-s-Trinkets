package net.fabricmc.Util.ActionKeyHandlerStates;

import java.util.UUID;

import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.WeaponStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.Util.IDedUUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RecallInitActionKeyHandlerState {
    
    static public void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld) {
        //UUIDStackComponent uuidstack = mycomponents.EntityUUIDs.get(world);
		//BlockPosStackComponent bpstack = mycomponents.BlockEntityPositions.get(world);

        WeaponStackComponent stack = mycomponents.WeaponStacks.get(world);
        IDedUUID entry = stack.Peek(player.getEntityName());

        if (entry == null){
            return;
        }

        if (entry.isBlock){
            RecallBEActionKeyHandler.handle(server, player, world, ticksHeld, stack, entry.pos);
        }
        else{
            RecallEntityActionKeyHandler.handle(server, player, world, ticksHeld, stack, entry);
        }
        /* 
		IDedUUID entityuuid = uuidstack.Peek(player.getEntityName());

        if (entityuuid != null){
            // handle entity recall
            RecallEntityActionKeyHandler.handle(server, player, world, ticksHeld, uuidstack, entityuuid);
            return;
        }

        // handle blockentity recall
        BlockPos pos = bpstack.Peek(player.getEntityName());
        RecallBEActionKeyHandler.handle(server, player, world, ticksHeld, bpstack, pos);
        */
    }
}
