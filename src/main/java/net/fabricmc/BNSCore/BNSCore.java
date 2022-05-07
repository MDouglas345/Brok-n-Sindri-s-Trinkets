package net.fabricmc.BNSCore;

import net.fabricmc.GenericItemBlock.GenericItemBlock;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.GenericItemBlock.*;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BNSCore implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("BNS");
	public static final String ModID = "bns";

	public static final EntityType<GenericThrownItemEntity> GenericThrownItemEntityType = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(ModID, "generic_thrown_item"),
			FabricEntityTypeBuilder.<GenericThrownItemEntity>create(SpawnGroup.MISC, GenericThrownItemEntity::new)
					.dimensions(EntityDimensions.fixed(0.65F, 0.65F)) // dimensions in Minecraft units of the projectile
					.trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
					.build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
	);

	public static final Block GENERIC_ITEM_BLOCK = new GenericItemBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0f, 9999999f).nonOpaque().noCollision());
	public static BlockEntityType<GenericItemBlockEntity> GENERIC_ITEM_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(GenericItemBlockEntity::new, GENERIC_ITEM_BLOCK).build();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		BNSCore.LOGGER.info("In the main");

		Registry.register(Registry.BLOCK, new Identifier(ModID, "generic_item_block"), GENERIC_ITEM_BLOCK);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ModID, "generic_item_block_entity"), GENERIC_ITEM_BLOCK_ENTITY);

		ServerPlayNetworking.registerGlobalReceiver(NetworkConstants.EstablishThrownItem, (server, client, handler, buf, responseSender) -> {

			float timeHeld = (float)buf.readByte();

			server.submit(() ->{
				
				ServerWorld world = server.getOverworld();
				ItemStack itemstackOrig = client.getMainHandStack();
				ItemStack itemstack = itemstackOrig.copy();
				itemstack.setCount(1);
				//Entity e = new CreeperEntity(EntityType.CREEPER, world);
				GenericThrownItemEntity e = new GenericThrownItemEntity(GenericThrownItemEntityType, world);
				Vec3d pos = client.getPos();
				//client.getHandPosOffset(itemstack.getItem());
				e.setPos(pos.x, client.getEyeY(), pos.z);
				e.updatePosition(pos.x, client.getEyeY(), pos.z);
				e.updateTrackedPosition(pos.x, client.getEyeY(), pos.z);
				e.setOwner(client);
				e.setItem(itemstack.copy());
				//e.setProperties()
				//client.getCameraEntity().
				e.setVelocity(client, client.getPitch(), client.getYaw(), 0, timeHeld / 40f , 0f);
				

				//e.setQuat(Quaternion.fromEulerXyzDegrees(new Vec3f(0f, client.getYaw() + 180f,0)));
				//e.setQuat(Vec3f.POSITIVE_Y.getDegreesQuaternion(client.getYaw()));
				//e.setQuat(Quaternion.fromEulerXyz((client.getCameraEntity().getRotationVector()));
				e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 180 - client.getYaw(), true));
				e.setRSpeed(timeHeld*4f);

				if (!client.isCreative()){
					itemstackOrig.decrement(1);
				}
				world.spawnEntity(e);
				
			});



		});
	}
}
