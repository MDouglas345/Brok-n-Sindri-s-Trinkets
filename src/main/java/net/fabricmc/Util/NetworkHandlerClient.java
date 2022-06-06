package net.fabricmc.Util;

import java.util.List;

import com.google.common.graph.Network;

import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class NetworkHandlerClient {

    public static void registerClientResponses(){
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SpawnBranchLightning, new handleSpawnedBranchLightning());
    }

   

    

    


    private static class handleSpawnedBranchLightning implements PlayChannelHandler{

        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                PacketSender responseSender) {
                    
                    Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                    Vec3d randompos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            
                client.submit(() ->{
                        client.world.addParticle(ParticleRegistery.BRANCH_LIGHTNING_PARTICLE,
                                            pos.getX(), pos.getY(), pos.getZ(), 
                                            randompos.getX(), randompos.getY(), randompos.getZ());
                });
        }
    }
}
