package net.fabricmc.StaticFireBlock;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class BaseStaticFireBlock extends StaticFireBlock {

    public BaseStaticFireBlock(Settings settings) {
        super(settings, 1.0f, 100);
        //TODO Auto-generated constructor stub
    }

    @Override
    protected BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age) {
        BlockState blockState = this.getStateN(world, pos);
        return blockState.isOf(BNSCore.BASE_STATIC_FIRE_BLOCK) ? (BlockState)blockState.with(AGE, age) : blockState;
     }

    @Override
    protected BlockState getStateN(BlockView world, BlockPos pos) {
        // TODO Auto-generated method stub
        return BNSCore.BASE_STATIC_FIRE_BLOCK.getDefaultState();
    }

   
    
}
