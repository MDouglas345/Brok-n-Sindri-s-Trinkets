package net.fabricmc.Util.ActionKeyHandlerStates;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RecallBEActionKeyHandler {
    static public void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld, BlockPosStackComponent stack, BlockPos BEPosition) {
        if (BEPosition == null){
            return;
        }
        
        GenericItemBlockEntity restingEntity = (GenericItemBlockEntity) world.getBlockEntity(BEPosition);

        if (restingEntity == null){
            return;
        }

        if (!restingEntity.Owner.isOwner(player)){
            stack.Pop(player.getEntityName());
        }

        if (EnchantmentHelper.getLevel(BNSCore.WorthyTool, restingEntity.SavedItem) == 0 && EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, restingEntity.SavedItem) == 0 ){
            stack.Pop(player.getEntityName());
            return;
        }

        int dist = (server.getPlayerManager().getViewDistance()) * 8;
		BlockPos Destination = player.getBlockPos();

        

        if (BEPosition.isWithinDistance(Destination, 4)){
            /**
             * if block is within 2 block lengths of the player, just chuck the item into the player inventory
             */
            if (!player.getInventory().insertStack(restingEntity.SavedItem)){
                return;
            }

            stack.Pop(player.getEntityName());
            
            world.removeBlock(BEPosition, false);
            return;
        }

        stack.Pop(player.getEntityName());

        if (!BEPosition.isWithinDistance(Destination, dist -4)){
            // if the BE is too far!
            Vec3d Direction = Vec3d.of(Destination.subtract(BEPosition));
            Direction = Direction.normalize();
            Direction = Direction.multiply(dist-6);
            Vec3d position = Vec3d.of(Destination).subtract(Direction);

            GenericThrownItemEntity e = GenericThrownItemEntity.CreateNew(world, player, position, restingEntity.SavedItem);

            world.spawnEntity(e);

            e.ChangeState(4);

        }else{
            // if the BE is within range!
            
        
            Vec3d position = Vec3d.of(BEPosition);
        
            GenericThrownItemEntity e = GenericThrownItemEntity.CreateNew(world, player, position, restingEntity.SavedItem);

            world.spawnEntity(e);

            e.ChangeState(4);

        }

        world.removeBlock(BEPosition, false);
        return;

    }
}
