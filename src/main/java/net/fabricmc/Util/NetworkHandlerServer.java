package net.fabricmc.Util;

import java.util.List;

import net.fabricmc.Util.ActionKeyHandlerStates.InitActionKeyHandlerState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NetworkHandlerServer {

    public static void registerServerResponses(){
        ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.EstablishThrownItem, new handleActionKey());
    }

    public static void spawnBranchLightning(ServerWorld world, Vec3d pos, Vec3d Target){
        PacketByteBuf createPacket = PacketByteBufs.create();

        createPacket.writeDouble(pos.x);
        createPacket.writeDouble(pos.y);
        createPacket.writeDouble(pos.z);

        createPacket.writeDouble(Target.x);
        createPacket.writeDouble(Target.y);
        createPacket.writeDouble(Target.z);

        List<ServerPlayerEntity> players = world.getPlayers(player -> (player.getPos().distanceTo(pos) <= 50));

        for (ServerPlayerEntity player : players){
            ServerPlayNetworking.send(player, NetworkConstants.SpawnBranchLightning, createPacket);
        }
    }

    public static void spawnFrostContact(ServerWorld world, Vec3d pos, double power){
        PacketByteBuf createPacket = PacketByteBufs.create();

        createPacket.writeDouble(pos.x);
        createPacket.writeDouble(pos.y);
        createPacket.writeDouble(pos.z);

        createPacket.writeDouble(power);
        

        List<ServerPlayerEntity> players = world.getPlayers(player -> (player.getPos().distanceTo(pos) <= 50));

        for (ServerPlayerEntity player : players){
            ServerPlayNetworking.send(player, NetworkConstants.SpawnFrostContact, createPacket);
        }
    }

    public static void spawnFlameContact(ServerWorld world, Vec3d pos, double power){
        PacketByteBuf createPacket = PacketByteBufs.create();

        createPacket.writeDouble(pos.x);
        createPacket.writeDouble(pos.y);
        createPacket.writeDouble(pos.z);

        createPacket.writeDouble(power);
        

        List<ServerPlayerEntity> players = world.getPlayers(player -> (player.getPos().distanceTo(pos) <= 50));

        for (ServerPlayerEntity player : players){
            ServerPlayNetworking.send(player, NetworkConstants.SpawnFlameContact, createPacket);
        }
    }

    public static void spawnFlameAffectingEntities(ServerWorld world, Vec3d pos, double power, int level){
        PacketByteBuf createPacket = PacketByteBufs.create();

        createPacket.writeDouble(pos.x);
        createPacket.writeDouble(pos.y);
        createPacket.writeDouble(pos.z);

        createPacket.writeDouble(level < 2 ? 1.8 : 3);
        createPacket.writeInt(level);
        

        List<ServerPlayerEntity> players = world.getPlayers(player -> (player.getPos().distanceTo(pos) <= 50));

        for (ServerPlayerEntity player : players){
            ServerPlayNetworking.send(player, NetworkConstants.SpawnFlameAffectEntities, createPacket);
        }
    }
    public static class handleActionKey implements PlayChannelHandler{
        /**
         * Approach to make this clean and dry:
         * 
         * Nested classes?  ehh might look more confusing.
         * A State machine... (most likely)
         * 
         * State interface :
         * MinecraftServer, ServerPlayerEntity
         * 
         * 
         * 
         * 
         * Tests to ensure functionality and consitency
         * 
         * Throw and recall weapon before it lands
         * Throw weapon and die. Respanw and recall to ensure it returns
         * 
         * Thrown Weapon close, let it land, and recall to ensure automatic insertion into player inventory
         * Throw weapon far (Several Chunks), let it land, and recall to ensure the thrown item entity is created correctly and actually returns
         * Throw weapon close, let it land, die, respawn and recall item. It should still return
         * Throw weapon far (Several Chunks), let it land, die, respawn and recall ite. It should still return
         * 
         * Pin entity that is close, recall. It should automaticall insert into player inventory
         * Pin entity that is medium distance. Recall. It should return
         * Pin entity that is far (Several Chunks). Recall. It should return
         * Pin entity, die, respawn and recall. It Should return
         *
         */
        @Override
        public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                PacketByteBuf buf, PacketSender responseSender) {
            // TODO Auto-generated method stub
            
            float timeHeld = (float)buf.readByte();
            

            server.submit(() -> {
                ServerWorld world = getClientWorld(server, player);
                InitActionKeyHandlerState.handle(server, player, world, timeHeld);
            });
        }

        private ServerWorld getClientWorld(MinecraftServer server, ServerPlayerEntity player){
            return server.getWorld(player.world.getRegistryKey());
        }
        
    }
}
