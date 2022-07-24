package net.fabricmc.CardinalComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Map.Entry;

import net.fabricmc.Util.IDedUUID;
import net.fabricmc.Util.Util;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WeaponStackComponent implements IHashMapComponent<String, IDedUUID> {
    private World world;
    private HashMap<String, Stack<IDedUUID>> data = new HashMap<String, Stack<IDedUUID>>();

    public WeaponStackComponent(World w){
        world = w;
    }
    @Override
    public void readFromNbt(NbtCompound tag) {
        // TODO Auto-generated method stub

        NbtCompound WeaponStacks = tag.getCompound("WeaponStacks");


        for (String key : WeaponStacks.getKeys()){
            NbtList Stack = (NbtList) WeaponStacks.get(key);
            Stack<IDedUUID> newStack = new Stack<>();

            for (NbtElement stackEntry : Stack){
                NbtCompound record = (NbtCompound)stackEntry;

                IDedUUID iDedUUID = new IDedUUID(record.getInt("id"), NbtHelper.toUuid(record.get("uuid")), NbtHelper.toBlockPos((NbtCompound) record.get("blockpos")), record.getBoolean("isblock"));

                newStack.add(iDedUUID);
            }

            data.put(key, newStack);
        }
        
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        NbtCompound WeaponStacks = new NbtCompound();

        for (Entry<String, Stack<IDedUUID>> entry : data.entrySet()){
            NbtList Stack = new NbtList();

            for (IDedUUID stackEntry : entry.getValue()){
                NbtCompound ideduid = new NbtCompound();
                
                ideduid.put("blockpos", NbtHelper.fromBlockPos(stackEntry.pos));
                ideduid.put("uuid", NbtHelper.fromUuid(stackEntry.uuid));
                ideduid.putInt("stackid", stackEntry.id);
                ideduid.putBoolean("isblock", stackEntry.isBlock);

                Stack.add(ideduid);
            }

            WeaponStacks.put(entry.getKey(), Stack);
        }

        tag.put("WeaponStacks", WeaponStacks);
    }

    @Override
    public int Push(String key, IDedUUID value) {
        // TODO Auto-generated method stub
        value.id = Util.randgen.nextInt();
        if (!data.containsKey(key)){
            Stack<IDedUUID> stack = new Stack<>();
            data.put(key, stack);
        }

        Stack<IDedUUID> stack = data.get(key);
        stack.add(value);

        return value.id;
    }

    @Override
    public IDedUUID Pop(String key) {
        // TODO Auto-generated method stub
        if (!data.containsKey(key)){
            return null;
        }

        Stack<IDedUUID> stack = data.get(key);
        return stack.pop();
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
    public IDedUUID Peek(String key) {
        // TODO Auto-generated method stub
        if (!data.containsKey(key)){
            return null;
        }

        Stack<IDedUUID> stack = data.get(key);

        return stack.peek();
    }

    @Override
    public IDedUUID Peek(String key, int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void Reset() {
        // TODO Auto-generated method stub
        data = new HashMap<String, Stack<IDedUUID>>();
    }

    @Override
    public List<IDedUUID> getList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void Update(String key, int id, IDedUUID value) {
        // TODO Auto-generated method stub
        
    }
    
}
