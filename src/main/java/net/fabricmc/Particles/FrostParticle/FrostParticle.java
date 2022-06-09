package net.fabricmc.Particles.FrostParticle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class FrostParticle extends SpriteBillboardParticle {

    SpriteProvider spriteSets;

    protected FrostParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
    SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.velocityMultiplier = 0.9F;
       
        this.scale = 0.3f;
        this.maxAge = 16;
        this.setSpriteForAge(spriteSet);

        this.gravityStrength = 0.03f;

        this.spriteSets = spriteSet;
       
        this.alpha = 0.8f;
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public void tick() {

        
        if (!this.onGround){
            this.velocityY -= 0.03;
        }
        else{
            this.velocityX *= 1.001;
            this.velocityZ *= 1.001;
            this.velocityY = 0;
        }
        
        super.tick();
        this.setSpriteForAge(spriteSets);
        fadeOut();
       

    }

    @Override
    public ParticleTextureSheet getType() {
        // TODO Auto-generated method stub
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)maxAge) * age * 0.8f + 0.9f);
        //this.alpha = 0.8f * ()
    }


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new FrostParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    
}
