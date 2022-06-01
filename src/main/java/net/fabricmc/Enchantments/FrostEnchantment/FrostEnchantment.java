package net.fabricmc.Enchantments.FrostEnchantment;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Enchantments.IWorldBehvaior;
import net.fabricmc.Util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
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
        return 1;
    }

    @Override
    public int getMinLevel() {
        // TODO Auto-generated method stub
        return 1;
    }

    
    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        
    }

    @Override
    public void OnBlockThrownHit(World world, BlockPos pos, int level) {
        
        /**
         * Based on enchantment level, create a boxed area around the position, and for every block in that area
         * check the distance to the origin, and based of that and some % chance, turn the block into a snow block
         * call SpawnBlockContactParticles
         * Some effect cloud perhaps?
         */
        if (world.isClient){
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
                        if (!blockstate.getMaterial().isSolid()){continue;}

                        float currentDistToOrig = current.getManhattanDistance(pos);

                        float chanceToTurn =  (currentDistToOrig/RelativeDistance)- Util.getRandomFloat(0, 0.2f);

                        if (chanceToTurn < 0.4){
                            world.setBlockState(current, Blocks.SNOW_BLOCK.getDefaultState());
                        }

                       
                  }
              }
          }

        
    }

    @Override
    public void OnEntityThrownHit(World world, EntityHitResult result, int level) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void SpawnTrailingParticles(World world, Vec3d pos, int level) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void SpawnBlockContactParticles(World world, Vec3d pos, int level) {
        // TODO Auto-generated method stub
        
    }
}


