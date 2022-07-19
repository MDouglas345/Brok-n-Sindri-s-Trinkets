package net.fabricmc.Entity.PassiveDwarf.Goals;

import java.util.Iterator;
import java.util.List;



import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.AIHelper.SensorHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;

public class PickupWantedGoal extends Goal {

    PassiveDwarf owner;
    List<ItemEntity> wanted;
    Iterator<ItemEntity> iterator;

    ItemEntity currentTarget;

    float speed;

    public PickupWantedGoal(PassiveDwarf entity, float goSpeed){
        owner = entity;
        speed = goSpeed;
    }

    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        wanted = SensorHelper.getNearestEntityByClass((ServerWorld)owner.world, ItemEntity.class, owner,(entity) ->{
                                                                                                                                        Item item = ((ItemEntity)entity).getStack().getItem();
                                                                                                                                        ItemGroup  group = item.getGroup();
                                                                                                                                        if (group == null || item == null){return false;}
                                                                                                                                        return Util.isItemThrowValid(item) || group.equals(ItemGroupRegistry.RUNE_STONE) || item.equals(Items.COOKED_BEEF);
                                                                                                                                    });

        if (owner.isInventoryFull()|| wanted.isEmpty()){return false;}

        iterator = wanted.iterator();
        
        currentTarget = iterator.next();

        goToTarget();
        return true;
    }

    @Override
    public void tick(){
        if (currentTarget == null && iterator.hasNext()){
            currentTarget = iterator.next();
            goToTarget();
        }
       
        if (owner.world.getEntityById(currentTarget.getId()) == null){
            return;
        }

        if (owner.getBlockPos().isWithinDistance(currentTarget.getBlockPos(), 1.1)){
            owner.lootWanted(currentTarget);
            if (iterator.hasNext()){
                currentTarget = iterator.next();
            }
        }
    }

    @Override
    public boolean shouldContinue(){
        if (currentTarget == null || owner.getNavigation().isIdle()){
            return canStart();
        }


        return true;
    }


    public void goToTarget(){
        owner.getNavigation().startMovingTo(currentTarget, speed);
    }

    
    
}
