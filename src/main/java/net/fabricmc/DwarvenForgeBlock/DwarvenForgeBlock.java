package net.fabricmc.DwarvenForgeBlock;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.ConfigRegistery;
import net.fabricmc.Entity.Brok.BrokEntity;
import net.fabricmc.Entity.Sindri.SindriEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DwarvenForgeBlock extends BlockWithEntity{

    private static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape VOXEL_SHAPE_N = Block.createCuboidShape(0.6499999999999995, 0.07500000000000018, 3.75, 15.350000000000001, 5.025, 12.25);

    private static final VoxelShape VOXEL_SHAPE_W = Block.createCuboidShape(3.75, 0.07500000000000018, 0.6499999999999986, 12.25, 5.025, 15.350000000000001);

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

        Entity brok = world.getEntityById(be.brokUUID);
        Entity sindri = world.getEntityById(be.sindriUUID);

        if (brok != null && brok instanceof BrokEntity){
            ((BrokEntity)brok).despawn();
        }

        if (sindri != null && sindri instanceof SindriEntity){
            ((SindriEntity)sindri).despawn();
        }



        
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

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state,  BlockRotation rotation){
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror){
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder){
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)){
            case NORTH  : return VOXEL_SHAPE_N;
            case SOUTH  : return VOXEL_SHAPE_N;
            case WEST   : return VOXEL_SHAPE_W;
            case EAST   : return VOXEL_SHAPE_W;
            default     : return VOXEL_SHAPE_N;

        }
     }

    
}
