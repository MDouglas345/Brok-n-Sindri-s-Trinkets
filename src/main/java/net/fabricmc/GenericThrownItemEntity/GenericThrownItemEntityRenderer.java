package net.fabricmc.GenericThrownItemEntity;

import org.apache.commons.compress.harmony.unpack200.bytecode.forms.IincForm;
import org.lwjgl.system.CallbackI.I;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.Util;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;


public class GenericThrownItemEntityRenderer extends EntityRenderer<GenericThrownItemEntity>{

    private static final float MIN_DISTANCE = 12.25F;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public GenericThrownItemEntityRenderer(Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
       
     }
  
     public GenericThrownItemEntityRenderer(Context context) {
        this(context, 1.0F, false);
     }
  
     protected int getBlockLight(GenericThrownItemEntity entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
     }

     public void render(GenericThrownItemEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        
      Quaternion r =  entity.originalRot.copy();
      r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(entity.rotoffset, 0,0)));
      
      
      

      

      matrices.push();
      
      matrices.multiply(r);

      //matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
      //matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch())));
      
      //matrices.scale(1.3f, 1.3f, 1.3f);
      //this.itemRenderer.renderItem(entity.itemToRender, Mode.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getId());
      this.itemRenderer.renderItem((LivingEntity) entity.getOwner(), entity.itemToRender, Mode.FIRST_PERSON_RIGHT_HAND, false, matrices, vertexConsumers, entity.world, light, 0, 0);
      //this.SpawnLineOfParticles((ClientWorld) entity.world, entity.getPos(), r,  0.5f);

      matrices.pop();

      //this.SpawnLineOfParticles((ClientWorld) entity.world, entity.getPos(), r,  0.5f);
   }

   public void SpawnLineOfParticles(ClientWorld world, Vec3d Pos, Quaternion quat, float radius){
// need to rotate this so that it follows the items orientation
// also need to make it spawn in a straight line instead of a circle. Maybe the circle can be used when its planted in the ground

   /*   
   Vec3d newpos = Pos.add(new Vec3d(0.5f, 0.5f, 0.5f));

      newpos = Util.rotatePointByQuat(Pos, quat);

      float steps = (2*3.14159f)/5f;
      for (float i = 0; i <= (2*3.14159); i += steps){
         double x = radius * Math.cos(i);
         double z = radius * Math.sin(i);

         world.addParticle(ParticleRegistery.FROST_PARTICLE,
         newpos.getX() + x, newpos.getY(), newpos.getZ() + z,
                       0, 0, 0);
            
      }
      */
      int amount  = 5;
      float step = 0.15f;
      for ( int i = 0; i  < amount; i += 1){
         //Vector4f newPos = new Vector4f(0,0,step*i, 1);
         
         //Vec3f newPos = new Vec3f(0,step*i,0);
         //newPos.transform(mat);
         Vec3d newPos = new Vec3d(0,0,step*i);
         newPos = Util.rotatePointByQuat(newPos, quat);
         newPos = newPos.add(Pos);
        //newPos.add((float)Pos.x, (float)Pos.y, (float)Pos.z, 0f);
         //newPos.add((float)Pos.x, (float)Pos.y, (float)Pos.z);

         world.addParticle(ParticleRegistery.FROST_PARTICLE,
         newPos.getX(), newPos.getY(), newPos.getZ(),
                       0, 0, 0);
      }
   }
  /*
      @Override
     public void render(GenericThrownItemEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        
        Quaternion r =  entity.originalRot.copy();
        
        

        r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(entity.rotoffset, 0,0)));

        matrices.push();
        //matrices.multiply(entity.originalRot);
        matrices.multiply(r);
        matrices.scale(1.3f, 1.3f, 1.3f);
        this.itemRenderer.renderItem(entity.itemToRender, Mode.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getId());
        matrices.pop();
     }

   */ 

   @Override
   public Identifier getTexture(GenericThrownItemEntity var1) {
      // TODO Auto-generated method stub
      return null;
   }
    
}
