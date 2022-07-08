package net.fabricmc.Util;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;

public interface IHasOwner {
    
    public String getOwner();

    public void setOwner(String s);



}
