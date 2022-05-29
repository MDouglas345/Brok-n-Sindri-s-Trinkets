/**
 *
 * There is a bug that if a weapon is thrown in the same area as another, then it will override the previous. The solution is to have
 * a list that store all weapons that are in the same spot, maybe an inventory popup to show which item to select.
 */


package net.fabricmc.GenericItemBlock;

import io.netty.channel.VoidChannelPromise;
import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GenericItemBlock extends BlockWithEntity implements Waterloggable{

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public GenericItemBlock(Settings settings) {
        super(settings);

        setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false));
        
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState()
            .with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite())
            .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            // This is for 1.17 and below: world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
 
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING, WATERLOGGED);
    }
    

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BNSCore.GENERIC_ITEM_BLOCK_ENTITY, (world1, pos, state1, be) -> GenericItemBlockEntity.tick(world1, pos, state1, be));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos var1, BlockState var2) {
        
        return new GenericItemBlockEntity(var1, var2);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        GenericItemBlockEntity entity = (GenericItemBlockEntity) world.getBlockEntity(pos);

        if (!world.isClient) {

            /**
             * 
             * Need to do a check if the player that tries to pick up item and is not the owner and item has the worthy
             * enchantment, they should not be able to pick it up.
             * 
             * If the player that tries to pick up item and is not the owner and the item does not have the worthy enchantment, they should
             * be able to pick it up anyway
             * 
             * if the player that tries to pick up the item is the owner, they should be able to pick it up anyway
             * 
             * 
             * The use of UUIDs means that pirated versions of the game may encournter issues. will need more testing on pirated verisons
             * real versions will not have this issue? Need to look further into it.
             */

            //replace player.getUUID with client identification
            if (!entity.Owner.isOwner(player)&& ( EnchantmentHelper.getLevel(BNSCore.WorthyTool, entity.SavedItem) == 2 || EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, entity.SavedItem) == 2 ) ){
                // the player that is not the owner and tries to pick up the item, and if the itemstack has either worthytool or worthyweapon enchantment, they should
                // not be able to pick it up
                player.sendMessage(new LiteralText("You are not Worthy!"), true);
                return ActionResult.FAIL;
            }

            if (!player.getInventory().insertStack(entity.SavedItem)){
                return ActionResult.FAIL;
            }
            
            world.removeBlock(pos, false);

            /// replace with BNSCore.removeBE~~~
            BNSCore.removeBEFromStack((ServerWorld) world, entity.Owner.name, entity.IndexIntoStack);
    
        }
 
        return ActionResult.SUCCESS;
    }

    
    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BNSCore.LOGGER.info("Hit by projectile");
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        EntityShapeContext entityShapeContext = (EntityShapeContext)context;

        if (context instanceof EntityShapeContext && entityShapeContext.getEntity() != null && entityShapeContext.getEntity() instanceof GenericThrownItemEntity){
            return super.getCollisionShape(state, world, pos, context);
        }   
        
        
        return VoxelShapes.empty();
        /*EntityShapeContext entityShapeContext;
        Entity entity;
        if (context instanceof EntityShapeContext && (entity = (entityShapeContext = (EntityShapeContext)context).getEntity()) != null) {
            if (entity.fallDistance > 2.5f) {
                return FALLING_SHAPE;
            }
            boolean bl = entity instanceof FallingBlockEntity;
            if (bl || PowderSnowBlock.canWalkOnPowderSnow(entity) && context.isAbove(VoxelShapes.fullCube(), pos, false) && !context.isDescending()) {
                return super.getCollisionShape(state, world, pos, context);
            }
        }
        return VoxelShapes.empty();
        */
    }
    
    
}
