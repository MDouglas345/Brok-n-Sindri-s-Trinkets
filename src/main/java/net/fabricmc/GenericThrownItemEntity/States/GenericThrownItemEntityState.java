package net.fabricmc.GenericThrownItemEntity.States;

import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public abstract class GenericThrownItemEntityState {
    protected GenericThrownItemEntity Master;

    public GenericThrownItemEntityState(GenericThrownItemEntity m){
        Master = m;
    }

    public  abstract void Tick();

    public abstract void onCollision(HitResult hitResult);

    public abstract void onBlockHit(BlockHitResult blockHitResult);

    public abstract void onEntityHit(EntityHitResult entityHitResult);

    public void OnEnter(){

    }

    public void OnExit(){
        
    }
}
