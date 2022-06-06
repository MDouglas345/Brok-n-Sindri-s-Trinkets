package net.fabricmc.Util.ActionKeyHandlerStates;

import java.util.UUID;

import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class RecallInitActionKeyHandlerState {
    
    static public void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld) {
        UUIDStackComponent uuidstack = mycomponents.EntityUUIDs.get(world);
		BlockPosStackComponent bpstack = mycomponents.BlockEntityPositions.get(world);

		UUID entityuuid = uuidstack.Peek(player.getEntityName());

        if (entityuuid != null){
            // handle entity recall
            RecallEntityActionKeyHandler.handle(server, player, world, ticksHeld, uuidstack, entityuuid);
            return;
        }

        // handle blockentity recall
        BlockPos pos = bpstack.Peek(player.getEntityName());
        RecallBEActionKeyHandler.handle(server, player, world, ticksHeld, bpstack, pos);

    }
}
