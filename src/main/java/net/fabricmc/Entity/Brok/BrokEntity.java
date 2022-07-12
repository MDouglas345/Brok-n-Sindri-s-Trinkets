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
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BrokEntity extends PassiveDwarf implements IAnimatable{

    

    public BrokEntity(EntityType<? extends PassiveDwarf> entityType, World world) {
        super(entityType, world);
        //TODO Auto-generated constructor stub
        improveLevel = 1;
    }

    public BrokEntity(World world) {
        super(EntityRegistry.BROK, world);
        //TODO Auto-generated constructor stub
        improveLevel = 1;
    }

    private AnimationFactory factory = new AnimationFactory(this);

   

    /**
     * Fun with goals! Coming Soon TM
     */

    


    @Override
    public void registerControllers(AnimationData data) {
        ANIMATION_WALK_ID = "animation.brok.walk";
        ANIMATION_IDLE_ID = "animation.brok.idle";
        ANIMATION_MOOD_HAPPY_ID = "animation.brok.greet_happy";
        ANIMATION_MOOD_UNHAPPY_ID = "animation.brok.greet_unhappy";
        AnimationController<BrokEntity> walkcontroller = new AnimationController<BrokEntity>(this, "walkcontroller", 0, this::walkpredicate);
        AnimationController<BrokEntity> moodcontroller = new AnimationController<BrokEntity>(this, "moodcontroller", 0, this::moodpredicate);

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
