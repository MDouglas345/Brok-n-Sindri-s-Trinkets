package net.fabricmc.GenericThrownItemEntity.States;

import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Particles.ParticleRegistery;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class ReturnState extends GenericThrownItemEntityState {

    public double originaldist = 0;
    PlayerEntity target;

    public ReturnState(GenericThrownItemEntity m) {
        super(m);
        //TODO Auto-generated constructor stub

    }

    @Override
    public void Tick() {
        // TODO Auto-generated method stub
        Master.rotoffset += Master.rotSpeed;
        Master.SuperTick();
            
           // Vec3d Destination = Master.getOwner().getPos().add(new Vec3d(0,1,0.5f));
           if (target == null){
               target = Master.world.getPlayerByUuid(Master.Owner.ID);
            if ( target == null){
                Master.ChangeState(0);
                return;
            }
        }

           Vec3d Destination = target.getPos().add(new Vec3d(0,1,0));

            Vec3d DesiredDir = Master.getPos().subtract(Destination).normalize().negate();

            double newdist = Destination.squaredDistanceTo(Master.getPos());
            newdist = newdist / originaldist;

            DesiredDir = DesiredDir.multiply(Math.max(0.8, newdist * 2));

            DesiredDir = Master.getVelocity().lerp(DesiredDir, 0.8f);

            Master.setVelocity(DesiredDir);

          
            Master.SpawnTrailingParticles();

        
        
        
    }

    @Override
    public void onCollision(HitResult hitResult) {
        // TODO Auto-generated method stub
       Master.SuperOnCollision(hitResult);
        
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
       
        try{
            PlayerEntity entity = (PlayerEntity) entityHitResult.getEntity();

            if (Master.Owner.isOwner((PlayerEntity)entityHitResult.getEntity())){

                if (!Master.world.isClient){
                    if (!entity.getInventory().insertStack(Master.itemToRender)){
                        Master.ChangeState(0);
                        
                    }
                    else{

                        Master.kill();
                    }

                }
                
            }
            else{

                Master.Attack(entityHitResult);
            }
        }
        catch(Exception e){

            Master.Attack(entityHitResult);
        }
    }

    @Override
    public void OnEnter(){
        target = Master.world.getPlayerByUuid(Master.Owner.ID);
        Vec3d Destination = Master.world.getPlayerByUuid(Master.Owner.ID).getPos().add(new Vec3d(0,1,0));

        originaldist = Destination.squaredDistanceTo(Master.getPos());
    }
   
        
       
    
    
}
