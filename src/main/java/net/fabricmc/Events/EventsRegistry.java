package net.fabricmc.Events;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;

/**
 * this is an optimzation start! Search for all GTIE in an unloaded chunk and turn them into GIB!
 */
public class EventsRegistry {
    public static void register(){
        ServerChunkEvents.CHUNK_UNLOAD.register((server, chunk) ->{
            //BNSCore.LOGGER.info(chunk.getWorld().getRegistryKey().toString() + chunk.getPos().toString() +  " unloaded");
        });

        ServerChunkEvents.CHUNK_LOAD.register((server, chunk) ->{
            //BNSCore.LOGGER.info(chunk.getWorld().getRegistryKey().toString() + chunk.getPos().toString() +  " loaded");
        });

    }
}
