package net.fabricmc.Particles.FlameParticle;

import net.fabricmc.Util.Util;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;

public class FlameParticle extends SpriteBillboardParticle {

    SpriteProvider spriteProvider;
    int spawnSmoke;

    protected FlameParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
    SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.velocityMultiplier = 0.8F;
       
        this.scale = 0.25f;
        this.maxAge = (int) (14 * Util.randgen.nextFloat() + 2);
        this.spawnSmoke = this.maxAge - 2;

        
        this.setSpriteForAge(spriteSet);
        

        this.spriteProvider = spriteSet;
        //this.gravityStrength = 0.03f;

       
        this.alpha = 0.8f;
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;
    }

    @Override
    public int getBrightness(float tint) {
        // might be able to do some fun stuff here
        float f = -((float)this.age + tint) / (float)this.maxAge + 1f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        int i = super.getBrightness(tint);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int)(f * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    @Override
    public void tick() {

        super.tick();
        this.velocityY += 0.005;

        if (this.age > spawnSmoke){
            this.world.addParticle(ParticleTypes.SMOKE, x, y, z, velocityX, velocityY, velocityZ);
        }

        //this.setSpriteForAge(this.spriteProvider);
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
            return new FlameParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    
}
