package net.fabricmc.Entity.PassiveDwarf.Goals;

import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.minecraft.entity.ai.goal.Goal;

public class ProcessMaterialsGoal extends Goal{
    PassiveDwarf owner;
    int         ticks = 0;

    public ProcessMaterialsGoal(PassiveDwarf d){
        owner = d;
    }

    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        return owner.WeaponToEnchant != null && owner.RunicStone != null;
    }


    @Override
    public void start(){
        ticks = 0;
    }

    @Override
    public void stop(){
        ticks = 0;
    }

    @Override
    public boolean canStop(){
        return ticks > 100;
    }

    @Override
    public void tick(){
        /**
         * Trigger animation bool?  
         * How long to last? 100 ticks?
         * 
         * 
         * logic to "enchant" weapon here. Needs to be unique. A unique tag.
         */
    }
    
}
