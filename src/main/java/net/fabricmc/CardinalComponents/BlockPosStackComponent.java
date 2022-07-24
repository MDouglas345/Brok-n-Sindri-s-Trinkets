package net.fabricmc.CardinalComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import net.fabricmc.Util.IDedBlockPos;
import net.fabricmc.Util.Util;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;

public class BlockPosStackComponent implements IHashMapComponent<String, BlockPos> {

    /***
     * listen. You need to be using a stack for these. If you dont, how will you get the latest weapon thrown
     *  for a given player? Think, Mark, Think!
     */

     public BlockPosStackComponent(Scoreboard sb, MinecraftServer ms){
         
     }


     public BlockPosStackComponent(WorldProperties p){

     }

     public BlockPosStackComponent(World w){
         
     }

    private HashMap<String,Stack<IDedBlockPos>> data = new HashMap<String, Stack<IDedBlockPos>>();

    @Override
    public void readFromNbt(NbtCompound tag) {

        /**
         * 
         * This function needs to verified that it is working as intended!
         */

        NbtCompound PlayerStoredBP = (NbtCompound) tag.get("BEPos");

        for(String name : PlayerStoredBP.getKeys()){
            
             NbtList array = (NbtList) PlayerStoredBP.get(name);
             Stack<IDedBlockPos> temp = new Stack<IDedBlockPos>();
             List<IDedBlockPos> list = new ArrayList<>();

             for (NbtElement elem : array){
                 NbtList e = (NbtList)elem;

                 int ID = ((NbtDouble)e.get(0)).intValue();
                 int X = ((NbtDouble)e.get(1)).intValue();
                 int Y = ((NbtDouble)e.get(2)).intValue();
                 int Z = ((NbtDouble)e.get(3)).intValue();

                 //temp.push(new IDedBlockPos(ID,new BlockPos(X, Y, Z)));
                 list.add(new IDedBlockPos(ID,new BlockPos(X, Y, Z)));
             }

             temp.addAll(list); // this is needed so that the stack maintains the ordering.

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
         *          ->  "BEPos" -> PlayerStoredBP(Compound with player names as keys)
         *                                          -> "Bob" -> NbtList(contains a list of NbtLists)
         *                                                                  -> NbtList(Contains 4 doubles)
         *                                                                              -> <random int> the id
         *                                                                              -> 5 X
         *                                                                              -> 4 Y
         *                                                                              -> 2 Z
         *                                                  
         *                                          -> "Susan" -> NbtList(contains a list of NbtLists)
         *                                                                  -> NbtList(Contains 4 doubles)
         *                                                                              -> <random int> the id
         *                                                                              -> 5 X
         *                                                                              -> 4 Y
         *                                                                              -> 2 Z
         *                                                                  -> NbtList(Contains 4 doubles)
         *                                                                              -> <random int> the id
         *                                                                              -> 5 X
         *                                                                              -> 4 Y
         *                                                                              -> 2 Z
         *                                                 
         *  ETC...
         */

        NbtCompound PlayerStoredBP = new NbtCompound();
        //PlayerStoredBP.put(key, element)

       for(Entry<String, Stack<IDedBlockPos>> entry :data.entrySet() ){
          NbtList array = new NbtList();
          
          Stack<IDedBlockPos> hash = entry.getValue();


          for(IDedBlockPos position : hash){
            
              NbtList pos = new NbtList();
              
              pos.add(NbtDouble.of(position.ID));
              pos.add(NbtDouble.of(position.blockpos.getX()));
              pos.add(NbtDouble.of(position.blockpos.getY()));
              pos.add(NbtDouble.of(position.blockpos.getZ()));

              array.add(pos);
          }

          PlayerStoredBP.put(entry.getKey(), array);
            
       }
        tag.put("BEPos", PlayerStoredBP);
    }

    @Override
    public int Push(String key,  BlockPos value) {
        if (!data.containsKey(key)){
            Stack<IDedBlockPos> t = new Stack<IDedBlockPos>();
            int id = Util.randgen.nextInt();
            t.push(new IDedBlockPos(id, value));
            data.put(key, t);

            return id;
        }
        else{
            Stack<IDedBlockPos> t = data.get(key);
            int id = Util.randgen.nextInt();
            t.push(new IDedBlockPos(id, value));

            return id;
        } 
    }

    

    @Override
    public void Remove(String key, int id) {
        if (!data.containsKey(key)){
            return;
        }

        Stack<IDedBlockPos> t = data.get(key);

        int idremoval = -1;
        
        for(IDedBlockPos p : t){
            if (p.ID == id){
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
    public BlockPos Pop(String key) {
        if (!data.containsKey(key)){
            return null;
        }
       Stack<IDedBlockPos> t = data.get(key);

       try{
            return t.pop().blockpos;
        }
        catch(Exception e){
            return null;
        }
    }

    @Override
    public BlockPos Peek(String key) {
        if (!data.containsKey(key)){
            return null;
        }
       Stack<IDedBlockPos> t = data.get(key);

       try{
            return t.peek().blockpos;
        }
        catch(Exception e){
            return null;
        }
    }

    @Override
    public BlockPos Peek(String key, int id) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void Reset() {
        // TODO Auto-generated method stub
        data = new HashMap<String, Stack<IDedBlockPos>>();
    }


    @Override
    public List<BlockPos> getList() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void Update(String key, int id, BlockPos value) {
        // TODO Auto-generated method stub
        
    }

    
    
}
