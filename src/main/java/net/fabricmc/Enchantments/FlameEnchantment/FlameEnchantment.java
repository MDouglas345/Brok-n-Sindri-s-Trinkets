package net.fabricmc.Enchantments.FlameEnchantment;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Enchantments.IWorldBehvaior;
import net.fabricmc.GenericItemBlock.GenericItemBlock;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.Util;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlameEnchantment extends Enchantment implements IWorldBehvaior {

    protected FlameEnchantment(EnchantmentTarget type) {
        super(Rarity.COMMON, type, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        //TODO Auto-generated constructor stub
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
                        //if (!blockstate.getMaterial().isSolid() && !blockstate.getMaterial().isLiquid()){continue;}

                        if (blockstate.getBlock() instanceof GenericItemBlock){continue;}

                        float currentDistToOrig = current.getManhattanDistance(pos);

                        float chanceToTurn =  (1 - (currentDistToOrig/RelativeDistance)) + Util.getRandomFloat(0, 0.2f);

                        if (chanceToTurn > 0.8 && AbstractFireBlock.canPlaceAt(world, current, Direction.UP)){
                            BlockState blockState2 = AbstractFireBlock.getState(world, current);
                        
                            ((ServerWorld)world).setBlockState(current, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                        }

                       
                  }
              }
          }
        
    }

    @Override
    public void OnEntityThrownHit(World world, Entity source, EntityHitResult result, int level, boolean max) {
        // TODO Auto-generated method stub
        if (world.isClient){return;}
        Entity e = result.getEntity();

        if (e instanceof LivingEntity){
            ((LivingEntity)e).setOnFireFor(2*level);
        }
        
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return !(other instanceof IWorldBehvaior);
    }

    @Override
    public void SpawnTrailingParticles(World world, Vec3d pos, Vec3d dir, int level, boolean max) {
        int p_amount = 3;
        float step = 0.25f;
        Vec3d direction = new Vec3d(dir.getX(), dir.getY(), dir.getZ());
        /**
         * either keep using getRotationVector, or find a direction vector using the quaternion and a forward vector
         */
        for (int i = 0; i < p_amount; i++){
            Vec3d spot = direction.multiply(i*step);
            spot = spot.add(pos);

            world.addParticle(ParticleRegistery.TRAILING_FLAME_PARTICLE,
            spot.getX(), spot.getY(), spot.getZ(),
                          0, 0, 0);
        }
        
    }

    @Override
    public void SpawnBlockContactParticles(World world, Vec3d pos, int level, boolean max) {
        if (world.isClient){
            return;
        }
         for (int i = 0; i < 15; i++){
             Vec3d dir = Util.getRandomDirectionUnitSphere().multiply(5);
            
             Vec3d newpos = pos.add(dir);
             dir = dir.normalize();
             dir = dir.multiply(1.8f);


             /**
              * ajust speed here?
              */
            
              ((ServerWorld)world).spawnParticles(ParticleRegistery.CONTACT_FLAME_PARTICLE, 
                                                      pos.x, pos.y, pos.z, 4,
                                                             0, 0, 0, 3f);
        }

        
    }

    @Override
    public void SpawnPulsingParticles(BlockPos Pos, World world, int level) {
        // TODO Auto-generated method stub
        if (Util.randgen.nextFloat() > 0.2f){
            return;
        }

         // TODO Auto-generated method stub
         Vec3d spot = new Vec3d(Pos.getX() + 0.5, Pos.getY() + 0.5, Pos.getZ() + 0.5); // adjustment here!
        
         int p_amount = 3;
         for (int i = 0; i < p_amount; i++){
            Vec3d dir = Util.getRandomDirectionUnitSphere();
            dir = dir.normalize();
            dir = dir.multiply(Util.randgen.nextFloat() * 0.5);
            dir = spot.add(dir);
             
             world.addParticle(ParticleRegistery.FLAME_PARTICLE,
             dir.getX(), dir.getY(), dir.getZ(),
                           0, 0, 0);
         }
        
    }

    @Override
    public void OnTick(GenericThrownItemEntity ntity, World world) {
        // TODO Auto-generated method stub
        
    }
    
}
