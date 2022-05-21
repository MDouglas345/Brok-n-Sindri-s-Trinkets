/**
 * This stuck renderer seems to be one of the hardestr things for me to get right.
 * 
 * idea : Get a point on the model part (this will be the contact / penetration point P)
 * 
 * Get another random point, scaled by some factor (this will be the translation point / the point where the item will be rendererd S)
 * 
 * Get the vector D = S - P ( a vector that points from the outer starting point to the penetration point), calculate the Quaternion of this directional vector
 * 
 * Apply Quaternion and translation and hopefully we will have a working random point penetration system
 */

package net.fabricmc.Renderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.system.windows.WINDOWPLACEMENT;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.IAccessModelParts;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.Util.Util;
import net.minecraft.block.AirBlock;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class StuckItemsSinglePartFeatureRenderer<T extends LivingEntity, M extends SinglePartEntityModel<T>> extends FeatureRenderer<T, M> {

    private final EntityRenderDispatcher dispatcher;
    private final ItemRenderer           itemRenderer;
    private String                       part;
    
    public StuckItemsSinglePartFeatureRenderer(EntityRendererFactory.Context context, LivingEntityRenderer<?, ?> entityRenderer, String part) {
        super((FeatureRendererContext<T, M>) entityRenderer);
        //TODO Auto-generated constructor stub
        this.dispatcher = context.getRenderDispatcher();
        itemRenderer = context.getItemRenderer();
        this.part = part;
    }

   
    public boolean hasItemStuck(LivingEntity ent) {
        ISavedItem e = (ISavedItem)ent;
       
        return e.getSavedItem().getItem() == Items.AIR ? false : true;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        /*int m = this.getObjectCount(livingEntity);
        Random random = new Random(((Entity)livingEntity).getId());
        if (m <= 0) {
            return;
        }
        for (int n = 0; n < m; ++n) {
            matrixStack.push();
            ModelPart modelPart = ((PlayerEntityModel)this.getContextModel()).getRandomPart(random);
            ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
            modelPart.rotate(matrixStack);
            float o = random.nextFloat();
            float p = random.nextFloat();
            float q = random.nextFloat();
            float r = MathHelper.lerp(o, cuboid.minX, cuboid.maxX) / 16.0f;
            float s = MathHelper.lerp(p, cuboid.minY, cuboid.maxY) / 16.0f;
            float t = MathHelper.lerp(q, cuboid.minZ, cuboid.maxZ) / 16.0f;
            matrixStack.translate(r, s, t);
            o = -1.0f * (o * 2.0f - 1.0f);
            p = -1.0f * (p * 2.0f - 1.0f);
            q = -1.0f * (q * 2.0f - 1.0f);
            this.renderObject(matrixStack, vertexConsumerProvider, i, (Entity)livingEntity, o, p, q, h);
            matrixStack.pop();
        }
        */

        if (!this.hasItemStuck(livingEntity)){
            return;
        }

        Random random = new Random(((Entity)livingEntity).getId());
        //Random random = Util.randgen;

        ModelPart modelPart = ((SinglePartEntityModel<T>)this.getContextModel()).getPart().getChild(part);
        ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
        

        ItemStack item = ((ISavedItem)livingEntity).getSavedItem();

        GenericThrownItemEntity thrown = new GenericThrownItemEntity(livingEntity.world, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        thrown.setItem(item);
        thrown.setQuat(Quaternion.fromEulerXyz(new Vec3f(0, 0, 0)));

        
        float side = random.nextFloat();

        Vec3d P = this.getPointOnPart(new Vec3d(random.nextFloat(), MathHelper.clamp(random.nextFloat(), 0.0f, 0.3f), side), cuboid);

        Vec3d S = this.getPointOnPart(new Vec3d(random.nextFloat(), MathHelper.clamp(random.nextFloat(), 0.0f, 0.3f), random.nextFloat() > 0.5f ? 1.0f : 0.0f), cuboid);
        S = S.multiply(2); // may need adjustments

        //BNSCore.LOGGER.info(P.toString() + " " + S.toString());

        Vec3d D = P.subtract(S);
        Vec3d itemFacing = Vec3d.fromPolar(thrown.getPitch(), thrown.getYaw());

        Quaternion toPenetration = Util.getDirectionalRotation(itemFacing, D);
                 
        matrixStack.push();
        //modelPart.rotate(matrixStack);
        //Quaternion quat = Quaternion.fromEulerXyz(modelPart.pitch, modelPart.yaw, modelPart.roll);
        
        //quat.hamiltonProduct(toPenetration);

        //matrixStack.multiply(quat);
        matrixStack.translate(S.x, S.y, S.z);
        matrixStack.multiply(toPenetration);

        

        //matrixStack.translate(point.getX(), point.getY(), point.getZ());
        
        this.dispatcher.render(thrown, 0, 0, 0, 0, h, matrixStack, vertexConsumerProvider, i);
        

        //this.itemRenderer.renderItem(null, item, Mode.FIRST_PERSON_RIGHT_HAND, false, matrixStack, vertexConsumerProvider, livingEntity.world, i, 0, 0);
        matrixStack.pop();
        

       
    }

    private Vec3d getPointOnPart(Vec3d v, ModelPart.Cuboid cuboid){
        float r = (float) (MathHelper.lerp(v.x, cuboid.minX, cuboid.maxX) / 16.0f);
        float s = (float) (MathHelper.lerp(v.y, cuboid.minY, cuboid.maxY)  / 16.0f);
        float t = (float) (MathHelper.lerp(v.z, cuboid.minZ, cuboid.maxZ)  / 16.0f) ;

        return new Vec3d(r,s,t);
    }

    protected void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float directionx,
            float directiony, float directionz, float tickDelta) {
        
                ItemStack i = ((ISavedItem)entity).getSavedItem();
                Vec3d entityrotation = entity.getRotationVector();
                Vec3d direction = new Vec3d(directionx, directiony, directionz);

                if (entityrotation.dotProduct(direction) > 0){

                }
                else{

                }
                GenericThrownItemEntity thrown = new GenericThrownItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
                thrown.setItem(i);
                thrown.setQuat(Quaternion.fromEulerXyz(new Vec3f(directionx, directiony, directionz)));
                this.dispatcher.render(thrown, 0, 0, 0, 0, tickDelta, matrices, vertexConsumers, light);

    }
}
