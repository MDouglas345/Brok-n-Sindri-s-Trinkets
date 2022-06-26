package net.fabricmc.Entity.PassiveDwarf;

import net.fabricmc.Entity.PassiveDwarf.Goals.GoToDwarvenForgeGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.ProcessMaterialsGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public  class PassiveDwarf extends PathAwareEntity{

    public boolean      foundForge = false;
    public BlockPos     lastKnownForgeLocation = null;

    public Item         WeaponToEnchant = null;
    public Item         RunicStone = null;

    protected PassiveDwarf(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        //TODO Auto-generated constructor stub
        
    }

    protected void initGoals(){
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GoToDwarvenForgeGoal(this, 0.26f));
        this.goalSelector.add(1, new ProcessMaterialsGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.2f));
    }


    public void despawn(){
        /**
         * Drop any weapon or runic stone and then discard
         */


    }

    
  
}
