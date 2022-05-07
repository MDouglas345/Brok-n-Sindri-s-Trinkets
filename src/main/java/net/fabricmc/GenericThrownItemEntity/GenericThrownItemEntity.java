package net.fabricmc.GenericThrownItemEntity;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.States.BounceState;
import net.fabricmc.GenericThrownItemEntity.States.GenericThrownItemEntityState;
import net.fabricmc.GenericThrownItemEntity.States.GroundedState;
import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.GenericThrownItemEntity.States.ThrownState;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.Util.PacketUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;

public class GenericThrownItemEntity extends ThrownItemEntity {

    public ItemStack       itemToRender;
    public Quaternion      originalRot;
    public float           rotoffset = 0;
    public boolean         run          = true;

    public float            rotSpeed = 0;

    public GenericThrownItemEntityState[]  States = 
    {   new ThrownState(this),
        new StuckState(this),
        new BounceState(this),
        new GroundedState(this)};

    public GenericThrownItemEntityState    ActiveState = States[0];

    public GenericThrownItemEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
	}
 
	public GenericThrownItemEntity(World world, LivingEntity owner) {
		super(BNSCore.GenericThrownItemEntityType, owner, world); // null will be changed later
	}
 
	public GenericThrownItemEntity(World world, double x, double y, double z) {
		super(BNSCore.GenericThrownItemEntityType, x, y, z, world); // null will be changed later
	}
 
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
		ActiveState.onEntityHit(entityHitResult);

        
	}

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult){
        ActiveState.onBlockHit(blockHitResult);
      
       
        
        
    }
    @Override
	protected void onCollision(HitResult hitResult) { // called on collision with a block
		ActiveState.onCollision(hitResult);
        
       
        
            //BNSCore.LOGGER.info("I just hit a block. Where am I?");
            
        
        
        
	}


    @Override
    public void tick(){
        ActiveState.Tick();
        
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt){
        super.writeNbt(nbt);
        
       itemToRender.writeNbt(nbt);
       nbt.putFloat("quatx", originalRot.getX());
       nbt.putFloat("quaty", originalRot.getY());
       nbt.putFloat("quatz", originalRot.getZ());
       nbt.putFloat("quatw", originalRot.getW());
       nbt.putFloat("roff", rotoffset);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        
        itemToRender = ItemStack.fromNbt(nbt);
        originalRot = new Quaternion(nbt.getFloat("quatx"), nbt.getFloat("quaty"), nbt.getFloat("quatz"), nbt.getFloat("quatw"));
        rotoffset = nbt.getFloat("roff");
    }

    @Override
	public Packet createSpawnPacket() {
		PacketByteBuf createPacket = PacketByteBufs.create();

        if (this.world.isClient){
            throw new IllegalStateException("SpawnPacketUtil.create called on the logical client!");
        }

        createPacket.writeItemStack(this.itemToRender);
        PacketUtil.WriteVec3d(createPacket, this.getPos());
        PacketUtil.WriteQuaternion(createPacket, this.originalRot);
        createPacket.writeFloat(this.rotSpeed);

        createPacket.writeInt(this.getId());
        createPacket.writeUuid(this.getUuid());

        return ServerPlayNetworking.createS2CPacket(NetworkConstants.EstablishThrownItem, createPacket);
	}


    @Override
    protected Item getDefaultItem() {
        // TODO Auto-generated method stub
        return itemToRender.getItem();
    }

    public void setItem(ItemStack s){
        itemToRender = s;
    }

    public void setQuat(Quaternion q){
        originalRot = q;
    }

    public void setROff(float r){
        rotoffset = r;
    }

    public void setRSpeed(float r){
        rotSpeed = r;
    }
    

    public void SuperTick(){
        super.tick();
    }

    public void SuperOnCollision(HitResult hitResult){
        super.onCollision(hitResult);
    }

    public void SuperOnBlockHit(BlockHitResult blockHitResult){
        super.onBlockHit(blockHitResult);
    }

    public void SuperOnEntityHit(EntityHitResult entityHitResult){
        super.onEntityHit(entityHitResult);
    }

    public void ChangeState(int i){
        BNSCore.LOGGER.info("Entity changed to state " + i);
        ActiveState.OnExit();
        ActiveState = States[i];
        ActiveState.OnEnter();
    }
}
