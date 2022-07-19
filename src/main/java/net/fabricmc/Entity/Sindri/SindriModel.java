package net.fabricmc.Entity.Sindri;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class SindriModel extends AnimatedGeoModel<SindriEntity>{

    @Override
    public Identifier getAnimationResource(SindriEntity animatable) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "animations/sindri.animation.json");
    }

    @Override
    public Identifier getModelResource(SindriEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "geo/sindri.geo.json");
    }

    @Override
    public Identifier getTextureResource(SindriEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "textures/entity/sindri/sindri.png");
    }

    public void setLivingAnimations(SindriEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
		IBone head = this.getAnimationProcessor().getBone("Head");

		LivingEntity entityIn = (LivingEntity) entity;
		EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
		head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
		head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
	}
    
}
