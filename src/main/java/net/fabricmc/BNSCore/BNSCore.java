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
 * 
 * 
 * 
 * new task!
 * Create a way to determin whether a block was placed by a player. Reason is to prevent replacing blocks that can introduce griefin? 
 * 
 * Approach : Use the world components from cardinal components to save data about every placed block, and whether they were placed by player
 * 
 * Data structure : A hashmap that uses the XYZ coord : boolean for quick loop up.
 * 
 * Adding to data structure : ? Find out where the player places a block and mixinto that
 * 
 * Removing from data structure : Mixin into Block.onBreak() and cast the object to the interface for removing.
 * 
 * Obtaining value : Given a block pos, return the boolean, or null.
 * 
 * 
 * Reminder! Check for bugf with creeper exploding with a stuck item. it causes issues with recalling weapon.
 * 
 * New bug found : When player is very far from their respawn point and they die with worhty weapons,
 * there is a chance the GenericThrownItemEntities will be despawn, without settling and creating permanent 
 * Block Entities. 
 * 
 * work around : when a player dies, brute force creating these BE all around instead of creating thrown entities.
 * Cons : Jarring AF and unsatisfactory.
 * 
 * Look into creating a mixin to determin whether a chunk is loaded or not. Could be useful for making things smoother
 * 
 * 
 * Reminder : When changing mc versions within VSCode F1 -> Java : Clean Workplace -> restart and delete
 * 
 * 
 * ServerChunkEvents -> this is called when a chunk is loaded! Need to find something that triggers when a chunk is unloaded!
 * 
 * CHUNK_UNLOAD.regsiter((chunk, world) -> {}); is the callback!
 */


package net.fabricmc.BNSCore;

import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Items.ItemRegistry;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.fabricmc.LootTables.LootTableRegistry;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Sounds.SoundRegistry;
import net.fabricmc.StaticFireBlock.AdvStaticFireBlock;
import net.fabricmc.StaticFireBlock.BaseStaticFireBlock;
import net.fabricmc.StaticFireBlock.StaticFireBlock;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.GlobalPosRecordComponent;
import net.fabricmc.CardinalComponents.PinnedEntityComponent;
import net.fabricmc.CardinalComponents.PlayerBlockComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.WeaponStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.Config.ConfigRegistery;
import net.fabricmc.DwarvenForgeBlock.DwarvenForgeBlock;
import net.fabricmc.DwarvenForgeBlock.DwarvenForgeBlockEntity;
import net.fabricmc.Effects.ParalysisEffect;
import net.fabricmc.Enchantments.FlameEnchantment.FlameToolEnchantment;
import net.fabricmc.Enchantments.FlameEnchantment.FlameWeaponEnchantment;
import net.fabricmc.Enchantments.FrostEnchantment.FrostToolEnchantment;
import net.fabricmc.Enchantments.FrostEnchantment.FrostWeaponEnchantment;
import net.fabricmc.Enchantments.LightningEnchantment.LightningToolEnchantment;
import net.fabricmc.Enchantments.LightningEnchantment.LightningWeaponEnchantment;
import net.fabricmc.Enchantments.PinnedEnchantment.PinnedToolEnchantment;
import net.fabricmc.Enchantments.PinnedEnchantment.PinnedWeaponEnchantment;
import net.fabricmc.Enchantments.ThrowEnchantment.ThrowToolEnchantment;
import net.fabricmc.Enchantments.ThrowEnchantment.ThrowWeaponEnchantment;
import net.fabricmc.Enchantments.WorthyEnchantment.WorthyToolEnchantment;
import net.fabricmc.Enchantments.WorthyEnchantment.WorthyWeaponEnchantment;
import net.fabricmc.Entity.EntityRegistry;
import net.fabricmc.Entity.ScheduleRegistry.ScheduleRegistry;
import net.fabricmc.Events.EventsRegistry;
import net.fabricmc.GenericItemBlock.*;
import net.fabricmc.Util.EntityContainer;
import net.fabricmc.Util.IDedUUID;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.Util.NetworkHandlerServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;



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
					.trackRangeBlocks(100).trackedUpdateRate(20) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
					.build() // VERY IMPORTANT DONT DELETE FOR THE LOVE OF GOD PSLSSSSSS
	);

	public static final Block GENERIC_ITEM_BLOCK = new GenericItemBlock(FabricBlockSettings.of(Material.METAL).strength(-1.0f, 9999999f).nonOpaque());

	public static BlockEntityType<GenericItemBlockEntity> GENERIC_ITEM_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(GenericItemBlockEntity::new, GENERIC_ITEM_BLOCK).build();


	public static final Block DWARVEN_FORGE_BLOCK= new DwarvenForgeBlock(FabricBlockSettings.of(Material.METAL).strength(1).requiresTool().nonOpaque());

	public static BlockEntityType<DwarvenForgeBlockEntity> DWARVEN_FORGE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(DwarvenForgeBlockEntity::new, DWARVEN_FORGE_BLOCK).build();


	public static final Block BASE_STATIC_FIRE_BLOCK = new BaseStaticFireBlock(FabricBlockSettings.of(Material.FIRE, MapColor.BRIGHT_RED).breakInstantly().luminance(15).nonOpaque().noCollision());

	public static final Block ADV_STATIC_FIRE_BLOCK = new AdvStaticFireBlock(FabricBlockSettings.of(Material.FIRE, MapColor.BRIGHT_RED).breakInstantly().luminance(15).nonOpaque().noCollision());


	public static Enchantment WorthyWeapon = Registry.register(Registry.ENCHANTMENT, 
												new Identifier(BNSCore.ModID, "worthyweapon"),
												new WorthyWeaponEnchantment());

	public static Enchantment WorthyTool = Registry.register(Registry.ENCHANTMENT, 
												new Identifier(BNSCore.ModID, "worthytool"),
												new WorthyToolEnchantment());

	public static Enchantment PinnedWeapon  = Registry.register(Registry.ENCHANTMENT, 
												new Identifier(BNSCore.ModID, "pinnedweapon"),
												new PinnedWeaponEnchantment());

	public static Enchantment PinnedTool  = Registry.register(Registry.ENCHANTMENT, 
												new Identifier(BNSCore.ModID, "pinnedtool"),
												new PinnedToolEnchantment());
	
	public static Enchantment FlameWeapon = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "flameweapon"),
												new FlameWeaponEnchantment());

	public static Enchantment FlameTool = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "flametool"),
												new FlameToolEnchantment());
	
	public static Enchantment FrostWeapon = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "frostweapon"),
												new FrostWeaponEnchantment());

	public static Enchantment FrostTool = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "frosttool"),
												new FrostToolEnchantment());
									
	public static Enchantment LightningWeapon = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "lightningweapon"),
												new LightningWeaponEnchantment());

	public static Enchantment LightningTool = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "lightningtool"),
												new LightningToolEnchantment());

	public static Enchantment ThrowWeapon = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "throwweapon"),
												new ThrowWeaponEnchantment());

	public static Enchantment ThrowTool = Registry.register(Registry.ENCHANTMENT,
												new Identifier(BNSCore.ModID, "throwtool"),
												new ThrowToolEnchantment());
									
	


	public static final StatusEffect Paralysis = new ParalysisEffect();
 
					


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		BNSCore.LOGGER.info("In the main");

		ConfigRegistery.initConfig();

		Registry.register(Registry.BLOCK, new Identifier(ModID, "generic_item_block"), GENERIC_ITEM_BLOCK);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ModID, "generic_item_block_entity"), GENERIC_ITEM_BLOCK_ENTITY);

		Registry.register(Registry.BLOCK, new Identifier(ModID, "dwarven_forge_block"), DWARVEN_FORGE_BLOCK);

		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(ModID, "dwarven_forge_block_entity"), DWARVEN_FORGE_BLOCK_ENTITY);

		Registry.register(Registry.BLOCK, new Identifier(ModID, "base_static_fire_block"), BASE_STATIC_FIRE_BLOCK);

		Registry.register(Registry.BLOCK, new Identifier(ModID, "adv_static_fire_block"), ADV_STATIC_FIRE_BLOCK);


		Registry.register(Registry.STATUS_EFFECT, new Identifier("bns", "paralysis"), Paralysis);

		CommandRegistrationCallback.EVENT.register((dispatcher,  dedicated) -> {
            
                dispatcher.register(CommandManager.literal("bns").then(CommandManager.literal("resetthrownstacks")
				.executes(context -> {
					
					MinecraftServer server = context.getSource().getServer();
					BNSCore.resetStacks(server.getWorld(World.OVERWORLD));
					BNSCore.resetStacks(server.getWorld(World.NETHER));
					BNSCore.resetStacks(server.getWorld(World.END));
					

					BNSCore.LOGGER.info("Resetting Stacks!");
					return 1;
				
				})));
        });

		NetworkHandlerServer.registerServerResponses();
	
		ParticleRegistery.registerParticles();

		SoundRegistry.registerSounds();

		EntityRegistry.registerAttributes();

		EventsRegistry.register();

		ItemGroupRegistry.register();

		ItemRegistry.register();

		ScheduleRegistry.register();

		LootTableRegistry.register();

		
	}

	public static void removeEntityFromStack(ServerWorld world, String name, int id){
			//UUIDStackComponent uuidstack = mycomponents.EntityUUIDs.get(world.getLevelProperties());
			//UUIDStackComponent uuidstack = mycomponents.EntityUUIDs.get(world);
			WeaponStackComponent uuidstack = mycomponents.WeaponStacks.get(world);

            uuidstack.Remove(name, id);
	}

	public static int pushEntityOntoStack(ServerWorld world, String name, IDedUUID uuid){
				//UUIDStackComponent stack = mycomponents.EntityUUIDs.get(world.getLevelProperties());
				//UUIDStackComponent stack = mycomponents.EntityUUIDs.get(world);
				WeaponStackComponent stack = mycomponents.WeaponStacks.get(world);
				int id = stack.Push(name, uuid);
				return id;
	}

	public static void removeBEFromStack(ServerWorld world, String name, int id){
		//BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world.getLevelProperties());
		//BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world);
		WeaponStackComponent stack = mycomponents.WeaponStacks.get(world);

        stack.Remove(name, id);
	}

	public static void removeDWFromStack(ServerWorld world, String name, int id){
		//BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world.getLevelProperties());
		GlobalPosRecordComponent stack = mycomponents.DwarvenForges.get(world);

        stack.Remove(name, id);
	}

	public static int pushBEOntoStack(ServerWorld world, String name, BlockPos hitpos){
		//BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world.getLevelProperties());
		//BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world);
		WeaponStackComponent stack = mycomponents.WeaponStacks.get(world);

		IDedUUID idDedUUID = new IDedUUID(0, hitpos);
        int id = stack.Push(name, idDedUUID);

		return id;
	}


	public static int pushDFOntoStack(ServerWorld world, String name, BlockPos hitpos){
		//BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world.getLevelProperties());
		GlobalPosRecordComponent stack = mycomponents.DwarvenForges.get(world);

            int id = stack.Push(name, hitpos);

			return id;
	}

	public static void removePlayerBlock(ServerWorld world, String name){
		PlayerBlockComponent stack = BNSCore.getPlayerBlockStack(world);

		stack.Remove(name, 0);
	}

	public static void pushPlayerBlock(ServerWorld world, String name, boolean value){
		PlayerBlockComponent stack = BNSCore.getPlayerBlockStack(world);

		stack.Push(name, value);
	}

	public static boolean getPlayerBlock(ServerWorld world, String name){
		PlayerBlockComponent stack = BNSCore.getPlayerBlockStack(world);
		Boolean res = stack.Peek(name); 
		return res == null ? false : res;
	}

	public static void pushPinnedEntity(ServerWorld world, LivingEntity entity){
		PinnedEntityComponent stack = getPinnedEntityComponent(world);

		ISavedItem e = (ISavedItem)entity;

		EntityContainer container = new EntityContainer(entity.getUuid(), entity.getBlockPos(), e.getSavedItem(), e.getSavedItemOwner());

		stack.Push(container.uuid, container);
	}

	public static void removePinnedEntity(ServerWorld world, LivingEntity entity){
		removePinnedEntity(world, entity.getUuid());
	}

	public static void removePinnedEntity(ServerWorld world, UUID uuid){
		PinnedEntityComponent stack = getPinnedEntityComponent(world);

		stack.Remove(uuid, 0);
	}

	public static EntityContainer getPinnedEntity(ServerWorld world, UUID key){
		PinnedEntityComponent stack = getPinnedEntityComponent(world);

		return stack.Peek(key);
	}


	public static void updatePinnedEntity(ServerWorld world, UUID key, EntityContainer value){
		PinnedEntityComponent stack = getPinnedEntityComponent(world);

		stack.Push(key, value);
	}


	public static void resetStacks(ServerWorld world){
		BlockPosStackComponent bstack = BNSCore.getBlockStack(world);
		UUIDStackComponent eStack = BNSCore.getEntitytack(world);
		GlobalPosRecordComponent pos = BNSCore.getDwarvenForgeStack(world);
		PinnedEntityComponent pinned = getPinnedEntityComponent(world);

		bstack.Reset();
		eStack.Reset();
		pos.Reset();
		pinned.Reset();

	}

	public static void resetDwarvenBlocks(ServerWorld world){
		GlobalPosRecordComponent list = BNSCore.getDwarvenForgeStack(world);

		list.Reset();
	}

	public static BlockPosStackComponent getBlockStack(ServerWorld world){
		return  mycomponents.BlockEntityPositions.get(world);
	}

	public static UUIDStackComponent getEntitytack(ServerWorld world){
		return  mycomponents.EntityUUIDs.get(world);
	}

	public static GlobalPosRecordComponent getDwarvenForgeStack(ServerWorld world){
		return  mycomponents.DwarvenForges.get(world);
	}

	public static PinnedEntityComponent getPinnedEntityComponent(ServerWorld world){
		return mycomponents.PinnedEntities.get(world);
	}

	public static PlayerBlockComponent getPlayerBlockStack(ServerWorld world){
		return mycomponents.PlayerBlocks.get(world);
		//return null;
	}



	public void HandleBlockRecall(ServerWorld world){
		
	}	
}
