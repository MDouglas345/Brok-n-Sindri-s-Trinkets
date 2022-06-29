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

public class GlobalPosRecordComponent implements IHashMapComponent<String, BlockPos> {

    /***
     * listen. You need to be using a stack for these. If you dont, how will you get the latest weapon thrown
     *  for a given player? Think, Mark, Think!
     */

     public GlobalPosRecordComponent(Scoreboard sb, MinecraftServer ms){
         
     }


     public GlobalPosRecordComponent(WorldProperties p){

     }

     public GlobalPosRecordComponent(World w){
         
     }

    private List<BlockPos> data = new ArrayList<>();

    @Override
    public void readFromNbt(NbtCompound tag) {

        /**
         * 
         * This function needs to verified that it is working as intended!
         */

        NbtCompound BlockPos = (NbtCompound) tag.get("globalPos");

        for (String name : BlockPos.getKeys()){
            NbtList pos = (NbtList)BlockPos.get(name);

            data.add(new BlockPos(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
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

        NbtCompound BlockPos = new NbtCompound();
        //PlayerStoredBP.put(key, element)

        for (BlockPos pos : data){
            NbtList newEntry = new NbtList();

            newEntry.add(NbtDouble.of(pos.getX()));
            newEntry.add(NbtDouble.of(pos.getY()));
            newEntry.add(NbtDouble.of(pos.getZ()));

            BlockPos.put(pos.toShortString(), newEntry);
        }

        tag.put("globalPos", BlockPos);

       
    }

    @Override
    public int Push(String key,  BlockPos value) {

        data.add(value);
        return data.size()-1;

    }

    

    @Override
    public void Remove(String key, int id) {

        data.remove(id);
        
        
    }

    @Override
    public BlockPos Pop(String key) {

      return null;
    }

    @Override
    public BlockPos Peek(String key) {
        return null;
    }

    @Override
    public BlockPos Peek(String key, int id) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void Reset() {
        // TODO Auto-generated method stub
        data = new ArrayList<>();
    }


    @Override
    public List<BlockPos> getList() {
        // TODO Auto-generated method stub
        return data;
        
    }

    
    
}
