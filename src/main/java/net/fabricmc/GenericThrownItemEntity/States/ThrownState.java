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
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.Util.IPlayerEntityItems;
import net.fabricmc.Util.Util;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;

public class ThrownState extends GenericThrownItemEntityState{

    public ThrownState(GenericThrownItemEntity m){
        super(m);
    }

    @Override
    public void Tick() {
       

        Master.rotoffset -= Master.rotSpeed;
        Master.SuperTick();

        if (Master.age > 30){
            Master.setNoGravity(false);
        }
        
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

       if (!Master.world.isClient){
            Quaternion q = blockHitResult.getSide().getRotationQuaternion();

            BlockPos hitpos = Util.getAdjacentBlock(blockHitResult.getBlockPos(), blockHitResult.getSide());

            //IPlayerEntityItems player = (IPlayerEntityItems)(PlayerEntity)Master.getOwner();

            //player.addReturnableItem(hitpos);


            //blockHitResult.withSide(blockHitResult.getSide());

            q.hamiltonProduct(this.Master.originalRot);

            

            Master.world.setBlockState(hitpos,BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());
            GenericItemBlockEntity be = (GenericItemBlockEntity)Master.world.getBlockEntity(hitpos);
            //be.Initalize(Master.itemToRender, Master.originalRot, Master.rotoffset);
        
            //be.Initalize(Master.itemToRender, q, (float)Util.getRandomDouble(100, 200),  player.getStack().size()-1, Master.Owner);
        
        
            //Master.ChangeState(3);

            BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(Master.world.getLevelProperties());

            UUIDStackComponent uuidstack = mycomponents.EntityUUIDs.get(Master.world.getLevelProperties());

            uuidstack.Remove(Master.Owner.name, Master.StackID);

            int id = stack.Push(Master.getOwner().getEntityName(), hitpos);

            be.Initalize(Master.itemToRender, q, (float)Util.getRandomDouble(100, 200),  id, Master.Owner);

            Master.kill();
       }
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
        Master.SuperOnEntityHit(entityHitResult);

        

        
            Master.Attack(entityHitResult);
        
        
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
