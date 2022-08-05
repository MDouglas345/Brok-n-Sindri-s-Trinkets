package net.fabricmc.StaticFireBlock;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class AdvStaticFireBlock extends StaticFireBlock{

    public AdvStaticFireBlock(Settings settings) {
        super(settings, 3.0f, 300);
        //TODO Auto-generated constructor stub
    }

    protected BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age) {
        BlockState blockState = getStateN(world, pos);
        return blockState.isOf(BNSCore.ADV_STATIC_FIRE_BLOCK) ? (BlockState)blockState.with(AGE, age) : blockState;
     }

    @Override
    protected BlockState getStateN(BlockView world, BlockPos pos) {
        // TODO Auto-generated method stub
        return BNSCore.ADV_STATIC_FIRE_BLOCK.getDefaultState();
    }

    
    
}
