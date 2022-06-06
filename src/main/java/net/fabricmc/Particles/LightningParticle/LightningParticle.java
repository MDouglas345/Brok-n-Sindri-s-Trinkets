package net.fabricmc.Particles.LightningParticle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.fabricmc.Util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class LightningParticle  extends SpriteBillboardParticle{
    SpriteProvider sprites;
    int ageIncrement;

    protected LightningParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
    SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.velocityMultiplier = 0F;

       
        this.scale = 0.2f;

        this.maxAge = 16;

        this.age =  (int)Util.getRandomFloat(0.2f, 0.8f) * this.maxAge;

        this.ageIncrement = Util.randgen.nextFloat() > 0.5? 1 : -1;

        this.setSpriteForAge(spriteSet);

        this.gravityStrength = 0.03f;

        this.sprites = spriteSet;

        this.angle = Util.getRandomFloat(0, (float) (2 * Math.PI));
        this.prevAngle = this.angle;
       
        this.alpha = 0.8f;
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public int getBrightness(float tint) {
        // might be able to do some fun stuff here
        int i = super.getBrightness(tint);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int)(0.8f * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    @Override
    public void tick(){
        //super.tick();
        this.age += this.ageIncrement;
        if (age >= this.maxAge || age <= 0){
            this.markDead();
            return;
        }
        this.angle = this.prevAngle;
        this.setSpriteForAge(this.sprites);
    }

    @Override
    public ParticleTextureSheet getType() {
        // TODO Auto-generated method stub
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new LightningParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
