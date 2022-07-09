package net.fabricmc.DwarvenForgeBlock;

import org.apache.logging.log4j.core.jmx.Server;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.Brok.BrokEntity;
import net.fabricmc.Entity.Sindri.SindriEntity;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DwarvenForgeBlockEntity extends BlockEntity{

    public int StackID;

    public boolean spawnedBrok = false;
    public boolean spawnedSindri = false;

    public DwarvenForgeBlockEntity(BlockPos pos, BlockState state) {
        super(BNSCore.DWARVEN_FORGE_BLOCK_ENTITY, pos, state);
        //TODO Auto-generated constructor stub
       
    }

    public boolean nearbyIncreasingBlocks(){
        /**
         * Check for atleast 3 nearby gold blocks
         * thy must exist on the same plane, no height variance
         */
        int found = 0;

        BlockPos origin = getPos();

         for (int x = -3; x < 3; x++){
            for (int z = -3; z < 3; z++){
                
                BlockPos pos = origin.add(x, 0, z);

                BlockState state = world.getBlockState(pos);

                if (state.isOf(Blocks.GOLD_BLOCK)){
                    found++;
                }

                if (found == 3){
                    return true;
                }
            }
         }

         return false;
    }


    @Override
    public void writeNbt(NbtCompound nbt){
        super.writeNbt(nbt);

        nbt.putInt("stackID", StackID);
        nbt.putBoolean("spawnbrok", this.spawnedBrok);
        nbt.putBoolean("spawnsindri", this.spawnedSindri);
    }   

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        StackID = nbt.getInt("stackID");
        spawnedBrok = nbt.getBoolean("spawnbrok");
        spawnedSindri = nbt.getBoolean("spawnsindri");
    }

    public static void tick(World world, BlockPos pos, BlockState state, DwarvenForgeBlockEntity be) {
        if (world.isClient){return;}

            if (world.random.nextFloat() < 0.001){
                if (!be.spawnedBrok){
                    BrokEntity brok = new BrokEntity(world);
                    brok.setPosition(Vec3d.of(pos.up()));
                    be.spawnedBrok = true;
                    ((ServerWorld)world).spawnEntity(brok);
                    be.markDirty();
                    
                }

                if (!be.spawnedSindri && be.nearbyIncreasingBlocks()){
                    SindriEntity sindri = new SindriEntity(world);
                    sindri.setPosition(Vec3d.of(pos.up()));
                    be.spawnedSindri = true;
                    ((ServerWorld)world).spawnEntity(sindri);
                    be.markDirty();
                }
            }
        
       
    }
    
}
