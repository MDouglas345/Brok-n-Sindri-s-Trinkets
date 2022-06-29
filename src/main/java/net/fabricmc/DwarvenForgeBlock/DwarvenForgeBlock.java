package net.fabricmc.DwarvenForgeBlock;

import java.util.List;

import javax.annotation.Nullable;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.ConfigRegistery;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DwarvenForgeBlock extends BlockWithEntity{

    

    public DwarvenForgeBlock(Settings settings) {
        super(settings);
        //TODO Auto-generated constructor stub
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        // TODO Auto-generated method stub
        DwarvenForgeBlockEntity ent = new DwarvenForgeBlockEntity(pos, state);
        
        return ent;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (world.isClient){return;}
        DwarvenForgeBlockEntity be = (DwarvenForgeBlockEntity) world.getBlockEntity(pos);
        BNSCore.removeDWFromStack((ServerWorld) world, "forge", be.StackID);

        
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient){
            int distancebetween = ConfigRegistery.configuration.getInt("DFDistance");
            List<BlockPos> list = BNSCore.getDwarvenForgeStack((ServerWorld) world).getList();
            for (BlockPos n : list){
                if (n.isWithinDistance(pos, distancebetween)){
                    world.breakBlock(pos, true);
                    return;
                }
            }

            DwarvenForgeBlockEntity ent =  (DwarvenForgeBlockEntity) world.getBlockEntity(pos);
            ent.StackID = BNSCore.pushDFOntoStack((ServerWorld) world, "forge", pos);
        }
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BNSCore.DWARVEN_FORGE_BLOCK_ENTITY, (world1, pos, state1, be) -> DwarvenForgeBlockEntity.tick(world1, pos, state1, be));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
    }

    
}
