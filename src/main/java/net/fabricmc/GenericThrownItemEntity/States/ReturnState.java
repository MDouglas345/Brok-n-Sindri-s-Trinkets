package net.fabricmc.GenericThrownItemEntity.States;


import net.fabricmc.Config.ConfigReader;
import net.fabricmc.Config.ConfigRegistery;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.PlantBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ReturnState extends GenericThrownItemEntityState {

    public double originaldist = 0;
    PlayerEntity target;
    float returnspeed;

    public ReturnState(GenericThrownItemEntity m) {
        super(m);
        //TODO Auto-generated constructor stub
        returnspeed = (float) ConfigRegistery.configuration.getDouble("ReturnSpeed");
    }

    @Override
    public void Tick() {
        // TODO Auto-generated method stub
        Master.rotoffset = MathHelper.lerp(0.8f, Master.rotoffset, Master.rotoffset + Master.rotSpeed);
        Master.SuperTick();
            
        
           // Vec3d Destination = Master.getOwner().getPos().add(new Vec3d(0,1,0.5f));
           if (target == null){
               target = Master.world.getPlayerByUuid(Master.Owner.ID);
            if ( target == null){
                Master.ChangeState(0);
                return;
            }
        }

           Vec3d Destination = target.getPos().add(new Vec3d(0,1.5f,0));

            Vec3d DesiredDir = Master.getPos().subtract(Destination).normalize().negate();

            // experimental custom rotation turning.
            Master.originalRot = Util.getDirectionalRotation(new Vec3d(0,0,1), DesiredDir);
            

            double newdist = Destination.squaredDistanceTo(Master.getPos());
            newdist = newdist / originaldist;

            DesiredDir = DesiredDir.multiply(Math.max(returnspeed, newdist * 2));

            DesiredDir = Master.getVelocity().lerp(DesiredDir, 0.2f);

            Master.setVelocity(DesiredDir);

            Master.SpawnTrailingParticles();


            if (!Master.world.isClient){
                if (target.distanceTo(Master) < 2){
                    if (!target.getInventory().insertStack(Master.itemToRender)){
                        Master.ChangeState(0);
                    }
                    else{
                        Master.kill();
                    }
                    
                
                }
            }
        
        
        
    }

    @Override
    public void onCollision(HitResult hitResult) {
        // TODO Auto-generated method stub
       Master.SuperOnCollision(hitResult);
        
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {

        Block block = Master.world.getBlockState(blockHitResult.getBlockPos()).getBlock();

        if (block instanceof PlantBlock){
            Master.world.removeBlock(blockHitResult.getBlockPos(), false);
        }
        
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO Auto-generated method stub
       
        
        if (!(entityHitResult.getEntity() instanceof PlayerEntity) || !Master.Owner.isOwner((PlayerEntity)entityHitResult.getEntity())){

            Master.Attack(entityHitResult);
        }
    }

    @Override
    public void OnEnter(){
        target = Master.world.getPlayerByUuid(Master.Owner.ID);
        
        if (target == null){
            Master.ChangeState(0);
            return;
        }

        Vec3d Destination = target.getPos().add(new Vec3d(0,1.5f,0));

        Vec3d dir = Master.getPos().subtract(Destination).normalize();

        Master.originalRot = Util.getDirectionalRotation(new Vec3d(0,0,1), dir);

        originaldist = Destination.squaredDistanceTo(Master.getPos());

        if (!Master.world.isClient){
            Master.playReturnSound((ServerWorld) Master.world, Master.getBlockPos(), Master.Maxed);
        }
        
    }
   
        
       
    
    
}
