package net.fabricmc.BNSCore;



import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.GenericItemBlock.GenericItemBlockEntityRenderer;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntityRenderer;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Particles.FrostParticle.FrostParticle;
import net.fabricmc.Renderers.StuckItemsPlayerFeatureRenderer;
import net.fabricmc.Renderers.StuckItemsQuadrupedFeatureRenderer;
import net.fabricmc.Renderers.StuckItemsSinglePartFeatureRenderer;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.Util.PacketUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.Living;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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

            /**
             * Particles can probably be spawned here depending on how long the key is pressed
             * (how to sync with other players?)
             */
            if (client.player != null && client.player.input.sneaking){
                HeldTime = 0;
            }

            if (ActionKey.isPressed() && !client.player.input.sneaking){
                HeldTime += 1;
            }
            else{
                
               if (HeldTime > 0){
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
            float bonusAttack = buf.readFloat();
            int ID = buf.readInt();
            UUID uuid = buf.readUuid();
            UUID owneruuid = buf.readUuid();
            String ownername = buf.readString();
            boolean maxed = buf.readBoolean();

            
            
            
            client.submit(() ->{
          
               
                
                GenericThrownItemEntity e = new GenericThrownItemEntity(client.world, pos.x, pos.y, pos.z);
                e.setItem(stack);
                e.setId(ID);
                e.setUuid(uuid);
                e.setQuat(rot);
                e.updatePosition(pos.x, pos.y, pos.z);
                e.updateTrackedPosition(pos.x, pos.y, pos.z);
                //e.setOwner(entity);
                e.setRSpeed(rspeed);
                e.setBonusAttack(bonusAttack);
                e.setOwner(client.world.getPlayerByUuid(owneruuid));
                e.SetMaxed(maxed);
                e.SetOwner(ownername, owneruuid);
               
                client.world.addEntity(ID, e);
            });
        });

        BlockRenderLayerMap.INSTANCE.putBlock(BNSCore.GENERIC_ITEM_BLOCK, RenderLayer.getCutout());

        ParticleFactoryRegistry.getInstance().register(ParticleRegistery.FROST_PARTICLE, FrostParticle.Factory::new);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			// minecraft:player SHOULD be printed twice
			
            /*
			if (entityRenderer instanceof PlayerEntityRenderer) {
				registrationHelper.register(new StuckItemsPlayerFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(context, (PlayerEntityRenderer) entityRenderer));
                
			}
            */
            
            /**
             * Lots of things have unique traits, theres gonna be alot of renderers for each mob... This
             * will definitely take the longest to complete. Working so far : bipeds, quadrupeds
             */
            
            if (entityRenderer instanceof BipedEntityRenderer || entityRenderer instanceof PlayerEntityRenderer){
                registrationHelper.register(new StuckItemsPlayerFeatureRenderer<LivingEntity, BipedEntityModel<LivingEntity>>(context,  entityRenderer));
            }
            else if (entityType == EntityType.SHEEP || entityType == EntityType.COW || entityType == EntityType.PIG){
                registrationHelper.register(new StuckItemsQuadrupedFeatureRenderer<LivingEntity, QuadrupedEntityModel<LivingEntity>>(context,  entityRenderer));
                
            }
            else if (entityType == EntityType.VILLAGER || entityType == EntityType.PILLAGER){
                registrationHelper.register(new StuckItemsSinglePartFeatureRenderer<LivingEntity, SinglePartEntityModel<LivingEntity>>(context,  entityRenderer, "body"));
            }
            else if (entityType == EntityType.SLIME){
                registrationHelper.register(new StuckItemsSinglePartFeatureRenderer<LivingEntity, SinglePartEntityModel<LivingEntity>>(context,  entityRenderer, "cube"));
            }
            else if (entityType == EntityType.CREEPER){
                registrationHelper.register(new StuckItemsSinglePartFeatureRenderer<LivingEntity, SinglePartEntityModel<LivingEntity>>(context,  entityRenderer, "head"));
            }

		});
    }
    
}
