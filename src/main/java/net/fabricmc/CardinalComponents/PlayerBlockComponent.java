package net.fabricmc.CardinalComponents;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;

public class PlayerBlockComponent implements IHashMapComponent<String, Boolean> {

    private HashMap<String, Boolean> data = new HashMap<String,Boolean>();

    public PlayerBlockComponent(World w){
         
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        NbtCompound hash = (NbtCompound) tag.get("PlayerBlocks");
        
        for(String name : hash.getKeys()){

            boolean value = hash.getBoolean(name);

            Push(name, value);
        }
        
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        /*
         * Strucutre for NBT
         * <BlockPos> as tag :  Boolean as value
         */

        NbtCompound hash = new NbtCompound();


         for(Entry<String, Boolean> entry : data.entrySet()){

            hash.putBoolean(entry.getKey(), entry.getValue());
         }

         tag.put("PlayerBlocks", hash);

    }

    @Override
    public int Push(String key, Boolean value) {
        // TODO Auto-generated method stub
        
        data.put(key, value);
        return 0;
    }

    @Override
    public Boolean Pop(String key) {
        // TODO Auto-generated method stub
        return data.get(key);
    }

    @Override
    public void Remove(String key, int id) {
        // TODO Auto-generated method stub
        if (!data.containsKey(key)){
            return;
        }
        data.remove(key);
        
    }

    @Override
    public Boolean Peek(String key) {
        // TODO Auto-generated method stub
        if (!data.containsKey(key)){
            return null;
        }
        return data.get(key);
    }

    @Override
    public Boolean Peek(String key, int id) {
        // TODO Auto-generated method stub
        if (!data.containsKey(key)){
            return null;
        }
        return data.get(key);
    }

    @Override
    public void Reset() {
        // TODO Auto-generated method stub
        data = new HashMap<String,Boolean>();
    }
    
}
