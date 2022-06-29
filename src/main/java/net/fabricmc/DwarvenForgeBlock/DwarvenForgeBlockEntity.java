package net.fabricmc.DwarvenForgeBlock;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class DwarvenForgeBlockEntity extends BlockEntity{

    int StackID;

    boolean spawnedBrok = false;
    boolean spawnedSindri = false;

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
    
}
