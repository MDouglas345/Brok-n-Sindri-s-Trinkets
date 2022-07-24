package net.fabricmc.CardinalComponents;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

import net.fabricmc.Util.EntityContainer;
import net.fabricmc.Util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.World;

public class PinnedEntityComponent implements IHashMapComponent<UUID, EntityContainer> {
    
    private HashMap<UUID,EntityContainer> data = new HashMap<UUID, EntityContainer>();
    private World world;

    public PinnedEntityComponent(World w){
         world = w;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {

        NbtCompound StoredPinnedEntities = tag.getCompound("pinnedentities");

        for (String key : StoredPinnedEntities.getKeys()){
            UUID uuid = UUID.fromString(key);

            NbtCompound record = (NbtCompound) StoredPinnedEntities.get(key);

            EntityContainer container = new EntityContainer(uuid, NbtHelper.toBlockPos((NbtCompound) record.get("blockpos")), ItemStack.fromNbt((NbtCompound) record.get("itemstack")), record.getString("owner"));

            data.put(uuid, container);
        }
        
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound StoredPinnedEntities = new NbtCompound();
        //PlayerStoredBP.put(key, element)

       for(Entry<UUID, EntityContainer> entry :data.entrySet() ){
        NbtCompound record = new NbtCompound();
        NbtCompound item = new NbtCompound();
        EntityContainer container = entry.getValue();
        UUID key = entry.getKey();

        container.Stack.writeNbt(item);

        record.put("itemstack", item);
        record.put("blockpos", NbtHelper.fromBlockPos(container.pos));
        record.putString("owner", container.Owner.name);
        
        StoredPinnedEntities.put(key.toString(), record);
        

       }

       tag.put("pinnedentities", StoredPinnedEntities);
        
    }

    @Override
    public int Push(UUID key, EntityContainer value) {
        // TODO Auto-generated method stub

        data.put(key,value);
        return 0;
    }

    @Override
    public EntityContainer Pop(UUID key) {
        // TODO Auto-generated method stub
        EntityContainer container = data.get(key);
        data.remove(key);

        return container;
    }

    @Override
    public void Remove(UUID key, int id) {
        // TODO Auto-generated method stub
        data.remove(key);
    }

    @Override
    public EntityContainer Peek(UUID key) {
        // TODO Auto-generated method stub
        return data.get(key);
    }

    @Override
    public EntityContainer Peek(UUID key, int id) {
        // TODO Auto-generated method stub
        return data.get(key);
    }

    @Override
    public void Reset() {
        // TODO Auto-generated method stub
        data = new HashMap<UUID, EntityContainer>();
    }

    @Override
    public List<EntityContainer> getList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void Update(UUID key, int id, EntityContainer value) {
        // TODO Auto-generated method stub
        data.put(key, value);
    }
    
}
