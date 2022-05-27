package net.fabricmc.Particles;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegistery {
    public static final DefaultParticleType FROST_PARTICLE = FabricParticleTypes.simple();


    public static void registerParticles(){
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "frost_particle"),
        FROST_PARTICLE);
    }
}
