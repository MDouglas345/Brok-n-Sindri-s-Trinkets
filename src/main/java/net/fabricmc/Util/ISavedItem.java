package net.fabricmc.Util;

import net.minecraft.item.ItemStack;

public interface ISavedItem {
    public void setSavedItem(ItemStack stack);

    public ItemStack getSavedItem();

    public void setSavedItemOwner(String name);

    public String getSavedItemOwner();

    public void setIndexIntoStack(int i);

    public int  getIndexIntoStack();

    public void reset();
}
