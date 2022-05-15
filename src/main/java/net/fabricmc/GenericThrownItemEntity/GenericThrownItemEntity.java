
/**
 * 
 *  problem : diffeernt items should land differently, utilize a json with min, max values for angles
 *  use itemstack.getItem() instance of SwordItem to determin if the stack is a sword type.
 * 
 * 
 * 
 * Entity.onTrackedDataSet(TrackedData<?>) useful for getting update on whether tracked data has changed
 */
package net.fabricmc.GenericThrownItemEntity;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.GenericThrownItemEntity.States.BounceState;
import net.fabricmc.GenericThrownItemEntity.States.GenericThrownItemEntityState;
import net.fabricmc.GenericThrownItemEntity.States.GroundedState;
import net.fabricmc.GenericThrownItemEntity.States.ReturnState;
import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.GenericThrownItemEntity.States.ThrownState;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.Util.PacketUtil;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GenericThrownItemEntity extends ThrownItemEntity {

    public ItemStack                itemToRender;
    public Quaternion               originalRot;
    public float                    rotoffset = 0;
    public boolean                  run          = true;
    public float                    bonusAttack = 0;

    public float                    rotSpeed = 0;

    public ClientIdentification     Owner;

    public int                      StackID;

    public static final TrackedData<Byte> STATE = DataTracker.registerData(GenericThrownItemEntity.class, TrackedDataHandlerRegistry.BYTE);

    public GenericThrownItemEntityState[]  States = 
    {   new ThrownState(this),
        new StuckState(this),
        new BounceState(this),
        new GroundedState(this),
        new ReturnState(this)};

    public GenericThrownItemEntityState    ActiveState = States[0];

    public GenericThrownItemEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
        setNoGravity(true);
	}
 
	public GenericThrownItemEntity(World world, LivingEntity owner) {
		super(BNSCore.GenericThrownItemEntityType, owner, world); // null will be changed later
        setNoGravity(true);
	}
 
	public GenericThrownItemEntity(World world, double x, double y, double z) {
		super(BNSCore.GenericThrownItemEntityType, x, y, z, world); // null will be changed later
        setNoGravity(true);
	}

    @Override
    public void initDataTracker(){
        super.initDataTracker();
        this.dataTracker.startTracking(STATE, (byte)0);
    }


    
    

    public void Attack(EntityHitResult entityHitResult){
        if (!world.isClient){
            // Need to work on the damage calculation to accomodate enchantments and various affects
            
            Item item = itemToRender.getItem();
            float attackDamage = item instanceof MiningToolItem ? ((MiningToolItem)item).getAttackDamage(): item instanceof SwordItem ? ((SwordItem)item).getAttackDamage() : 0;
            entityHitResult.getEntity().damage(DamageSource.player((PlayerEntity) getOwner()), attackDamage + 0.5f * attackDamage * bonusAttack);

            if (Util.randgen.nextFloat() > 0.8){
            
                Vec3d dir = getVelocity().multiply(-1).normalize();
                dir = dir.multiply(getVelocity().length() * 0.2f);
                setVelocity(dir);

            }
           

            //Master.updateVelocity(0.2f, dir);
        }
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

        if (world.isClient){
            if (getDataTracker().isDirty()){
                ChangeState(getState());
                getDataTracker().clearDirty();
            }
        }
        
        ActiveState.Tick();
        
    }

    public int getState(){
        return this.dataTracker.get(STATE);
    }

    public void setState(int i){
        this.dataTracker.set(STATE, (byte)i);
        
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
       nbt.putFloat("bonus", bonusAttack);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        
        itemToRender = ItemStack.fromNbt(nbt);
        originalRot = new Quaternion(nbt.getFloat("quatx"), nbt.getFloat("quaty"), nbt.getFloat("quatz"), nbt.getFloat("quatw"));
        rotoffset = nbt.getFloat("roff");
        bonusAttack = nbt.getFloat("bonus");
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
        createPacket.writeFloat(this.bonusAttack);

        createPacket.writeInt(this.getId());
        createPacket.writeUuid(this.getUuid());
        createPacket.writeUuid(this.Owner.ID);

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

    public void setBonusAttack(float a){
        bonusAttack = a;
    }

    public void SetOwner(PlayerEntity entity){
        // may cause bugs if player name is absurdly long. i.e < 255 characters
        this.Owner = new ClientIdentification(entity.getName().asString(), entity.getUuid());
    }

    public void SetStackID(int i ){
        this.StackID = i;
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
        if (!world.isClient){
            setState(i);
        }
        BNSCore.LOGGER.info("Entity changed to state " + i);
        ActiveState.OnExit();
        ActiveState = States[i];
        ActiveState.OnEnter();
    }
}
