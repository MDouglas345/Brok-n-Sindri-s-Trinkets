package net.fabricmc.GenericItemBlock;

import javax.annotation.Nullable;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;

public class GenericItemBlockEntity extends BlockEntity{

    public  ItemStack                       SavedItem;
    public  Quaternion                      Quat;
    public  float                           Offset;
    public  int                             IndexIntoStack;
    public  ClientIdentification            Owner; // replace with client identification

    public GenericItemBlockEntity(BlockPos pos, BlockState state){
        super(BNSCore.GENERIC_ITEM_BLOCK_ENTITY, pos, state);
    }

    public void Initalize(ItemStack stack, Quaternion rot, float offset, int index, ClientIdentification owner){
       
        SavedItem = stack.copy(); //maybe need to copied version instead of a reference?
        Quat = rot;
        Offset = offset;
        IndexIntoStack = index;
        Owner = owner;
        BNSCore.LOGGER.info("We have done something good " + SavedItem.toString());
        this.markDirty();
    }

    

    @Override
    public void writeNbt(NbtCompound tag) {
        // Save the current value of the number to the tag
        
        super.writeNbt(tag);

        NbtCompound temp = new NbtCompound();
        this.SavedItem.writeNbt(temp);
        tag.put("savedItem",  this.SavedItem.writeNbt(temp));

        tag.putFloat("quatx", Quat.getX());
        tag.putFloat("quaty", Quat.getY());
        tag.putFloat("quatz", Quat.getZ());
        tag.putFloat("quatw", Quat.getW());
        tag.putFloat("roff", Offset);
        tag.putInt("index", IndexIntoStack);
        Owner.writeNBT(tag);

        
      

       
    }

 

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        
        NbtCompound savedItem = (NbtCompound)tag.get("savedItem");

        this.SavedItem = ItemStack.fromNbt(savedItem);

        this.Quat = new Quaternion(tag.getFloat("quatx"), tag.getFloat("quaty"), tag.getFloat("quatz"), tag.getFloat("quatw"));
        this.Offset = tag.getFloat("roff");
        this.IndexIntoStack = tag.getInt("index");
        this.Owner = ClientIdentification.fromNBT(tag);
    
    }

    

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
 
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    
}