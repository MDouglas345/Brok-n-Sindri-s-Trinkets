package net.fabricmc.Entity.Brok;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BrokModel extends AnimatedGeoModel<BrokEntity>{

    @Override
    public Identifier getAnimationResource(BrokEntity animatable) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "animations/brok.animation.json");
    }

    @Override
    public Identifier getModelResource(BrokEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "geo/brok.geo.json");
    }

    @Override
    public Identifier getTextureResource(BrokEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "textures/entity/brok/brok.png");
    }
    
}
