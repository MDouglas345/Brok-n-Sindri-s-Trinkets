package net.fabricmc.Entity.PassiveDwarf.Goals;

import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemGroup;

public class ProcessMaterialsGoal extends Goal{
    PassiveDwarf owner;
    int         ticks = 0;

    public ProcessMaterialsGoal(PassiveDwarf d){
        owner = d;
    }

    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        return owner.inventoryContainsRune() && owner.inventoryContainsWeapon();
    }


    @Override
    public void start(){
        ticks = 0;
    }

    @Override
    public void stop(){
        ticks = 0;
        /*
         * Throw out new processed item after done.
         */
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
