package net.fabricmc.CardinalComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.Map.Entry;

import net.fabricmc.Util.IDedUUID;
import net.fabricmc.Util.Util;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.WorldProperties;

public class UUIDStackComponent implements  IHashMapComponent<String, UUID>{
    
    private HashMap<String,Stack<IDedUUID>> data = new HashMap<String, Stack<IDedUUID>>();

    public UUIDStackComponent(WorldProperties p){

    }



    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtCompound EntityStoredUUIDs = tag.getCompound("EntityUUIDs");

        for(String name : EntityStoredUUIDs.getKeys()){


            NbtList array = (NbtList) EntityStoredUUIDs.get(name);
            Stack<IDedUUID> temp = new Stack<IDedUUID>();
            List<IDedUUID> list = new ArrayList<>();

            for (NbtElement elem : array){
                NbtCompound t = (NbtCompound)elem;

                int id = ((NbtDouble)t.get("id")).intValue();
                UUID uuid = NbtHelper.toUuid(t.get("uuid"));

                list.add(new IDedUUID(id, uuid));
            }

            temp.addAll(list);

            data.put(name, temp);
        }
        
    }

    @Override
    public void writeToNbt(NbtCompound tag) {

        /**
         * This function needs to be verified that it actually works as intended :
         * 
         * The structure is as follows :
         *      TAG(Compound)
         *          ->  "EntityUUIDs" -> EntityStoredUUIDs(Compound with player names as keys)
         *                                          -> "Bob" -> NbtList(contains a list of NbtLists)
         *                                                                  -> NbtList(Contains 1 double, 1 UUID)
         *                                                                              -> <random int> the id
         *                                                                              -> uuid
         *                                                  
         *                                          -> "Susan" -> NbtList(contains a list of NbtLists)
         *                                                                  -> NbtList(Contains 4 doubles)
         *                                                                              -> <random int> the id
         *                                                                              -> uuid
         * 
         *                                                                  -> NbtList(Contains 4 doubles)
         *                                                                              -> <random int> the id
         *                                                                              -> uuid
         *  ETC...
         */

         NbtCompound EntityStoredUUIDs  = new NbtCompound();

         for(Entry<String, Stack<IDedUUID>> entry : data.entrySet()){
            NbtList array = new NbtList();

            Stack<IDedUUID> stack = entry.getValue();

            for (IDedUUID container : stack){
                NbtCompound t = new NbtCompound();
                t.put("id",NbtDouble.of(container.id));
                t.put("uuid",NbtHelper.fromUuid(container.uuid));
                array.add(t);
            }

            EntityStoredUUIDs.put(entry.getKey(), array);
         }
        tag.put("EntityUUIDs", EntityStoredUUIDs);
    }

    @Override
    public int Push(String key, UUID value) {
        if (!data.containsKey(key)){
            Stack<IDedUUID> t = new Stack<IDedUUID>();
            int id = Util.randgen.nextInt();
            t.push(new IDedUUID(id, value));
            data.put(key, t);

            return id;
        }
        else{
            Stack<IDedUUID> t = data.get(key);
            int id = Util.randgen.nextInt();
            t.push(new IDedUUID(id, value));

            return id;
        } 
    }


    @Override
    public UUID Pop(String key) {
        if (!data.containsKey(key)){
            return null;
        }
        Stack<IDedUUID> t = data.get(key);

        try{
            return t.pop().uuid;
        }
        catch(Exception e){
            return null;
        }
         
    }

    @Override
    public void Remove(String key, int id) {
        if (!data.containsKey(key)){
            return;
        }

        Stack<IDedUUID> t = data.get(key);

        int idremoval = -1;
        
        for(IDedUUID p : t){
            if (p.id == id){
                idremoval = t.indexOf(p);
                break;
            }
        }

        if (idremoval == -1){
            return;
        }

        t.remove(idremoval);
        
        
    }

    @Override
    public UUID Peek(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID Peek(String key, int id) {
        // TODO Auto-generated method stub
        return null;
    }

    
    
}
