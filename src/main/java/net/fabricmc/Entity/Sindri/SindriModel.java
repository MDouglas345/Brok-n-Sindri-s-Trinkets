package net.fabricmc.Entity.Sindri;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SindriModel extends AnimatedGeoModel<SindriEntity>{

    @Override
    public Identifier getAnimationFileLocation(SindriEntity animatable) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "animations/brok.animation.json");
    }

    @Override
    public Identifier getModelLocation(SindriEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "geo/sindri.geo.json");
    }

    @Override
    public Identifier getTextureLocation(SindriEntity object) {
        // TODO Auto-generated method stub
        return new Identifier(BNSCore.ModID, "textures/entity/sindri/sindri.png");
    }


    
}
