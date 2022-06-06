package net.fabricmc.Util.ActionKeyHandlerStates;

import java.util.UUID;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.ISavedItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RecallEntityActionKeyHandler {

    static public void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld, UUIDStackComponent stack, UUID entityuuid) {
        Entity e = world.getEntity(entityuuid);

        if (e == null){
            stack.Pop(player.getEntityName());
            return;
        }

        stack.Pop(player.getEntityName());

        ISavedItem itemEnt = (ISavedItem)e;
        ItemStack savedItem = itemEnt.getSavedItem();

        if (itemEnt.getSavedItem().getItem().equals(Items.AIR)){
            stack.Pop(player.getEntityName());
            return;
        }

        if (EnchantmentHelper.getLevel(BNSCore.WorthyTool,savedItem) == 0 && EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, savedItem) == 0 ){
            return;
        }
        
        if (e instanceof GenericThrownItemEntity){
            // this entity is a thrown entity and is easier to recall.

           HandleEntity(server, player, world, (GenericThrownItemEntity) e, savedItem);

            return;
        }

        // This is a pinned item inside a living entity.

        HandlePinnedEntity(server, player, world, e, savedItem);

    }


    public static void HandleEntity(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, GenericThrownItemEntity thrownEntity, ItemStack savedItem) {

						
        if (thrownEntity == null){
            return;
        }

        BlockPos Destination = player.getBlockPos();
        if (thrownEntity.getBlockPos().isWithinDistance(Destination, 4)){
            /**
             * if block is within 2 block lengths of the player, just chuck the item into the player inventory
             */
            if (!player.getInventory().insertStack(savedItem)){
                return;
            }
            
            
            thrownEntity.kill();
            
        }

        thrownEntity.ChangeState(4);
    }

    public static void HandlePinnedEntity(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, Entity e, ItemStack savedItem) {
        BlockPos Destination = player.getBlockPos();

        ISavedItem inter = (ISavedItem)e;

       

        double distance = e.getBlockPos().getSquaredDistance(player.getBlockPos());
        distance = MathHelper.sqrt((float) distance);
        
        if (distance < 4){
            /**
             * if block is within 2 block lengths of the player, just chuck the item into the player inventory
             */
            if (!player.getInventory().insertStack(savedItem)){
                return;
            }
            
           
        

            // remove the paralysis effect from e
            // remove stuck item details from e
            LivingEntity living = (LivingEntity)e;
            
            living.removeStatusEffect(BNSCore.Paralysis);
            inter.setSavedItem(new ItemStack(Items.AIR,1));
            inter.setSavedItemOwner("");
            
            
            return;
            
            
        }

        double serverViewDistance =  server.getPlayerManager().getViewDistance() * 8;

        if (distance > serverViewDistance){
            // Too far to create a thrown item entity, need to make adjustmets first :
            Vec3d Direction = player.getPos().subtract(e.getPos());
            Direction = Direction.normalize();
    
            int dist = (server.getPlayerManager().getViewDistance()) * 8;
            Direction = Direction.multiply(dist-6);
    
            Vec3d position = Vec3d.of(Destination).subtract(Direction);
            GenericThrownItemEntity thrown = GenericThrownItemEntity.CreateNew(world, player,position, inter.getSavedItem());
            world.spawnEntity(thrown);
            thrown.ChangeState(4);
        }
        else{
            // Entity is within range to create a thrown item entity.

            GenericThrownItemEntity thrown = GenericThrownItemEntity.CreateNew(world, player, e.getPos(), inter.getSavedItem());
            world.spawnEntity(thrown);
            thrown.ChangeState(4);
        }
       
        

        // remove the paralysis effect from e
        // remove stuck item details from e
        LivingEntity living = (LivingEntity)e;
        living.removeStatusEffect(BNSCore.Paralysis);
        inter.setSavedItem(new ItemStack(Items.AIR,1));
        inter.setSavedItemOwner("");
        return;
    }
}
