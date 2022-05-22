
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

import java.util.UUID;

import org.apache.logging.log4j.core.tools.picocli.CommandLine.MaxValuesforFieldExceededException;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.GenericThrownItemEntity.States.BounceState;
import net.fabricmc.GenericThrownItemEntity.States.GenericThrownItemEntityState;
import net.fabricmc.GenericThrownItemEntity.States.GroundedState;
import net.fabricmc.GenericThrownItemEntity.States.ReturnState;
import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.GenericThrownItemEntity.States.ThrownState;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.Util.PacketUtil;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class GenericThrownItemEntity extends ThrownItemEntity implements ISavedItem {

    public ItemStack                itemToRender;
    public Quaternion               originalRot;
    public float                    rotoffset = 0;
    public boolean                  run          = true;
    public float                    bonusAttack = 0;

    public float                    rotSpeed = 0;

    public ClientIdentification     Owner;

    public int                      StackID;

    public boolean                  Maxed   = false;                   

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
        
            // Need to work on the damage calculation to accomodate enchantments and various affects
            
            Item item = itemToRender.getItem();
            float attackDamage = item instanceof MiningToolItem ? ((MiningToolItem)item).getAttackDamage(): item instanceof SwordItem ? ((SwordItem)item).getAttackDamage() : 0;
            entityHitResult.getEntity().damage(DamageSource.player((PlayerEntity) getOwner()), attackDamage + 0.5f * attackDamage * bonusAttack);

            if (!Maxed || (EnchantmentHelper.getLevel(BNSCore.PinnedTool, itemToRender) == 0 || EnchantmentHelper.getLevel(BNSCore.PinnedWeapon, itemToRender) == 0)){
               
                
                Vec3d dir = getVelocity().multiply(-1).add(new Vec3d(0,4,0)).normalize();
                dir = dir.multiply(getVelocity().length() * 0.5f);
                setVelocity(dir);

                
            }

            //Master.updateVelocity(0.2f, dir);
        
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
       nbt.putUuid("owner", this.Owner.ID);
       nbt.putString("ownername", this.Owner.name);
       nbt.putBoolean("maxed", Maxed);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        
        itemToRender = ItemStack.fromNbt(nbt);
        originalRot = new Quaternion(nbt.getFloat("quatx"), nbt.getFloat("quaty"), nbt.getFloat("quatz"), nbt.getFloat("quatw"));
        rotoffset = nbt.getFloat("roff");
        bonusAttack = nbt.getFloat("bonus");
        this.Owner = new ClientIdentification(nbt.getString("ownername"), nbt.getUuid("owner"));
        this.Maxed = nbt.getBoolean("maxed");
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
        createPacket.writeString(this.Owner.name);
        createPacket.writeBoolean(this.Maxed);

        return ServerPlayNetworking.createS2CPacket(NetworkConstants.EstablishThrownItem, createPacket);
	}


   
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

    public void SetOwner(String name, UUID id){
        this.Owner = new ClientIdentification(name, id);
    }

    public void SetStackID(int i ){
        this.StackID = i;
    }

    public void SetMaxed(boolean m){
        Maxed = m;
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

    public static GenericThrownItemEntity CreateNew(ServerWorld world, PlayerEntity owner, Vec3d spawnposition, ItemStack itemstack){
                GenericThrownItemEntity e = new GenericThrownItemEntity(BNSCore.GenericThrownItemEntityType, world);
				Vec3d pos = spawnposition;

				e.setPos(pos.x, pos.y, pos.z);
				e.updatePosition(pos.x, pos.y, pos.z);
				e.updateTrackedPosition(pos.x, pos.y, pos.z);
				e.setOwner(owner); // Assume the thrower is always THE owner!
				e.SetOwner(owner);
				e.setItem(itemstack.copy());
                e.SetMaxed(false);
                
                //e.setVelocity(client, client.getPitch(), client.getYaw(), 0, timeHeld / 40f , 0f);
                e.setBonusAttack(1);
                e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 10, true));

                /**
                 *  pitch = asin(-d.Y);
                 *  yaw = atan2(d.X, d.Z)
                 */
                
                
				

			
				e.setRSpeed(5f);

                

                return e;
    }

    public void throwRandomly(){
        
            float yaw = Util.randgen.nextFloat() * 360;
            setVelocity(this.getOwner(), Util.randgen.nextFloat() * -90 + 10, yaw, 0, Util.randgen.nextFloat() * 0.5f , 0.4f);
            setQuat(new Quaternion(Vec3f.POSITIVE_Y, 180 - yaw, true));
        
    }

    public static GenericThrownItemEntity CreateNew(ServerWorld world, String owner, UUID uuid, Vec3d spawnposition, ItemStack itemstack){
        GenericThrownItemEntity e = new GenericThrownItemEntity(BNSCore.GenericThrownItemEntityType, world);
        Vec3d pos = spawnposition;

        e.setPos(pos.x, pos.y, pos.z);
        e.updatePosition(pos.x, pos.y, pos.z);
        e.updateTrackedPosition(pos.x, pos.y, pos.z);
        e.setOwner(e); // Assume the thrower is always THE owner!
        e.SetOwner(owner, uuid);
        e.setItem(itemstack.copy());
        e.SetMaxed(false);


        
        //e.setVelocity(client, client.getPitch(), client.getYaw(), 0, timeHeld / 40f , 0f);
        e.setBonusAttack(1);
        e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 10, true));

        /**
         *  pitch = asin(-d.Y);
         *  yaw = atan2(d.X, d.Z)
         */
        
        
        

    
        e.setRSpeed(5f);

        

        return e;
}


    public static GenericThrownItemEntity CreateNew(ServerWorld world, PlayerEntity client, ItemStack itemstack, float timeHeld, boolean random){
                GenericThrownItemEntity e = new GenericThrownItemEntity(BNSCore.GenericThrownItemEntityType, world);
				Vec3d pos = client.getPos();

				e.setPos(pos.x, client.getEyeY(), pos.z);
				e.updatePosition(pos.x, client.getEyeY(), pos.z);
				e.updateTrackedPosition(pos.x, client.getEyeY(), pos.z);
				e.setOwner(client); // Assume the thrower is always THE owner!
				e.SetOwner(client);
				e.setItem(itemstack.copy());

                float speed = 0;

                if (timeHeld > 15){
                    e.SetMaxed(true);
                    speed = 1.5f;
                }
                else{
                    e.SetMaxed(false);
                    speed = 0.8f;
                }

                if (!random){
				    e.setVelocity(client, client.getPitch(), client.getYaw(), 0, speed , 0f);
				    e.setBonusAttack(timeHeld / 40f);
                    e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 180 - client.getYaw(), true));
                }
                else{
                    float yaw = Util.randgen.nextFloat() * 360;
                    e.setVelocity(client, Util.randgen.nextFloat() * -90 + 10, yaw, 0, Util.randgen.nextFloat() * 0.5f , 0.4f);
				    e.setBonusAttack(timeHeld / 40f);
                    e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 180 - yaw, true));
                }
				

			
				e.setRSpeed(timeHeld*4f);

				UUIDStackComponent stack = mycomponents.EntityUUIDs.get(world.getLevelProperties());

				int id = stack.Push(client.getEntityName(), e.getUuid());

				e.SetStackID(id);

                return e;
        
    }

    public void ThrowRandom(float force){
        
        float yaw = Util.randgen.nextFloat() * 360;
        float pitch =  Util.randgen.nextFloat() * -90 + 10;
        float roll = 0;
        
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));

        this.setVelocity(f, g, h, force, 0);
        this.setBonusAttack(force);
        this.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 180 - yaw, true));
    }

    @Override
    public void setSavedItem(ItemStack stack) {
        this.itemToRender = stack;
        
    }

    @Override
    public ItemStack getSavedItem() {
        // TODO Auto-generated method stub
        return this.itemToRender;
    }

    @Override
    public void setSavedItemOwner(String name) {
        // TODO Auto-generated method stub
        if (this.Owner == null){
            this.Owner = new ClientIdentification(name, null);
            return;
        }
        this.Owner.name = name;
        
    }

    @Override
    public String getSavedItemOwner() {
        // TODO Auto-generated method stub
        if (this.Owner == null){
            return null;
        }
        return this.Owner.name;
    }

    @Override
    public void setIndexIntoStack(int i) {
        this.StackID = i;
        
    }

    @Override
    public int getIndexIntoStack() {
        // TODO Auto-generated method stub
        return this.StackID;
    }

    @Override
    public void reset() {
        this.setSavedItem(new ItemStack(Items.AIR,1));
        this.setSavedItemOwner("");
        
    }

  
}
