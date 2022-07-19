package net.fabricmc.Entity.PassiveDwarf.Goals;

import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class EscapeWaterGoal extends Goal{

    PassiveDwarf owner;
    BlockPos t;
    float speed;

    public EscapeWaterGoal(PassiveDwarf d, float s){
        owner = d;
        owner.getNavigation().setCanSwim(true);
        speed = s;
    }

    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        return this.owner.isTouchingWater() && this.owner.getFluidHeight(FluidTags.WATER) > this.owner.getSwimHeight() || this.owner.isInLava();
    }

    @Override
    public void start(){
        t = owner.lastKnownForgeLocation;

        if (t == null){
            /**
             * Well. Despawn.
             */
            owner.despawn();
            return;
        }

        owner.getNavigation().startMovingTo(t.getX(), t.getY(), t.getZ(), speed);
    }

    @Override
    public boolean shouldContinue(){
        return !this.owner.getNavigation().isIdle();
    }

   

    @Override
    public void tick(){
        if (this.owner.getRandom().nextFloat() < 0.8F) {
            this.owner.getJumpControl().setActive();
         }


    }
    
}
