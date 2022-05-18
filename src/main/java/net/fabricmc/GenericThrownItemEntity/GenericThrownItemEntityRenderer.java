package net.fabricmc.GenericThrownItemEntity;

import net.fabricmc.BNSCore.BNSCore;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;


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
      //matrices.multiply(entity.originalRot);
      matrices.multiply(r);
      matrices.scale(1.3f, 1.3f, 1.3f);
      //this.itemRenderer.renderItem(entity.itemToRender, Mode.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getId());
      this.itemRenderer.renderItem((LivingEntity) entity.getOwner(), entity.itemToRender, Mode.FIRST_PERSON_RIGHT_HAND, false, matrices, vertexConsumers, entity.world, light, 0, 0);
      matrices.pop();
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
