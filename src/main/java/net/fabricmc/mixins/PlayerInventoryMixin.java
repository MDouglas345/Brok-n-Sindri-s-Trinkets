package net.fabricmc.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    
    @Shadow private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow public PlayerEntity player;

    public List<ItemStack> SavedItems = new ArrayList<>();


    @Inject(at = @At("HEAD"), method = "dropAll()V")
    public void dropAllHead(CallbackInfo info) {
        
        PlayerInventory pi = (PlayerInventory)(Object)this;

        if (pi.player.world.isClient){
            return;
        }

        Box box = new Box(pi.player.getBlockPos().add(new BlockPos(-128,-128,-128)), pi.player.getBlockPos().add(new BlockPos(128,128,128)) );
        List<PlayerEntity> players = Util.getNearbyPlayers(pi.player.world, box);
        
        boolean res = true;
        int dist = 128;
        for (PlayerEntity player : players){

            if (player.isDead()){continue;}
            
            double d = player.squaredDistanceTo(pi.player);
            if (d < (double)(dist * dist)) {
                res = false;
            }
        }

        if (res){
            // dont bother spawning entities, just try to place the blocks!
            BlockPos Origin = pi.player.getBlockPos();
            int radius = 24;
            for (DefaultedList<ItemStack> list : combinedInventory){

                for (int i = 0; i < list.size(); ++i) {
                    ItemStack stack = (ItemStack)list.get(i);

                    if (EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, stack) ==0 && EnchantmentHelper.getLevel(BNSCore.WorthyTool, stack) ==0){
                        continue;
                    }
                    /**
                     * Establish a box of potential spawnning locations, choose randomly. 
                     * While the selection is invlaid, chose another selection
                     * Once valid selection has been establish, spawn block entity
                     */
                    BlockPos hitpos = new BlockPos(Origin.add(new Vec3i(Util.getRandomDouble(-radius, radius), Util.getRandomDouble(-radius, radius), Util.getRandomDouble(-radius, radius))));

                    BlockState state = pi.player.world.getBlockState(hitpos);
                    int iterations = 15;
                    int cur = 0;
                    while (!state.isAir()){
                        hitpos = new BlockPos(Origin.add(new Vec3i(Util.getRandomDouble(-radius, radius), Util.getRandomDouble(-radius, radius), Util.getRandomDouble(-radius, radius))));
                        hitpos = pi.player.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, hitpos);
                        
                        state = pi.player.world.getBlockState(hitpos);

                        // might need something here incase this hangs!
                        if (cur >= iterations){
                            hitpos =  hitpos = new BlockPos(Util.randgen.nextInt(),Util.randgen.nextInt(),Util.randgen.nextInt()); // evvn this isnt safe!
                            state = pi.player.world.getBlockState(hitpos);
                        }
                    }

                    pi.player.world.setBlockState(hitpos,BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());
                    GenericItemBlockEntity be = (GenericItemBlockEntity)pi.player.world.getBlockEntity(hitpos);

                    int id = BNSCore.pushBEOntoStack((ServerWorld) pi.player.world, pi.player.getEntityName(), hitpos);

                    be.Initalize(stack, new Quaternion(0,0,0,1), (float)Util.getRandomDouble(100, 200),  id, new ClientIdentification(pi.player.getEntityName(), pi.player.getUuid()));

                    list.set(i, ItemStack.EMPTY);
                }

            }

        }
        else{
            for (DefaultedList<ItemStack> list : combinedInventory){

                for (int i = 0; i < list.size(); ++i) {
                    ItemStack stack = (ItemStack)list.get(i);
                
                    if (EnchantmentHelper.getLevel(BNSCore.WorthyWeapon, stack) >= 1 || EnchantmentHelper.getLevel(BNSCore.WorthyTool, stack) >= 1){
                        //SavedItems.add(stack.copy());
                        pi.player.world.spawnEntity(GenericThrownItemEntity.CreateNew((ServerWorld) pi.player.world, pi.player, stack, 1, true));
                        BNSCore.LOGGER.info(stack.toString());
                        list.set(i, ItemStack.EMPTY);
                    }
                }

            } 
        }
    }
}



