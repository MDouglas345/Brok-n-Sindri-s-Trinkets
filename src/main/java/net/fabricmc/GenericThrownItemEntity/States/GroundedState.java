package net.fabricmc.GenericThrownItemEntity.States;

import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class GroundedState extends GenericThrownItemEntityState {

    public GroundedState(GenericThrownItemEntity m) {
        super(m);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void Tick() {
        // TODO Auto-generated method stub
        if (Master.world.getBlockState(Master.getBlockPos().down()).isAir()){
           // Master.ChangeState(0);
        }
        
    }

    @Override
    public void onCollision(HitResult hitResult) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
        
    }
    
}
