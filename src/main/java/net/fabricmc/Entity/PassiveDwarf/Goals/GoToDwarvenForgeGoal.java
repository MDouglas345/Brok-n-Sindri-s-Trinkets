package net.fabricmc.Entity.PassiveDwarf.Goals;

import net.fabricmc.DwarvenForgeBlock.DwarvenForgeBlockEntity;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;


public class GoToDwarvenForgeGoal extends Goal {
    PassiveDwarf owner;
    float speed;

    public GoToDwarvenForgeGoal(PassiveDwarf e, float s){
        owner = e;
        speed = s;
    }
    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        if (!owner.foundForge){return false;}
        if (owner.lastKnownForgeLocation.isWithinDistance(owner.getBlockPos(), 1.5)){return false;}

       
        return owner.inventoryContainsRune() && owner.inventoryContainsWeapon();
    }

    public boolean shouldContinue() {
        if (owner.lastKnownForgeLocation.isWithinDistance(owner.getBlockPos(), 1.5)){return false;}
        return !this.owner.getNavigation().isIdle();
     }

    @Override
    public void start(){
        BlockPos t = owner.lastKnownForgeLocation;
        owner.getNavigation().startMovingTo(t.getX(), t.getY(), t.getZ(), speed);
    }

    @Override
    public void stop(){
        DwarvenForgeBlockEntity be = (DwarvenForgeBlockEntity) owner.getWorld().getBlockEntity(owner.lastKnownForgeLocation);

        if (be == null){
            owner.despawn();
        }
    }


    
    
}
