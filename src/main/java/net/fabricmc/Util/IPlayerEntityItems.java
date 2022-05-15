package net.fabricmc.Util;

import java.util.Stack;
import java.util.UUID;

import net.minecraft.util.math.BlockPos;

public interface IPlayerEntityItems {
    public void addReturnableItem(BlockPos pos);

    public void addReturnableThrownEntity(UUID i);

    public BlockPos getReturnableItem();

    public UUID getReturnableThrownEntityID();

    public Stack<BlockPos> getStack();
}
