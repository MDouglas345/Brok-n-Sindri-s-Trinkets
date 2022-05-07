package net.fabricmc.BNSCore;



import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.GenericItemBlock.GenericItemBlock;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntityRenderer;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntityRenderer;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.Util.PacketUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback.Registry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class BNSClient implements ClientModInitializer {

    private static KeyBinding ActionKey = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding(
        "key.bns.actionkey", 
        GLFW.GLFW_KEY_R,
        "category.bns.keys",
        () -> false));
    
    public byte HeldTime = 0;


    @Override
    public void onInitializeClient() {
        BNSCore.LOGGER.info("In the client");

        BlockEntityRendererRegistry.register(BNSCore.GENERIC_ITEM_BLOCK_ENTITY, GenericItemBlockEntityRenderer::new);
        

        EntityRendererRegistry.register(BNSCore.GenericThrownItemEntityType, (context) ->{
            return new GenericThrownItemEntityRenderer(context);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            if (ActionKey.isPressed()){
                HeldTime += 1;
            }
            else{
               if (HeldTime >= 5){
                if (HeldTime > 40){HeldTime = 40;}
                PacketByteBuf P = PacketByteBufs.create();
                P.writeByte(HeldTime);
                ClientPlayNetworking.send(NetworkConstants.EstablishThrownItem, P);
               }
                HeldTime = 0;
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkConstants.EstablishThrownItem, (client, handler, buf, responseSender) ->{
            ItemStack stack = buf.readItemStack();
            Vec3d pos = PacketUtil.ReadVec3d(buf);
            Quaternion rot = PacketUtil.ReadQuaternion(buf);
            float rspeed = buf.readFloat();
            int ID = buf.readInt();
            UUID uuid = buf.readUuid();
            
            
            client.submit(() ->{
                GenericThrownItemEntity e = new GenericThrownItemEntity(client.world, pos.x, pos.y, pos.z);
                e.setItem(stack);
                e.setId(ID);
                e.setUuid(uuid);
                e.setQuat(rot);
                e.updatePosition(pos.x, pos.y, pos.z);
                e.updateTrackedPosition(pos.x, pos.y, pos.z);
                e.setRSpeed(rspeed);
                client.world.addEntity(ID, e);
            });
        });

        BlockRenderLayerMap.INSTANCE.putBlock(BNSCore.GENERIC_ITEM_BLOCK, RenderLayer.getCutout());
    }
    
}
