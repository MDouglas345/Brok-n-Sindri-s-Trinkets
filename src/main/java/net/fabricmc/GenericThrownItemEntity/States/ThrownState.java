/**
 * 
 * Perhaps only one state is truly needed : the bouncing mechanic is simply setting the velocity of the masters
 * entity to some smaller tangential trajectory
 * 
 * 
 * Future feature : have all living entites have a itemstack object that takes reference to the item that 
 * was thrown at it, for a feature renderer, and so that when they die, the item drops.
 * possible via mixin!
 * 
 */

package net.fabricmc.GenericThrownItemEntity.States;

import java.util.Random;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericItemBlock.GenericItemBlock;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.StopPanickingTask;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.EnchantmentData;
import net.fabricmc.Util.IMovementStopper;
import net.fabricmc.Util.IPlayerEntityItems;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.Util.Util;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.Enchantments.IWorldBehvaior;


public class ThrownState extends GenericThrownItemEntityState{
    float ageToGrav = 1;
    int p_amount = 5;
    float step = 0.25f;
    public ThrownState(GenericThrownItemEntity m){
        super(m);
        ageToGrav = Master.rotSpeed;
    }

    @Override
    public void Tick() {
        Master.rotoffset -= Master.rotSpeed;
        Master.SuperTick();
        
       
        
        /*
        Quaternion r = Master.originalRot.copy();
        r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(Master.rotoffset, 0,0)));
        this.Master.applyQuaternion(r);
        */

        //Quaternion r = Quaternion.fromEulerXyzDegrees(new Vec3f(Master.getYaw(), Master.getPitch(), 0));
        //r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(Master.rotoffset, 0,0)));
        //Master.applyQuaternion(r);

        //Master.applyRotation(Master.getYaw(), Master.getPitch() - Master.rotSpeed);

        if (Master.age > Master.TimeToGrav){
            Master.setNoGravity(false);
        }

        Vec3d direction = Master.getRotationVector();

        Master.SpawnTrailingParticles();

        
        
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCollision(HitResult hitResult) {

        Master.SuperOnCollision(hitResult);
        
        
       
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        // TODO Auto-generated method stub
        Master.SuperOnBlockHit(blockHitResult);
        //Vec3d toHitBlock = blockHitResult.getPos().subtract(Master.getPos());
        /**
         * problem : weapons when they hit a block dont always look as if they are in contact, or hovering over a block.
         * 
         * idea : calculate directional vector from the (quat * rotoffset), then through iterations move rotoffset
         *  until the dot product is within some margin 
         * 
         * pros with idea : when working, can produce convincing results.
         * 
         * issue with idea : quite expensive and might not be able to handle all cases (i.e -1 dot product result means that a huge value for
         *  correcting is needed)
         * 
         * 
         * 
         * 
         * idea2 : calculate angle between vectors (point of hit - weapon pos) and directional value of quat and set rotoffset to that value
         * 
         * pros with idea2 : will always work
         * 
         * issue with idea2 : might be jarring if the calculated angle is huge / difference between calculated angle and rotoffset is huge.
         * 
         * 
         * get vector from quat :
         *  x = 2 * (x*z + w*y)
         *  y = 2 * (y*z - w*x)
         *  z = 1 - 2 * (x*x + y*y)
         * 
         */
        
        

       /*
            Using the blockhitresult.getSide and getBlockPos together fixes some issues with placing items where they land :
                no more floating items if thrown straight onto the edge of a block

            need to find tune the translation vector so that there is some offset in the block to where it actually landed!

            need to fix the rotation quaternion! Its not quite right.
       */

      //if (!Master.world.isClient){

        /**
         * Make life easy : 
         * 
         * Frost behvaior
         * When a Frost Imbued item hits a block, surrounding blocks will have a change to turn into snow blocks.
         * if the block the item hits is a player placed block, it will not turn into a snow block.
         * make big frost particle effect that goes out wide (perhaps in dependent on the side hit) // VISUAL
         */

        BlockState b = Master.world.getBlockState(blockHitResult.getBlockPos());
        if (b.getBlock() instanceof GenericItemBlock){
            // if this entity hits a genericitemblock, bounce.
            Master.ThrowRandom(0.3f);

            return;
        }
        
        
       
    
       

        BlockPos hitpos = Util.getAdjacentBlock(blockHitResult.getBlockPos(), blockHitResult.getSide());
        BlockState state = Master.world.getBlockState(hitpos);
        Block block = state.getBlock();

        
        //block.replace(state, new BlockState(block, , mapCodec), world, pos, flags);

        
        
        //something better would be to check if the blockstate.isAir? or isSolidBlock? maybe better.
        
        if (  !(block instanceof AirBlock) && !(block instanceof FluidBlock) && !(block instanceof PlantBlock)){
            Master.ThrowRandom(0.3f);

            return;
        }

       
      /**
       * This is where environmental effects will come into place. 
       * Look into creating custom behavior in the Enchantment classes's themselves and just call it here!
       */
        
       EnchantmentData enchantmentData = Util.getSpecialThrownEnchantment(Master.itemToRender);

       if (enchantmentData != null){
           IWorldBehvaior worldBehvaior = (IWorldBehvaior)(enchantmentData.enchantment);
           worldBehvaior.OnBlockThrownHit(Master.world, hitpos, enchantmentData.level);
       }
        
        
        if (!Master.world.isClient){
        Quaternion q = blockHitResult.getSide().getRotationQuaternion();

        q.hamiltonProduct(Master.originalRot);
        

        

        Master.world.setBlockState(hitpos,BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());

        
            GenericItemBlockEntity be = (GenericItemBlockEntity)Master.world.getBlockEntity(hitpos);
            
            
            
            /**
             * yet another bug : Master.world is universal to both nether and overworld (with bias to overworld?)
             * meaning : no matter whether the player is in the nether or overworld, the entity is registered as if in both, 
             * so when players try to recall their weapon in the overworld, it will prioritze a weapon that may not be in the overworld
             * thus getting stuck and player not able to recall anything.
             * 
             * solution? find a way to get the player's (thrower's) world and register the stack stuff there.
             */

           

            BNSCore.removeEntityFromStack((ServerWorld) Master.world, Master.Owner.name, Master.StackID);

            int id = BNSCore.pushBEOntoStack((ServerWorld) Master.world, Master.Owner.name, hitpos);

            be.Initalize(Master.itemToRender, q, (float)Util.getRandomDouble(100, 200),  id, Master.Owner);

            Master.kill();
        }
       // }
       
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
        Master.SuperOnEntityHit(entityHitResult);

            if(Master.Maxed && (EnchantmentHelper.getLevel(BNSCore.PinnedTool, Master.itemToRender) > 0 || EnchantmentHelper.getLevel(BNSCore.PinnedWeapon, Master.itemToRender) > 0)){
                //Pin the entity.
                /**
                 * if the entity is a mob, set persistence!
                 *  ((MobEntity)entity).setPersistent();
                 */
                
                try{
                    LivingEntity e = (LivingEntity) entityHitResult.getEntity();
                    
                    ISavedItem eSaved = (ISavedItem) e;

                    if (eSaved.getSavedItem().getItem() != Items.AIR){
                        Master.Attack(entityHitResult);
                        return;
                    }

                    if (!Master.world.isClient){
                        
                        eSaved.setSavedItem(Master.itemToRender);
                        eSaved.setSavedItemOwner(Master.Owner.name);

                        // cant be sure if this will be an issue if done only on server
                       

				        int id = BNSCore.pushEntityOntoStack((ServerWorld)Master.world, Master.Owner.name, e.getUuid());
                        eSaved.setIndexIntoStack(id);

                        BNSCore.removeEntityFromStack((ServerWorld)Master.world, Master.Owner.name, Master.StackID);
                    }
                    
                    if (e instanceof MobEntity){
                        ((MobEntity) e).setPersistent();
                    }



                    Master.kill();
                }
                catch(Exception e){

                }
            }

            try{
                if (!Master.Owner.isOwner((PlayerEntity)entityHitResult.getEntity())){

                    Master.Attack(entityHitResult);
                }
            }
            catch(Exception e){
                Master.Attack(entityHitResult);
            }
        
        
            /*
            if (Util.randgen.nextFloat() > 0.8){
                StuckState s = (StuckState) Master.States[1];
                s.setStuckEntity(entityHitResult.getEntity());
                Master.ChangeState(1);
           }
           else{
               
              
               //Master.ChangeState(2);
           }
           */
        

        
        
    }

 
}
