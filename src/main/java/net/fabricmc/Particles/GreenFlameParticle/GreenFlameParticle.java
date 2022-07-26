package net.fabricmc.Particles.GreenFlameParticle;

import net.fabricmc.Particles.FlameParticle.FlameParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GreenFlameParticle extends FlameParticle {

    protected GreenFlameParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
            SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, spriteSet, xd, yd, zd);
        //TODO Auto-generated constructor stub
    }
    

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new GreenFlameParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
