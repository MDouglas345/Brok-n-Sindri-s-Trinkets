package net.fabricmc.DwarvenForgeBlock;

import org.apache.logging.log4j.core.jmx.Server;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.Brok.BrokEntity;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.minecraft.block.BlockState;
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


    @Override
    public void writeNbt(NbtCompound nbt){
        super.writeNbt(nbt);

        nbt.putInt("stackID", StackID);
    }

    @Override
    public void readNbt(NbtCompound nbt){
        StackID = nbt.getInt("stackID");
    }

    public static void tick(World world, BlockPos pos, BlockState state, DwarvenForgeBlockEntity be) {
        if (world.isClient){return;}

            if (world.random.nextFloat() < 0.001){
                if (!be.spawnedBrok){
                    BrokEntity brok = new BrokEntity(world);
                    brok.setPosition(Vec3d.of(pos.up()));
                    be.spawnedBrok = true;
                    ((ServerWorld)world).spawnEntity(brok);
                }

                if (!be.spawnedBrok){

                }
            }
        
       
    }
    
}
