package net.fabricmc.Entity.PassiveDwarf.Goals;

import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.GenericThrownItemEntity.GenericThrownItemEntity;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.fabricmc.Items.RuneStones.RuneStoneItem;
import net.fabricmc.Items.RuneStones.LightningStone.LightningStoneItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class ProcessMaterialsGoal extends Goal{
    PassiveDwarf owner;
    int         ticks = 0;

    public ProcessMaterialsGoal(PassiveDwarf d){
        owner = d;
    }

    @Override
    public boolean canStart() {
        // TODO Auto-generated method stub
        return owner.inventoryContainsRune() && owner.inventoryContainsWeapon() && owner.isCloseToDF();
    }


    @Override
    public void start(){
        ticks = 0;

    }

    @Override
    public void stop(){
        ticks = 0;
        
        ItemStack weapon = owner.inventory.getStack(0);

        RuneStoneItem rune = (RuneStoneItem) owner.inventory.getStack(1).getItem();

        rune.enchantItem(weapon);

        Vec3d pos = owner.getPos();
        
        ItemEntity entity = new ItemEntity(owner.world, pos.getX(), pos.getY(), pos.getZ(), weapon);

        owner.world.spawnEntity(entity);

        owner.resetInventory();
    
    }

    @Override
    public boolean shouldContinue(){
        return ticks < 100;
    }

    @Override
    public void tick(){
        ticks++;
        /**
         * Trigger animation bool?  
         * How long to last? 100 ticks?
         * 
         * 
         * logic to "enchant" weapon here. Needs to be unique. A unique tag.
         */
    }
    
}
