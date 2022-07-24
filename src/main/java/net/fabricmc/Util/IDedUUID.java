package net.fabricmc.Util;

import java.util.UUID;

import net.minecraft.util.math.BlockPos;

public class IDedUUID {
    public UUID uuid;
    public int id;
    public BlockPos pos;
    public boolean isBlock;

    public IDedUUID(int i, UUID u, BlockPos p, boolean b){
        uuid = u;
        id = i;
        pos = p;
        isBlock = b;
    }

    public IDedUUID(int i, UUID u, BlockPos p){
        this(i,u,p,false);
    }

    public IDedUUID(int i, BlockPos p){
        this(i, UUID.randomUUID(), p, true);
    }
}
