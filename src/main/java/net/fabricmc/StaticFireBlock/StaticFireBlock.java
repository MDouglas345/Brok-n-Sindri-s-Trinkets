package net.fabricmc.StaticFireBlock;


import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.Config.ConfigRegistery;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class StaticFireBlock extends AbstractFireBlock{
   int Age = 0;
   int TicksToLive;
   public static final IntProperty AGE;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty GREEN;
   private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES;
   private static final VoxelShape UP_SHAPE;
   private static final VoxelShape WEST_SHAPE;
   private static final VoxelShape EAST_SHAPE;
   private static final VoxelShape NORTH_SHAPE;
   private static final VoxelShape SOUTH_SHAPE;
   private final Map<BlockState, VoxelShape> shapesByState;

   static {
      AGE = Properties.AGE_15;
      NORTH = ConnectingBlock.NORTH;
      EAST = ConnectingBlock.EAST;
      SOUTH = ConnectingBlock.SOUTH;
      WEST = ConnectingBlock.WEST;
      UP = ConnectingBlock.UP;
      GREEN = BooleanProperty.of("green");
      DIRECTION_PROPERTIES = (Map)ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter((entry) -> {
         return entry.getKey() != Direction.DOWN;
      }).collect(Util.toMap());
      UP_SHAPE = Block.createCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      WEST_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
      EAST_SHAPE = Block.createCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      NORTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
      SOUTH_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
   }

   
   public StaticFireBlock(Settings settings, float damage, int tickstoburn) {
      super(settings, damage);
      //TODO Auto-generated constructor stub
      this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(GREEN, false));
      this.shapesByState = ImmutableMap.copyOf((Map)this.stateManager.getStates().stream().filter((state) -> {
         return (Integer)state.get(AGE) == 0;
      }).collect(Collectors.toMap(Function.identity(), StaticFireBlock::getShapeForState)));

      TicksToLive = tickstoburn;
   }



   protected void appendProperties(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE, NORTH, EAST, SOUTH, WEST, UP, GREEN});
   }

   @Override
   protected boolean isFlammable(BlockState state) {
      return !state.isAir();
   }

   public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      super.onBlockAdded(state, world, pos, oldState, notify);
      world.createAndScheduleBlockTick(pos, this, getFireTickDelay(world.random));
      StaticFireBlock block = (StaticFireBlock) state.getBlock();
      block.Age = 0;

      /**
       * set boolean properties based on what blocks are around? for more natural looking fire?
       */

      
   }

   public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      world.createAndScheduleBlockTick(pos, this, getFireTickDelay(world.random));

      Age++;

      if (Age > TicksToLive){
         world.removeBlock(pos, false);
      }
   }

   public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
      return this.canPlaceAt(state, world, pos) ? this.getStateWithAge(world, pos, (Integer)state.get(AGE)) : Blocks.AIR.getDefaultState();
   }

   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapesByState.get(state.with(AGE, 0));
   }

   @Override
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return this.getStateForPosition(ctx.getWorld(), ctx.getBlockPos());
   }
   
   private static int getFireTickDelay(Random random) {
      return 30 + random.nextInt(10);
   }

   private static VoxelShape getShapeForState(BlockState state) {
      VoxelShape voxelShape = VoxelShapes.empty();
      if ((Boolean)state.get(UP)) {
         voxelShape = UP_SHAPE;
      }

      if ((Boolean)state.get(NORTH)) {
         voxelShape = VoxelShapes.union(voxelShape, NORTH_SHAPE);
      }

      if ((Boolean)state.get(SOUTH)) {
         voxelShape = VoxelShapes.union(voxelShape, SOUTH_SHAPE);
      }

      if ((Boolean)state.get(EAST)) {
         voxelShape = VoxelShapes.union(voxelShape, EAST_SHAPE);
      }

      if ((Boolean)state.get(WEST)) {
         voxelShape = VoxelShapes.union(voxelShape, WEST_SHAPE);
      }

      return voxelShape.isEmpty() ? BASE_SHAPE : voxelShape;
   }

   public BlockState getStateForPosition(BlockView world, BlockPos pos) {
      BlockPos blockPos = pos.down();
      BlockState blockState = world.getBlockState(blockPos);
      if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
         BlockState blockState2 = this.getDefaultState();
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            BooleanProperty booleanProperty = (BooleanProperty)DIRECTION_PROPERTIES.get(direction);
            if (booleanProperty != null) {
               blockState2 = (BlockState)blockState2.with(booleanProperty, this.isFlammable(world.getBlockState(pos.offset(direction))));
            }
         }

         return blockState2;
      } else {
         return this.getDefaultState();
      }
   }

   protected BlockState getStateWithAge(WorldAccess world, BlockPos pos, int age) {
      BlockState blockState = getState(world, pos);
      return blockState.isOf(Blocks.FIRE) ? (BlockState)blockState.with(AGE, age) : blockState;
   }

   public void registerFireColor(){
      this.setDefaultState(this.stateManager.getDefaultState().with(GREEN, ConfigRegistery.configuration.getBoolean("GreenFire")));
   }

   protected abstract BlockState getStateN(BlockView world, BlockPos pos);


}