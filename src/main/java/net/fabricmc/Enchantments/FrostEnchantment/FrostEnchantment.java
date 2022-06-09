package net.fabricmc.Enchantments.FrostEnchantment;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Enchantments.IWorldBehvaior;
import net.fabricmc.GenericItemBlock.GenericItemBlock;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.NetworkHandlerServer;
import net.fabricmc.Util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FrostEnchantment extends Enchantment implements IWorldBehvaior{

    /**
     * Need to find out how to make one enchantment require another0
     */
    
    public FrostEnchantment(EnchantmentTarget t) {
     
            super(Enchantment.Rarity.COMMON, t , new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getMinLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return !(other instanceof IWorldBehvaior);
    }

    
    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity){
            ((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * level));
        }
    }

    @Override
    public void OnBlockThrownHit(World world, Entity source, BlockPos pos, int level, boolean max) {
        
        /**
         * Based on enchantment level, create a boxed area around the position, and for every block in that area
         * check the distance to the origin, and based of that and some % chance, turn the block into a snow block
         * call SpawnBlockContactParticles
         * Some effect cloud perhaps?
         */
        if (world.isClient || !max){
            return;
        }

         BlockPos boxMin = new BlockPos(-1,-1,-1);
         BlockPos boxMax = new BlockPos(1,1,1);

         /**
          * dependent on level, change the size of the box min/max
          */

          boxMin = boxMin.multiply(2 * level);
          boxMax = boxMax.multiply(2 * level);

          

          boxMax = boxMax.add(pos);
          boxMin = boxMin.add(pos);

          float RelativeDistance = boxMin.getManhattanDistance(pos);

          for(double x = boxMin.getX(); x <= boxMax.getX(); x++){
              for (double y = boxMin.getY(); y <= boxMax.getY(); y++){
                  for (double z = boxMin.getZ(); z <= boxMax.getZ(); z++){
                      /**
                       * WOW! A N^3 loop. So original!
                       */

                        BlockPos current = new BlockPos(x,y,z);

                       /**
                        * Check if the current block is a player placed block!
                        */
                        if (BNSCore.getPlayerBlock((ServerWorld) world, current.toString())){continue;}

                        BlockState blockstate = world.getBlockState(current);

                        //determine whether the block is valid for replacement!
                        if (!blockstate.getMaterial().isSolid() && !blockstate.getMaterial().isLiquid()){continue;}

                        if (blockstate.getBlock() instanceof GenericItemBlock){continue;}

                        float currentDistToOrig = current.getManhattanDistance(pos);

                        float chanceToTurn =  (currentDistToOrig/RelativeDistance)- Util.getRandomFloat(0, 0.2f);

                        if (chanceToTurn < 0.4){
                            world.setBlockState(current, Blocks.PACKED_ICE.getDefaultState());
                        }

                       
                  }
              }
          }

        
    }

    @Override
    public void OnEntityThrownHit(World world, Entity source, EntityHitResult result, int level, boolean max) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void SpawnTrailingParticles(World world, Vec3d pos, Vec3d dir, int level, boolean max) {
       
        int p_amount = 5;
        float step = 0.25f;
        Vec3d direction = new Vec3d(dir.getX(), dir.getY(), dir.getZ());
        /**
         * either keep using getRotationVector, or find a direction vector using the quaternion and a forward vector
         */
        for (int i = 0; i < p_amount; i++){
            Vec3d spot = direction.multiply(i*step);
            spot = spot.add(pos);

            world.addParticle(ParticleRegistery.TRAILING_FROST_PARTICLE,
            spot.getX(), spot.getY(), spot.getZ(),
                          0, 0, 0);
        }
        
    }

    @Override
    public void SpawnBlockContactParticles(World world, Vec3d pos, int level, boolean max) {
       if (world.isClient){
           return;
       }
      
       NetworkHandlerServer.spawnFrostContact((ServerWorld) world, pos, max ? 2 : 4);
        
    }

    @Override
    public void SpawnPulsingParticles(BlockPos Pos, World world, int level) {
        // TODO Auto-generated method stub
        if (Util.randgen.nextFloat() > 0.2f){
            return;
      }
        Vec3d spot = new Vec3d(Pos.getX() + 0.5, Pos.getY() + 0.5, Pos.getZ() + 0.5); // adjustment here!
        
        int p_amount = 3;
        for (int i = 0; i < p_amount; i++){
            Vec3d dir = Util.getRandomDirectionUnitSphere();
            dir.normalize();
            dir = dir.multiply(Util.getRandomDouble(0.01, 0.05));
            
            world.addParticle(ParticleRegistery.FROST_PARTICLE,
            spot.getX(), spot.getY(), spot.getZ(),
                          dir.x, dir.y, dir.z);
        }
    }

    @Override
    public void OnTick(GenericThrownItemEntity ntity, World world) {
       
    }
}


