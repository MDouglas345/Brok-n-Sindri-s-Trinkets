package net.fabricmc.Entity.Brok;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class BrokModel extends AnimatedGeoModel<BrokEntity>{

    @Override
    public Identifier getAnimationFileLocation(BrokEntity animatable) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "animations/brok.animation.json");
    }

    @Override
    public Identifier getModelLocation(BrokEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "geo/brok.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BrokEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "textures/entity/brok/brok.png");
    }

    public void setLivingAnimations(BrokEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");

		LivingEntity entityIn = (LivingEntity) entity;
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
	}

    
    
}
