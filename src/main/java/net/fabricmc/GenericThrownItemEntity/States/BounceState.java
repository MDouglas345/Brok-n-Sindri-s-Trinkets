package net.fabricmc.GenericThrownItemEntity.States;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class BounceState  extends GenericThrownItemEntityState{

    public BounceState(GenericThrownItemEntity m) {
        super(m);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void Tick() {
        // TODO Auto-generated method stub
        Master.SuperTick();
        
    }

    @Override
    public void onCollision(HitResult hitResult) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        // TODO Auto-generated method stub
        //Master.world.setBlockState(blockHitResult.getBlockPos(),BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());
        //Master.ChangeState(3);
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
        
        Master.Attack(entityHitResult);
    }
    
}
