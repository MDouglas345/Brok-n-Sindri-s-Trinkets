/**
 *
 * There is a bug that if a weapon is thrown in the same area as another, then it will override the previous. The solution is to have
 * a list that store all weapons that are in the same spot, maybe an inventory popup to show which item to select.
 */


package net.fabricmc.GenericItemBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GenericItemBlock extends BlockWithEntity{

    public GenericItemBlock(Settings settings) {
        super(settings);
        
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos var1, BlockState var2) {
        
        return new GenericItemBlockEntity(var1, var2);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        GenericItemBlockEntity entity = (GenericItemBlockEntity) world.getBlockEntity(pos);

        if (!world.isClient) {

            
            
            player.getInventory().insertStack(entity.SavedItem);
            
            world.removeBlock(pos, false);
        }
 
        return ActionResult.SUCCESS;
    }

    
    
}
