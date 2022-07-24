package net.fabricmc.Util.ActionKeyHandlerStates;

import java.util.UUID;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.WeaponStackComponent;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.EntityContainer;
import net.fabricmc.Util.IDedUUID;
import net.fabricmc.Util.ISavedItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public class RecallEntityActionKeyHandler {

    static public void handle(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, float ticksHeld, WeaponStackComponent stack, IDedUUID ideduuid) {

        if (!HandlePinnedEntity(server, player, world, ideduuid, stack)){
            HandleEntity(server, player, world, ideduuid, stack);    
        }
        

    }

    public static boolean isValidRecall(ServerPlayerEntity player, ItemStack stack, String Owner){
        if (!player.getEntityName().equals(Owner)){
            return false;
        }

        if (EnchantmentHelper.getLevel(BNSCore.WorthyTool,stack) == 0 && EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, stack) == 0 ){
            return false;
        }
        

        return true;
    }


    public static boolean HandleEntity(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, IDedUUID ideduuid, WeaponStackComponent stack) {

        Entity e = world.getEntity(ideduuid.uuid);
        

        if (e == null){
            
            BNSCore.LOGGER.info("Did not find entity");
            stack.Pop(player.getEntityName());
            return false;
            
           
        }

        if (!(e instanceof GenericThrownItemEntity)){
            stack.Pop(player.getEntityName());
            return false;
        }

		GenericThrownItemEntity thrownEntity = (GenericThrownItemEntity)e;		

        if (thrownEntity == null){
            stack.Pop(player.getEntityName());
            return false;
        }

        if (!isValidRecall(player, thrownEntity.itemToRender, thrownEntity.Owner.name)){
            stack.Pop(player.getEntityName());
            return true;
        }

        BlockPos Destination = player.getBlockPos();
        if (thrownEntity.getBlockPos().isWithinDistance(Destination, 4)){
            /**
             * if block is within 2 block lengths of the player, just chuck the item into the player inventory
             */
            if (!player.getInventory().insertStack(thrownEntity.itemToRender)){
                return true;
            }
            
            
            thrownEntity.kill();
            stack.Pop(player.getEntityName());
            return true;
            
        }

        thrownEntity.ChangeState(4);
        stack.Pop(player.getEntityName());
        return true;
    }






    public static boolean HandlePinnedEntity(MinecraftServer server, ServerPlayerEntity player, ServerWorld world, IDedUUID ideduuid, WeaponStackComponent stack) {
        BlockPos Destination = player.getBlockPos();

       EntityContainer container = BNSCore.getPinnedEntity(world, ideduuid.uuid);


       if (container == null){
        return false;
       }

       LivingEntity e = (LivingEntity) world.getEntity(ideduuid.uuid);

       if (!isValidRecall(player, container.Stack, container.Owner.name)){
        return true;
       }

        double distance = container.pos.getSquaredDistance(player.getBlockPos());
        distance = MathHelper.sqrt((float) distance);
        
        if (distance < 4){
            /**
             * if block is within 2 block lengths of the player, just chuck the item into the player inventory
             */
            if (!player.getInventory().insertStack(container.Stack)){
                return true;
            }
            
           
        

            // remove the paralysis effect from e
            // remove stuck item details from e
            container.Stack = new ItemStack(Items.AIR);
            container.Owner.name = "";


            BNSCore.updatePinnedEntity(world, ideduuid.uuid, container);

            /*
            LivingEntity living = (LivingEntity)e;
            
            living.removeStatusEffect(BNSCore.Paralysis);
            inter.setSavedItem(new ItemStack(Items.AIR,1));
            inter.setSavedItemOwner("");
            */
            
            stack.Pop(player.getEntityName());

            if (e != null){
                ISavedItem inter = (ISavedItem)e;
                inter.forceupdate();
            }
            return true;
            
            
        }

        double serverViewDistance =  server.getPlayerManager().getViewDistance() * 8;

        if (distance > serverViewDistance){
            // Too far to create a thrown item entity, need to make adjustmets first :
            Vec3d Direction = player.getPos().subtract(Vec3d.of(container.pos));
            Direction = Direction.normalize();
    
            int dist = (server.getPlayerManager().getViewDistance()) * 8;
            Direction = Direction.multiply(dist-6);
    
            Vec3d position = Vec3d.of(Destination).subtract(Direction);
            GenericThrownItemEntity thrown = GenericThrownItemEntity.CreateNew(world, player,position, container.Stack);
            world.spawnEntity(thrown);
            thrown.ChangeState(4);
        }
        else{
            // Entity is within range to create a thrown item entity.

            GenericThrownItemEntity thrown = GenericThrownItemEntity.CreateNew(world, player, Vec3d.of(container.pos), container.Stack);
            world.spawnEntity(thrown);
            thrown.ChangeState(4);
        }
       
        

        // remove the paralysis effect from e
        // remove stuck item details from e


        container.Stack = new ItemStack(Items.AIR);
        container.Owner.name = "";

        BNSCore.updatePinnedEntity(world, ideduuid.uuid, container);
        stack.Pop(player.getEntityName());
        if (e != null){
            ISavedItem inter = (ISavedItem)e;
            inter.forceupdate();
        }
        return true;
    }
}
