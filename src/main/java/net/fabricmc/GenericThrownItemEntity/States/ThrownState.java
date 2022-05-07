package net.fabricmc.GenericThrownItemEntity.States;

import java.util.Random;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;

import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.Util.Util;

public class ThrownState extends GenericThrownItemEntityState{

    public ThrownState(GenericThrownItemEntity m){
        super(m);
    }

    @Override
    public void Tick() {
        
        Master.rotoffset -= Master.rotSpeed;
        Master.SuperTick();
        
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
        
        //Quaternion q = Master.originalRot.copy();
        //q.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(Master.rotoffset, 0,0)));

        //Vec3d quatDir = Util.getVec3FromQuat(q); // assume this works
        //quatDir.multiply(-1);

        //Vec3d toHitPos = Master.getPos().subtract(blockHitResult.getPos());
        //BNSCore.LOGGER.info(quatDir.toString());


       // q.hamiltonProduct(blockHitResult.getSide().getRotationQuaternion());
        Quaternion q = blockHitResult.getSide().getRotationQuaternion();

        q.hamiltonProduct(this.Master.originalRot);

        Master.world.setBlockState(this.Master.getBlockPos(),BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());
        GenericItemBlockEntity be = (GenericItemBlockEntity)Master.world.getBlockEntity(this.Master.getBlockPos());
        //be.Initalize(Master.itemToRender, Master.originalRot, Master.rotoffset);
        


        be.Initalize(Master.itemToRender, q, (float)Util.getRandomDouble(100, 200));
        
        
        Master.ChangeState(3);

        Master.kill();
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
        Master.SuperOnEntityHit(entityHitResult);

        if (Master.world.random.nextFloat() > 0.8){
             StuckState s = (StuckState) Master.States[1];
             s.setStuckEntity(entityHitResult.getEntity());
             Master.ChangeState(1);
        }
        else{
            
            Vec3d dir = new Vec3d(Master.world.random.nextFloat(), Master.world.random.nextFloat(), Master.world.random.nextFloat()).normalize();
            dir.multiply(Master.getVelocity().length() * 0.002f);
            Master.setVelocity(dir);
            Master.ChangeState(2);
        }
        
    }

 
}
