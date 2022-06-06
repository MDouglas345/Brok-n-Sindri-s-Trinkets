/**
 * Special type of particle that does not follow the camera, but instead gets rendererd with a unique quaternion
 * 
 * Problem : Back face culling needs to be turned off
 * 
 * Solution 1 : Mixin into ParticleManager.renderParticles() head.
 * Create a new ParticleTextureSheet with properties for rendering. In the mixin described above :
 * 
 * Have a new list 
 */
package net.fabricmc.Particles.BranchLightningParticle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.fabricmc.Util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class BranchLightningParticle  extends SpriteBillboardParticle{
    SpriteProvider sprites;
    int ageIncrement;
    Quaternion directionTo;

    protected BranchLightningParticle(ClientWorld level, double xCoord, double yCoord, double zCoord,
    SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);
        Vec3d pos = new Vec3d(x,y,z);
        Vec3d target = new Vec3d(xd, yd, zd);

        Vec3d dir = target.subtract(pos).normalize();
        Vec3d up = new Vec3d(0, -1, 0);

        directionTo = Util.getDirectionalRotation(up, dir);
        /**
         * assume xCoord, yCoord, zCoord is the target's position and not velocity!
         */


        this.velocityMultiplier = 0F;

       
        this.scale = 0.9f;

        this.maxAge = 10;

        //this.age =  (int)Util.getRandomFloat(0.2f, 0.8f) * this.maxAge;

        //this.ageIncrement = Util.randgen.nextFloat() > 0.5? 1 : -1;

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
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Quaternion quaternion;
        Vec3d vec3d = camera.getPos();
        float f = (float)(MathHelper.lerp((double)tickDelta, this.prevPosX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickDelta, this.prevPosY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
       /**
        * calculate the quaternion / use the built in quaternion here!
        */
        quaternion = directionTo.copy();
        /* 
        Vec3f X = new Vec3f(0,0,1);
        X.rotate(quaternion);
    
        Vec3d toCamera = camera.getPos().subtract(new Vec3d(x,y,z)).normalize();

        Quaternion newTurn = Util.getDirectionalRotation(new Vec3d(X.getX(), X.getY(), X.z), toCamera);
        quaternion.hamiltonProduct(newTurn);
        */
      

        
        

        /* 
         quaternion = new Quaternion(camera.getRotation());
            float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
            quaternion.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(i));
        */
        Vec3f vec3f = new Vec3f(-1.0f, -1.0f, 0.0f);
        vec3f.rotate(quaternion);
        //Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0f, -1.0f, 0.0f), new Vec3f(-1.0f, 1.0f, 0.0f), new Vec3f(1.0f, 1.0f, 0.0f), new Vec3f(1.0f, -1.0f, 0.0f)}; 
        Vec3f[] vec3fs = new Vec3f[]{new Vec3f(-1.0f, -5.0f, 0.0f), new Vec3f(-1.0f, 0.0f, 0.0f), new Vec3f(1.0f, 0.0f, 0.0f), new Vec3f(1.0f, -5.0f, 0.0f)}; 
        float j = this.getSize(tickDelta);
        for (int k = 0; k < 4; ++k) {
            Vec3f vec3f2 = vec3fs[k];
            vec3f2.scale(j);
            vec3f2.rotate(quaternion);
            vec3f2.add(f, g, h);
        }
        float l = this.getMinU();
        float m = this.getMaxU();
        float n = this.getMinV();
        float o = this.getMaxV();
        int p = this.getBrightness(tickDelta);

        vertexConsumer.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ()).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ()).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ()).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();

        vertexConsumer.vertex(vec3fs[3].getX(), vec3fs[3].getY(), vec3fs[3].getZ()).texture(l, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[2].getX(), vec3fs[2].getY(), vec3fs[2].getZ()).texture(l, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[1].getX(), vec3fs[1].getY(), vec3fs[1].getZ()).texture(m, n).color(this.red, this.green, this.blue, this.alpha).light(p).next();
        vertexConsumer.vertex(vec3fs[0].getX(), vec3fs[0].getY(), vec3fs[0].getZ()).texture(m, o).color(this.red, this.green, this.blue, this.alpha).light(p).next();
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
        this.age += 1;
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
            return new BranchLightningParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
