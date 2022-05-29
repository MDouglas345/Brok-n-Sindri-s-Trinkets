package net.fabricmc.GenericItemBlock;

import javax.annotation.Nullable;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GenericItemBlockEntity extends BlockEntity{

    public  ItemStack                       SavedItem;
    public  Quaternion                      Quat;
    public  float                           Offset;
    public  int                             IndexIntoStack;
    public  ClientIdentification            Owner; // replace with client identification

    DefaultParticleType             PTypeToUse = null;

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
        
        if ((EnchantmentHelper.getLevel(BNSCore.FrostTool, SavedItem) > 0) || (EnchantmentHelper.getLevel(BNSCore.FrostWeapon, SavedItem) > 0)){
            //if item has Frost, spawn frost particles
            PTypeToUse = ParticleRegistery.FROST_PARTICLE;
        }
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

    public static void tick(World world, BlockPos pos, BlockState state, GenericItemBlockEntity be) {
       be.SpawnPulsingParticles(world, be.getPos());

       
    }

    public void SpawnPulsingParticles(World world, BlockPos Pos){
        //potentily needs to transalte the origin point to the center

        // also needs a velocity vector that points randomly outwards from the position;
        if (PTypeToUse == null){return;}

        Vec3d spot = new Vec3d(Pos.getX() + 0.5, Pos.getY() + 0.5, Pos.getZ() + 0.5); // adjustment here!

        int p_amount = 5;
        for (int i = 0; i < p_amount; i++){
            Vec3d dir = Util.getRandomDirectionUnitSphere(0, 2 * MathHelper.PI, 0, MathHelper.PI / 2);
            dir.normalize();
            dir = dir.multiply(Util.getRandomDouble(0.1, 0.5));
            world.addParticle(PTypeToUse,
            spot.getX(), spot.getY(), spot.getZ(),
                          dir.x, dir.y, dir.z);
        }
    }
    
}