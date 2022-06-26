package net.fabricmc.Entity.PassiveDwarf.Goals;

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
        return owner.WeaponToEnchant != null && owner.RunicStone != null;
    }

    public boolean shouldContinue() {
        return !this.owner.getNavigation().isIdle();
     }

    @Override
    public void start(){
        BlockPos t = owner.lastKnownForgeLocation;
        owner.getNavigation().startMovingTo(t.getX(), t.getY(), t.getZ(), speed);
    }


    
    
}
