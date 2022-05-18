/**
 * Attempting to create a return effect for weapons and tools!
 * 
 * idea for referring to far away block entities : implemented!
 * Each player entity will have a list of blockpos of where returnable items are currently.
 * That way the server can essentially "pop the stack" of blockpos and return items to the player.
 * This only works under the assumption that the thrown item lands on a block! need a more universal solution in case
 * the thrown item is lodged into another living entity! This is something i will look into when I am implementing stuck in living entity feature
 * 
 * details on idea : requires a mixin for the player entity
 * 
 * idea for the actual return effect :
 * Block entities that are called to be returned will turn into thrown entities which will then go in the direction
 * of the player that threw it / is attached to. Once collided with said player, it will go into their inventory
 * 
 * details on idea : if an block entity is in an unloaded chunk (very far from any player), the created thrown
 * entity may not be tickable. Need to find a solution for this.
 * 
 * Solution : if BE is some threshold away from the player, get a direction vector that points from the BE to the player,
 *  and minimize it so the vector points from the rim of the chunk the player is in to the player. That way, the thrown entity will
 *  become tickable and will move towards the player as if it came from its original position in the BE.
 * 
 * 
 *  EnchantmentHelper.getLevel(enchantment, itemStack) useful for determining if an itemstack has an enchantment by returning 0 if non existent
 * 
 * 
 * problem : a client can throw an item and disconnects before the item lands and registers as a returnable item for the player
 * solution : have a NBT-esque data structure <playername> : <stack of entity uuid>	stored on the server, agnostic to actual serverplayerentity objects
 * 														   : <stack of block pos>
 * 
 * 				this will be a fool proof way of ensuring all player's thrown items are accounted for and they can call them back in case of crash / disconnect
 * 
 * 
 * continuing on...
 * 
 * when the player dies and they have itemstacks that have the worthy 2 enchantment.. 
 * A mixin is required to save those items in a list and remove from the player inventory at the head of the PlayerInventory.dropAll() function
 * and another mixin is required to place those items back into the players inventory before the return of the same function.
 * 
 * Loophole : if a player put their worthy 2 weapon in a chest, anyone can yoink it. Needs to look at a solution or just tell the player
 * tough luck, shouldve just thrown it against the wall or something if you need space.
 * 
 * 
 * Check powdersnowbloc#getcollisionshape to find out how to make blocks that are only collidable with certain types
 */


package net.fabricmc.BNSCore;

import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.Enchantments.WorthyEnchantment.WorthyToolEnchantment;
import net.fabricmc.Enchantments.WorthyEnchantment.WorthyWeaponEnchantment;
import net.fabricmc.GenericItemBlock.*;
import net.fabricmc.Util.IClientPlayerEntity;
import net.fabricmc.Util.IPlayerEntityItems;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

import javax.print.attribute.standard.Destination;

import org.apache.logging.log4j.core.pattern.ThreadIdPatternConverter;
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
					.trackRangeBlocks(10).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
					.build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
	);

	public static final Block GENERIC_ITEM_BLOCK = new GenericItemBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0f, 9999999f).nonOpaque());

	public static BlockEntityType<GenericItemBlockEntity> GENERIC_ITEM_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(GenericItemBlockEntity::new, GENERIC_ITEM_BLOCK).build();

	public static Enchantment WorthyWeapon = Registry.register(Registry.ENCHANTMENT, 
														 new Identifier(ModID, "worthyweapon"),
														 new WorthyWeaponEnchantment());

	public static Enchantment WorthyTool = Registry.register(Registry.ENCHANTMENT, 
														 new Identifier(ModID, "worthytool"),
														 new WorthyToolEnchantment());

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

				//IClientPlayerEntity player = (IClientPlayerEntity) client;
                //player.toggleShouldMove();
			
				if (timeHeld < 5){
					// recall the last throw / landed item in stack!
					//otherwise throw whatever is in the players hand
					UUIDStackComponent uuidstack = mycomponents.EntityUUIDs.get(world.getLevelProperties());
					BlockPosStackComponent bpstack = mycomponents.BlockEntityPositions.get(world.getLevelProperties());

					UUID entityuuid = uuidstack.Pop(client.getEntityName());

					if (entityuuid == null){
						BlockPos BEPosition = bpstack.Pop(client.getEntityName());

						if (BEPosition == null){
							return;
						}
						
						GenericItemBlockEntity restingEntity = (GenericItemBlockEntity) world.getBlockEntity(BEPosition);

						if (restingEntity == null){
							return;
						}

						if (EnchantmentHelper.getLevel(WorthyTool, restingEntity.SavedItem) == 0 && EnchantmentHelper.getLevel(WorthyWeapon, restingEntity.SavedItem) == 0 ){
							return;
						}

						

						/**
						 * How do you check if the entity is too far from the player? what is the radius of a loaded chunk?
						 */

						int dist = (server.getPlayerManager().getViewDistance()- 2 ) * 16; //modify this to fix bug where item is close but acts like its far
						BlockPos Destination = client.getBlockPos();

						if (BEPosition.isWithinDistance(Destination, 4)){
							/**
							 * if block is within 2 block lengths of the player, just chuck the item into the player inventory
							 */
							if (!client.getInventory().insertStack(restingEntity.SavedItem)){
								return;
							}
							
							world.removeBlock(BEPosition, false);
						}

						if (!BEPosition.isWithinDistance(Destination, dist + 2)){
							// if the BE is too far!
							Vec3d Direction = Vec3d.of(Destination.subtract(BEPosition));
							Direction = Direction.normalize();
							Direction = Direction.multiply(dist);
							Vec3d position = Vec3d.of(Destination).subtract(Direction);

							GenericThrownItemEntity e = GenericThrownItemEntity.CreateNew(world, (PlayerEntity) world.getEntity(restingEntity.Owner.ID), position, restingEntity.SavedItem);

							world.spawnEntity(e);

							e.ChangeState(4);

						}else{
							// if the BE is within range!
							
						
							Vec3d position = Vec3d.of(BEPosition);
						
							GenericThrownItemEntity e = GenericThrownItemEntity.CreateNew(world, (PlayerEntity) world.getEntity(restingEntity.Owner.ID), position, restingEntity.SavedItem);

							world.spawnEntity(e);

							e.ChangeState(4);

						}

						world.removeBlock(BEPosition, false);
						return;
					}

					GenericThrownItemEntity thrownEntity = (GenericThrownItemEntity) world.getEntity(entityuuid);

					if (EnchantmentHelper.getLevel(WorthyTool, thrownEntity.itemToRender) == 0 && EnchantmentHelper.getLevel(WorthyWeapon, thrownEntity.itemToRender) == 0 ){
						return;
					}
					
					if (thrownEntity == null){
						return;
					}

					thrownEntity.ChangeState(4);
					return;
				}
				
				
				
				ItemStack itemstackOrig = client.getMainHandStack();

				if (!(itemstackOrig.getItem() instanceof SwordItem) && !(itemstackOrig.getItem() instanceof MiningToolItem)){
					return;
				}
				
		
				GenericThrownItemEntity e = GenericThrownItemEntity.CreateNew(world, client, itemstackOrig, timeHeld, false);
				
				if (!client.isCreative()){
					itemstackOrig.decrement(1);
				}
				client.swingHand(Hand.MAIN_HAND);
				world.spawnEntity(e);
			
			});



		});
	}
}
