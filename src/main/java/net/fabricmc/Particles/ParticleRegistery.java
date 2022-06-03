package net.fabricmc.Particles;

import net.fabricmc.Particles.ContactFrostParticle.ContactFrostParticle;
import net.fabricmc.Particles.FrostParticle.FrostParticle;
import net.fabricmc.Particles.TrailingFrostParticle.TrailingFrostParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleRegistery {
    public static final DefaultParticleType FROST_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType CONTACT_FROST_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType TRAILING_FROST_PARTICLE = FabricParticleTypes.simple();


    public static void registerParticles(){
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "frost_particle"),
                                FROST_PARTICLE);
        
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "contact_frost_particle"),
        CONTACT_FROST_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "trailing_frost_particle"),
        TRAILING_FROST_PARTICLE);
    }


    public static void registerClientSideParticles(){
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.FROST_PARTICLE, FrostParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.CONTACT_FROST_PARTICLE, ContactFrostParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.TRAILING_FROST_PARTICLE, TrailingFrostParticle.Factory::new);
    }
}
