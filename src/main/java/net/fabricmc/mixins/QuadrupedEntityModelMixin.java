package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.Util.IAccessModelParts;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.model.ModelPart;

@Mixin(QuadrupedEntityModel.class)
public class QuadrupedEntityModelMixin implements IAccessModelParts{

    @Shadow protected ModelPart body;

    @Override
    public ModelPart getBody() {
        // TODO Auto-generated method stub
        return body;
    }
    
}
