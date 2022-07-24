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

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
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
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class UniversalStuckItemsFeatureRenderer<T extends LivingEntity, M extends AnimalModel<T>> extends FeatureRenderer<T, M> {

    private final EntityRenderDispatcher dispatcher;
    private final ItemRenderer           itemRenderer;
    //private Random random;
    
    public UniversalStuckItemsFeatureRenderer(EntityRendererFactory.Context context, LivingEntityRenderer<?, ?> entityRenderer) {
        super((FeatureRendererContext<T, M>) entityRenderer);
        //TODO Auto-generated constructor stub
        this.dispatcher = context.getRenderDispatcher();
        itemRenderer = context.getItemRenderer();
        //random = new Random();
        
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

        EntityDimensions d = livingEntity.getDimensions(EntityPose.STANDING);
        MatrixStack newMat = RenderSystem.getModelViewStack();

        
        

       Vec3d eye = livingEntity.getEyePos();
        
        //Random random = Util.randgen;

        
        

        ItemStack item = ((ISavedItem)livingEntity).getSavedItem();

        //GenericThrownItemEntity thrown = new GenericThrownItemEntity(livingEntity.world, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        GenericThrownItemEntity thrown = new GenericThrownItemEntity(livingEntity.world, eye.getX(), eye.getY(), eye.getZ());
        thrown.setItem(item);
        thrown.setQuat(Quaternion.fromEulerXyz(new Vec3f(0, 0, 0)));

        //Vec3d toEye = eye.subtract(thrown.getPos());
        Vec3d toEye = thrown.getPos().subtract(eye);




        /*
        
        float side = random.nextFloat() > 0.5f ? 1.0f : 0.2f;

        Vec3d P = this.getPointOnPart(new Vec3d(MathHelper.clamp(random.nextFloat(), 0.5, 0.6), MathHelper.clamp(random.nextFloat(), 0.7f, 0.9f), side), cuboid);

        Vec3d S = this.getPointOnPart(new Vec3d(random.nextFloat(), MathHelper.clamp(random.nextFloat(), 0.2f, 0.3f), -side), cuboid);
       
       
        S = S.multiply(1.2);
        P = new Vec3d(P.x, P.y, -S.z);
        
        */
       


        
        double side = random.nextDouble();

        double width = d.width / 2;
        double offset = -0.96;
        double height = livingEntity.getEyeHeight(EntityPose.STANDING) + offset;
        
        Vec3d P = new Vec3d(Util.getRandomDouble(random,-width, width), Util.getRandomDouble(random,-height, - offset), side > 0.5 ? width : -width);
        Vec3d S = new Vec3d(Util.getRandomDouble(random,-width, width), Util.getRandomDouble(random,-height, - offset), side > 0.5 ? -width : width);
        //BNSCore.LOGGER.info(P.toString() + " " + S.toString());
        

        /* 
        double side = random.nextDouble() >0.5 ? -0.5 : 0.5;
        
        Vec3d P = new Vec3d(Util.getRandomDouble(random, -0.1, 0.1), Util.getRandomDouble(random,0.3, 0.4), side);
        Vec3d S = new Vec3d(Util.getRandomDouble(random, -0.3, 0.3), Util.getRandomDouble(random,-0.1, 0.3), -side);
        */

        Vec3d D = P.subtract(S).normalize();
        Vec3d itemFacing = Vec3d.fromPolar(thrown.getPitch(), thrown.getYaw());

        Quaternion toPenetration = Util.getDirectionalRotation(new Vec3d(0,1,0), D);
                 
        matrixStack.push();
        //modelPart.rotate(matrixStack);
        //Quaternion quat = Quaternion.fromEulerXyz(modelPart.pitch, modelPart.yaw, modelPart.roll);
        
        //quat.hamiltonProduct(toPenetration);

        //matrixStack.multiply(quat);

        matrixStack.translate(S.x, S.y, S.z);
        //matrixStack.translate(toEye.x, toEye.y, toEye.z);
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