package net.fabricmc.GenericItemBlock;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;


public class GenericItemBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>{

    private ItemRenderer itemRenderer;
    private float scale;
    private boolean lit;
    float off = 0;

    public GenericItemBlockEntityRenderer(Context ctx) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        
     }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        GenericItemBlockEntity block = (GenericItemBlockEntity)blockEntity;

        Quaternion r = block.Quat.copy();
        r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(block.Offset, 0,0)));
        //r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(off, 0,0)));

        matrices.push();
        //matrices.multiply(entity.originalRot);
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.scale(1.3f, 1.3f, 1.3f);
        matrices.multiply(r);
       
        this.itemRenderer.renderItem(block.SavedItem, Mode.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
        //this.itemRenderer.renderItem((LivingEntity) block.getOwner(), block.SavedItem, Mode.FIRST_PERSON_RIGHT_HAND, false, matrices, vertexConsumers, entity.world, light, 0, 0);
        matrices.pop();
        
        //off += 0.05;


    }
    
}
