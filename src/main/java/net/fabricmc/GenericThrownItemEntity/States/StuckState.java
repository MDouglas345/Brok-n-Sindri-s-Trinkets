package net.fabricmc.GenericThrownItemEntity.States;

import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;


public class StuckState extends GenericThrownItemEntityState{

    private Entity StuckOnto;
    private Vec3d Offset;
    private Vec3d OriginalPos;
    

    public StuckState(GenericThrownItemEntity m) {
        super(m);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void Tick() {
        // TODO Auto-generated method stub
        if (!Master.world.isClient){
            Vec3d newPos = StuckOnto.getPos();
            Master.setPos(newPos.x, newPos.y, newPos.z);
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

    public void setStuckEntity(Entity e){
        StuckOnto = e;
    }
    
}
