package net.fabricmc.Particles;

import net.fabricmc.Particles.ContactFrostParticle.ContactFrostParticle;
import net.fabricmc.Particles.FlameParticle.FlameParticle;
import net.fabricmc.Particles.BranchLightningParticle.BranchLightningParticle;
import net.fabricmc.Particles.ContactFlameParticle.ContactFlameParticle;
import net.fabricmc.Particles.FrostParticle.FrostParticle;
import net.fabricmc.Particles.GreenFlameParticle.GreenFlameParticle;
import net.fabricmc.Particles.GreenSecondaryFlame.GreenSecondaryFlameParticle;
import net.fabricmc.Particles.GreenTrailingFlameParticle.GreenTrailingFlameParticle;
import net.fabricmc.Particles.LightningParticle.LightningParticle;
import net.fabricmc.Particles.TrailingFlameParticle.TrailingFlameParticle;
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

    public static final DefaultParticleType TRAILING_FLAME_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType GREEN_TRAILING_FLAME_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType CONTACT_FLAME_PARTICLE = FabricParticleTypes.simple();
    
    public static final DefaultParticleType FLAME_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType GREEN_FLAME_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType GREEN_SECONDARY_FLAME_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType LIGHTNING_PARTICLE = FabricParticleTypes.simple();

    public static final DefaultParticleType BRANCH_LIGHTNING_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles(){
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "frost_particle"),
                                FROST_PARTICLE);
        
        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "contact_frost_particle"),
        CONTACT_FROST_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "trailing_frost_particle"),
        TRAILING_FROST_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "trailing_flame_particle"),
        TRAILING_FLAME_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "green_trailing_flame_particle"),
        GREEN_TRAILING_FLAME_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "contact_flame_particle"),
        CONTACT_FLAME_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "flame_particle"),
        FLAME_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "green_flame_particle"),
        GREEN_FLAME_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "green_secondary_flame_particle"),
        GREEN_SECONDARY_FLAME_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "lightning_particle"),
        LIGHTNING_PARTICLE);

        Registry.register(Registry.PARTICLE_TYPE, new Identifier("bns", "branch_lightning_particle"),
        BRANCH_LIGHTNING_PARTICLE);
    }


    public static void registerClientSideParticles(){
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.FROST_PARTICLE, FrostParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.CONTACT_FROST_PARTICLE, ContactFrostParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.TRAILING_FROST_PARTICLE, TrailingFrostParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.TRAILING_FLAME_PARTICLE, TrailingFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.CONTACT_FLAME_PARTICLE, ContactFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.FLAME_PARTICLE, FlameParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.GREEN_FLAME_PARTICLE, GreenFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.GREEN_TRAILING_FLAME_PARTICLE, GreenTrailingFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.GREEN_SECONDARY_FLAME_PARTICLE, GreenSecondaryFlameParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.LIGHTNING_PARTICLE, LightningParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.BRANCH_LIGHTNING_PARTICLE, BranchLightningParticle.Factory::new);


    }
}
