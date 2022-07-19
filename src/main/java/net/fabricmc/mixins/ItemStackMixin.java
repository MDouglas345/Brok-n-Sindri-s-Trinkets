package net.fabricmc.mixins;

import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.Util.IHasOwner;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IHasOwner{

    public String Owner;

    @Override
    public String getOwner() {
        // TODO Auto-generated method stub
        return Owner;
    }

    @Override
    public void setOwner(String s) {
        Owner = s;
        
    }
    
}