package net.fabricmc.Particles.GreenSecondaryFlame;

import net.fabricmc.Util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractSlowingParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class GreenSecondaryFlameParticle extends AbstractSlowingParticle {
   SpriteProvider spriteProvider;
   int spawnSmoke;

   protected GreenSecondaryFlameParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
   SpriteProvider spriteSet, double xd, double yd, double zd) {
      super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        //this.velocityMultiplier = 0.8F;
       
        this.maxAge = (int) (14 * Util.randgen.nextFloat() + 2);
        this.spawnSmoke = this.maxAge - 2;

        this.setSpriteForAge(spriteSet);
        //this.gravityStrength = 0.03f;

       
        this.alpha = 0.8f;
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;

        
   }

   public void move(double dx, double dy, double dz) {
      this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
      this.repositionFromBoundingBox();
   }

   public float getSize(float tickDelta) {
      float f = ((float)this.age + tickDelta) / (float)this.maxAge;
      return this.scale * (1.0F - f * f * 0.5F);
   }

   public int getBrightness(float tint) {
      float f = ((float)this.age + tint) / (float)this.maxAge;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      int i = super.getBrightness(tint);
      int j = i & 255;
      int k = i >> 16 & 255;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
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
            return new GreenSecondaryFlameParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
