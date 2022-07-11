package net.fabricmc.Entity.Sindri;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class SindriRenderer extends GeoEntityRenderer<SindriEntity>{

    public SindriRenderer(Context ctx) {
        super(ctx, new SindriModel());
        //TODO Auto-generated constructor stub
        
    }

 
    
}