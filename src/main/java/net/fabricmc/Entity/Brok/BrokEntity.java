package net.fabricmc.Entity.Brok;

import net.fabricmc.Entity.EntityRegistry;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BrokEntity extends PassiveDwarf implements IAnimatable{

    public BrokEntity(EntityType<? extends PassiveDwarf> entityType, World world) {
        super(entityType, world);
        //TODO Auto-generated constructor stub
    }

    public BrokEntity(World world) {
        super(EntityRegistry.BROK, world);
        //TODO Auto-generated constructor stub
    }

    private AnimationFactory factory = new AnimationFactory(this);

   

    /**
     * Fun with goals! Coming Soon TM
     */

    @Override
    public void registerControllers(AnimationData data) {
        // TODO Auto-generated method stub
        
    }

    public static DefaultAttributeContainer.Builder setDefaultAttributes(){
        return VillagerEntity.createMobAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH ,500)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1f);
    }

    @Override
    public AnimationFactory getFactory() {
        // TODO Auto-generated method stub
        return factory;
    }
    
}
