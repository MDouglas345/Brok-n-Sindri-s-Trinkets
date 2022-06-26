package net.fabricmc.Entity.Brok;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BrokRenderer extends GeoEntityRenderer<BrokEntity>{

    public BrokRenderer(Context ctx) {
        super(ctx, new BrokModel());
        //TODO Auto-generated constructor stub
        
    }

 
    
}
