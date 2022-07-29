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
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class NetworkHandlerClient {

    public static void registerClientResponses(){
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SpawnBranchLightning, new handleSpawnedBranchLightning());
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SpawnFrostContact, new handleSpawnedFrostContact());
        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SpawnFlameContact, new handleSpawnedFlameContact());

        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.SpawnFlameAffectEntities, new handleSpawnedFlameAffectEntities());
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

    private static class handleSpawnedFrostContact implements PlayChannelHandler{

        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                PacketSender responseSender) {
                    
                    Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                    double  power = buf.readDouble();
            
                client.submit(() ->{
                    for (int i = 0; i <10 * power; i++){
                        Vec3d dir = Util.getRandomDirectionUnitSphere().multiply(power * 5);
                        client.world.addParticle(ParticleRegistery.CONTACT_FROST_PARTICLE,
                                            pos.getX(), pos.getY(), pos.getZ(), 
                                            dir.getX(), dir.getY(), dir.getZ());
                    }
                });
        }
    }

    private static class handleSpawnedFlameContact implements PlayChannelHandler{

        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                PacketSender responseSender) {
                    
                    Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                    double  power = buf.readDouble();
                    
            
                client.submit(() ->{

                    
                    for (int i = 0; i <10 * power; i++){
                        Vec3d dir = Util.getRandomDirectionUnitSphere().multiply(power * 10);
                        client.world.addParticle(ParticleRegistery.CONTACT_FLAME_PARTICLE,
                                            pos.getX(), pos.getY(), pos.getZ(), 
                                            dir.getX(), dir.getY(), dir.getZ());
                    }
                });
        }
    }

    private static class handleSpawnedFlameAffectEntities implements PlayChannelHandler{

        @Override
        public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
                PacketSender responseSender) {
                    
                    Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                    double  power = buf.readDouble();
                    int level = buf.readInt();

                client.submit(() ->{
                    DefaultParticleType type = level < 2 ? ParticleTypes.FLAME : ParticleRegistery.GREEN_SECONDARY_FLAME_PARTICLE;
                    for (int i = 0; i <30 * power; i++){
                        Vec3d dir = Util.getRandomDirectionUnitSphere().multiply(power * 0.2);
                        client.world.addParticle(type,
                                            pos.getX(), pos.getY(), pos.getZ(), 
                                            dir.getX(),dir.getY(), dir.getZ());
                    }
                });
        }
    }
}
