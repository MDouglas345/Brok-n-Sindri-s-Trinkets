package net.fabricmc.Util.ClientIdentification;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientIdentification {

    public String   name;
    public UUID     ID;

    // Turning on this flag might cause more issues than its worth!
    public static boolean OnlineMode = false;

    public ClientIdentification(String name, UUID id){
        this.name = name;
        this.ID = id;
    }

    public boolean equals(ClientIdentification other){

        if (!OnlineMode){
            if (this.name.equals(other.name)){
                return true;
            }
            return false;
        }

        if (this.ID.equals(other.ID)){
            return true;
        }

        return false;
    }

    public  boolean isOwner(PlayerEntity entity){

        if (!OnlineMode){
            if (this.name.equals(entity.getName().getString())){
                return true;
            }
            return false;
        }

        if (this.ID.equals(entity.getUuid())){
            return true;
        }

        return false;
    }

    public void writeNBT(NbtCompound nbt){

        NbtCompound id = new NbtCompound();

        id.putString("Name", this.name);
        id.putUuid("UUID", this.ID);

        nbt.put("Identification", id);
    }

    public static ClientIdentification fromNBT(NbtCompound nbt){
        if (!nbt.contains("Identification")){
            return null;
        }

        NbtCompound id = nbt.getCompound("Identification");

        return new ClientIdentification(id.getString("Name"), id.getUuid("UUID"));
    }
    
}
