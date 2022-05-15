/**
 *
 * There is a bug that if a weapon is thrown in the same area as another, then it will override the previous. The solution is to have
 * a list that store all weapons that are in the same spot, maybe an inventory popup to show which item to select.
 */


package net.fabricmc.GenericItemBlock;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.BlockPosStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
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

            player.getInventory().insertStack(entity.SavedItem);
            
            world.removeBlock(pos, false);

            BlockPosStackComponent stack = mycomponents.BlockEntityPositions.get(world.getLevelProperties());

            stack.Remove(entity.Owner.name, entity.IndexIntoStack);
        }
 
        return ActionResult.SUCCESS;
    }

    

    
    
}
