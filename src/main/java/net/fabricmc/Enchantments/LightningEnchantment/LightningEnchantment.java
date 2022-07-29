package net.fabricmc.Enchantments.LightningEnchantment;

import java.util.List;

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
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LightningEnchantment extends Enchantment implements IWorldBehvaior{

    /**
     * Need to find out how to make one enchantment require another0
     */
    
    public LightningEnchantment(EnchantmentTarget t) {
     
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
        if (user.world.isClient){return;}

        ServerWorld world = (ServerWorld) user.world;
        float chance = Util.randgen.nextFloat();

        if (chance < 0.1 * level){
            
           AffectNearbyEntities(world, user, target.getBlockPos(), level);
        }
    }

    @Override
    public void OnBlockThrownHit(World world, Entity source,  BlockPos pos, int level, boolean max) {
        if (world.isClient || !max){return;}
        float bonus = world.isThundering() || world.isRaining() ? 0.2f : 0.0f;
        if ((Util.randgen.nextFloat() - level * 0.08 - bonus) > 0.1){return;}  
        Util.createLightningStrike(pos, (ServerWorld) world, source, 2 * level);

        
    }

    @Override
    public void OnEntityThrownHit(World world, Entity source, EntityHitResult result, int level, boolean max) {
        // TODO Auto-generated method stub
        if (world.isClient || !max){return;}
        float bonus = world.isThundering() || world.isRaining() ? 0.2f : 0.0f;
        if ((Util.randgen.nextFloat() - level * 0.08 - bonus) > 0.1){return;}  
        Util.createLightningStrike(result.getEntity().getBlockPos(), (ServerWorld) world, source, 2 * level);
        
    }

    @Override
    public void SpawnTrailingParticles(World world, Vec3d pos, Vec3d dir, int level, boolean max) {
       
        int p_amount = 3;
        float step = 0.18f;
        Vec3d direction = new Vec3d(dir.getX(), dir.getY(), dir.getZ());


        /**
         * either keep using getRotationVector, or find a direction vector using the quaternion and a forward vector
         */
        for (int i = 0; i < p_amount; i++){
            Vec3d spot = direction.multiply(i*step);
            spot = spot.add(pos);

            world.addParticle(ParticleRegistery.LIGHTNING_PARTICLE,
            spot.getX(), spot.getY(), spot.getZ(),
                          0, 0, 0);
        }

       
        
    }

    @Override
    public void SpawnBlockContactParticles(World world, Vec3d pos, int level, boolean max) {
       
        
    }

    @Override
    public void SpawnPulsingParticles(BlockPos Pos, World world, int level) {
        if (Util.getRandomFloat(0, 1) >  0.15){
            return;
        }

        if (world.isThundering() && Util.randgen.nextFloat() < 0.001 && !world.isClient){
            Util.createVisualLightningStrike(Pos, (ServerWorld) world);
        }

        if (!world.isClient){
            return;
        }

        Vec3d spot = new Vec3d(Pos.getX() + 0.5, Pos.getY() + 0.5, Pos.getZ() + 0.5); // adjustment here!
        
    
         int p_amount = 2 * level;
         for (int i = 0; i < p_amount; i++){
             Vec3d dir = Util.getRandomDirectionUnitSphere();
             dir = dir.normalize();
             dir = dir.multiply(Util.randgen.nextFloat());
             dir = spot.add(dir);
             
             world.addParticle(ParticleRegistery.LIGHTNING_PARTICLE,
             dir.getX(), dir.getY(), dir.getZ(),
                           0, 0, 0);
         }
    }

    @Override
    public void OnTick(GenericThrownItemEntity entity, World world) {
        // TODO Auto-generated method stub
        float bonus = world.isThundering()  || world.isRaining()? 0.08f : 0;
        /**
         * 
         * Get nearby blocks / entities in a radius around the entity
         * random chance for a bolt to hit from entity to targee.(Need to fiigure out appraoch for the lightning bolt bit)
         */

        if ((Util.randgen.nextFloat() + bonus) > 0.01 && !world.isClient){
            Vec3d pos = entity.getPos();
            
            BlockPos currentblock = new BlockPos(Util.getRandomDouble(-6, 6), Util.getRandomDouble(-1, 4), Util.getRandomDouble(-6, 6));
            currentblock = currentblock.add(entity.getBlockPos());

            if (!world.getBlockState(currentblock).isAir()){
                NetworkHandlerServer.spawnBranchLightning((ServerWorld) world, pos, new Vec3d(currentblock.getX(), currentblock.getY(), currentblock.getZ()));
                world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 0.1f, 1f);
            }

            /* 
            for  (int x = -3; x < 3; x++){
                for (int y = -3; y < 3; y++){
                    for (int z = -3; z < 3; z++ ){
                        BlockPos currentblock = new BlockPos(x,y,z);
                        currentblock = currentblock.add(new BlockPos(pos));

                        if (!world.getBlockState(currentblock).isAir() && Util.randgen.nextFloat() < 0.005){
                            NetworkHandler.spawnBranchLightning((ServerWorld) world, pos, new Vec3d(currentblock.getX(), currentblock.getY(), currentblock.getZ()));
                            return;
                        }
                    }
                }
            }
            */
          

            
        }
    }

    @Override
    public void AffectNearbyEntities(ServerWorld world, Entity source, BlockPos pos, int level) {
        // TODO Auto-generated method stub
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, source.getBoundingBox().expand(5 * level), (entity)->{
            return !source.getUuid().equals(entity.getUuid());
        });
        Vec3d Pos = Vec3d.of(pos.up());
        list.forEach((living) -> {
            BlockPos currentblock = living.getBlockPos();
            NetworkHandlerServer.spawnBranchLightning((ServerWorld) world, Pos, new Vec3d(currentblock.getX(), currentblock.getY(), currentblock.getZ()));   
            living.damage(DamageSource.LIGHTNING_BOLT, 1); 
            world.playSound(null, living.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 0.8f, 1f);
        });
        
    }
}


