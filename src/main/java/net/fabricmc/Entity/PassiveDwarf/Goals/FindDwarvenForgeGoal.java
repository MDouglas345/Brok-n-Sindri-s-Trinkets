package net.fabricmc.Entity.PassiveDwarf.Goals;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.GlobalPosRecordComponent;
import net.fabricmc.DwarvenForgeBlock.DwarvenForgeBlockEntity;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;

import net.minecraft.world.poi.PointOfInterestStorage.OccupationStatus;
import net.minecraft.world.poi.PointOfInterest;

public class FindDwarvenForgeGoal extends Goal {
    private PassiveDwarf owner;

    int radius = 9;

    public FindDwarvenForgeGoal(PassiveDwarf entity){
        owner = entity;
    }

    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        return !owner.foundForge || owner.lastKnownForgeLocation == null;
    }


    @Override 
    public void tick(){
        BlockPos nearest = getNearbyDwarvenForge();

        if (nearest == null){
            /**
             * oh fuck!
             * what should happen here?....
             * Make the dwarf drop the good stuff, then desapwn.
             */
            owner.despawn();
            return;
        }

        owner.foundForge = true;
        owner.lastKnownForgeLocation = nearest;


    }

    @Nullable
    BlockPos getNearbyDwarvenForge(){

        BlockPos origin = owner.getBlockPos();
        /**
         * love me a n^3 loop.
         * instead! keep track of placed dwarven forge.
         */
        
        GlobalPosRecordComponent stack  = BNSCore.getDwarvenForgeStack((ServerWorld) owner.world);

        List<BlockPos> list = stack.getList();

        BlockPos closest = null ;
        double minDist = Double.MAX_VALUE;
        boolean shouldDespawn = true;
        for (BlockPos pos : list){

            BlockEntity be = owner.world.getBlockEntity(pos);
            if (be == null || !(be instanceof DwarvenForgeBlockEntity)){continue;}
            double d = pos.getSquaredDistance(origin);
            if (d  < 50*50){
                shouldDespawn = false;
            }
            if ( d < minDist){
                closest = pos;
                minDist = d;
            }
        }

        if (shouldDespawn){
            owner.despawn();
        }
        
        return closest;
       
    }

    


    
}
