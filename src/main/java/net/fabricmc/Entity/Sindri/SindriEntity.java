package net.fabricmc.Entity.Sindri;

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
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class SindriEntity extends PassiveDwarf implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);

    

    public SindriEntity(EntityType<? extends PassiveDwarf> entityType, World world) {
        super(entityType, world);
        //TODO Auto-generated constructor stub
        improveLevel = 2;
        
    }

    public SindriEntity(World world) {
        super(EntityRegistry.SINDRI, world);
        //TODO Auto-generated constructor stub
        improveLevel = 2;
    }

    @Override
    public void registerControllers(AnimationData data) {
        
        ANIMATION_WALK_ID = "animation.sindri.walk";
        ANIMATION_IDLE_ID = "animation.sindri.idle";
        ANIMATION_MOOD_HAPPY_ID = "animation.sindri.greet_happy";
        ANIMATION_MOOD_UNHAPPY_ID = "animation.sindri.greet_unhappy";

        AnimationController<SindriEntity> walkcontroller = new AnimationController<SindriEntity>(this, "walkcontroller", 0, this::walkpredicate);
        AnimationController<SindriEntity> moodcontroller = new AnimationController<SindriEntity>(this, "moodcontroller", 0, this::moodpredicate);

		//controller.registerCustomInstructionListener(this::customListener);
		data.addAnimationController(walkcontroller);
        data.addAnimationController(moodcontroller);
        
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
