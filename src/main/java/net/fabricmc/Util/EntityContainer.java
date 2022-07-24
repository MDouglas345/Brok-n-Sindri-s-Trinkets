package net.fabricmc.Util;

import java.util.UUID;

import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class EntityContainer {
    public UUID uuid;
    public BlockPos pos;

    public ItemStack Stack;
    public ClientIdentification Owner;

    public EntityContainer(UUID u, BlockPos p, ItemStack stack, String owner){
        uuid = u;
        pos = p;
        Owner = new ClientIdentification(owner, null);
        Stack = stack;
    }
}
