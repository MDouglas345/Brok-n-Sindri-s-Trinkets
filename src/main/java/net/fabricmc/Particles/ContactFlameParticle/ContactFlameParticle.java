package net.fabricmc.Particles.ContactFlameParticle;

import net.fabricmc.Particles.FrostParticle.FrostParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ContactFlameParticle extends SpriteBillboardParticle {

    float maxSize = 0.1f;
    float minSize = 0.2f;


    protected ContactFlameParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
    SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.velocityMultiplier = 0.9F;
       
        this.scale = 1f;
        this.maxAge = 25;
        this.setSpriteForAge(spriteSet);

        this.gravityStrength = 0.01f;
        
        this.angle = 0;

       
        this.alpha = 0.8f;
        this.red = 1f;
        this.green = 1f;
        this.blue = 1f;

        

       
    }


    @Override
    public void tick() {
        super.tick();
        fadeOut();
        //sizeUpOverTime();
        
        this.velocityY += 0.02;
        this.angle += 0.0005f;

        if (this.age > 22){
            this.world.addParticle(ParticleTypes.SMOKE, x, y, z, velocityX, velocityY, velocityZ);
        }
        
        //whiteOutOverTime();
        //this.velocityY -= 0.01;
        
    }

    @Override
    public float getSize(float delta){
        return MathHelper.lerp((age/maxAge) * 3f + 0.2f, 0.2f, 1.1f);
        
    }

    public void whiteOutOverTime(){
        float amount = 0.02f;

        this.blue += amount;
        this.green += amount;
        this.red += amount;
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
    public ParticleTextureSheet getType() {
        // TODO Auto-generated method stub
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)maxAge) * age * 1f + 1f);
        //this.alpha = 0.8f * ()
    }

    private void sizeUpOverTime(){
        this.scale((float) MathHelper.clamp((-age/maxAge)  * maxSize + minSize, 0.2, 1.0));
    }

    


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new ContactFlameParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
    
}
