
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

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.core.tools.picocli.CommandLine.MaxValuesforFieldExceededException;

import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.CardinalComponents.UUIDStackComponent;
import net.fabricmc.CardinalComponents.mycomponents;
import net.fabricmc.Enchantments.IWorldBehvaior;
import net.fabricmc.GenericItemBlock.GenericItemBlockEntity;
import net.fabricmc.GenericThrownItemEntity.States.BounceState;
import net.fabricmc.GenericThrownItemEntity.States.GenericThrownItemEntityState;
import net.fabricmc.GenericThrownItemEntity.States.GroundedState;
import net.fabricmc.GenericThrownItemEntity.States.ReturnState;
import net.fabricmc.GenericThrownItemEntity.States.StuckState;
import net.fabricmc.GenericThrownItemEntity.States.ThrownState;
import net.fabricmc.Particles.ParticleRegistery;
import net.fabricmc.Util.EnchantmentData;
import net.fabricmc.Util.ISavedItem;
import net.fabricmc.Util.NetworkConstants;
import net.fabricmc.Util.PacketUtil;
import net.fabricmc.Util.Util;
import net.fabricmc.Util.ClientIdentification.ClientIdentification;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class GenericThrownItemEntity extends ThrownItemEntity implements ISavedItem {

    public ItemStack                itemToRender;
    public Quaternion               originalRot;
    public float                    rotoffset = 0;
    public boolean                  run          = true;
    public float                    bonusAttack = 0;

    public float                    rotSpeed = 80;

    public ClientIdentification     Owner;

    public int                      StackID;

    public boolean                  Maxed   = false;     
    
    public DefaultParticleType             PTypeToUse = null;

    public float                    TimeToGrav = 20;

    public Random                          rand;

    public EnchantmentData                 enchantmentData;

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

    public Vec3f customRotationVec(){
        Quaternion r = originalRot.copy();
        r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(rotoffset, 0,0)));

        Vec3f dir = new Vec3f(0,1,0);
        dir.rotate(r);
        dir.normalize();

        return dir;
    }

    @Override
    protected void updateRotation() {
       /*
        Vec3d vec3d = this.getVelocity();
        double d = vec3d.horizontalLength();
        this.setPitch(ProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875)));
        this.setYaw(ProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875)));
        */

        
        Quaternion r = originalRot.copy();
        r.hamiltonProduct(Quaternion.fromEulerXyzDegrees(new Vec3f(rotoffset, 0,0)));

       
        

        Vec3f rot = r.toEulerXyz();

        this.setPitch(ProjectileEntity.updateRotation(this.prevPitch, rot.getX()));
        //this.setYaw(ProjectileEntity.updateRotation(this.prevYaw, rot.get()));
        
    }


    @Override
    public void initDataTracker(){
        super.initDataTracker();
        this.dataTracker.startTracking(STATE, (byte)0);
        
    }


    public void applyQuaternion(Quaternion q){
        Vec3f rot = q.toEulerXyzDegrees();
        this.setRotation(rot.getY(), rot.getX());
        
    }

    public void applyRotation(float yaw, float pitch){
        float pYaw = this.getYaw();
        float pPitch = this.getPitch();
        
        ProjectileEntity.updateRotation(pYaw, yaw);
        ProjectileEntity.updateRotation(pPitch, pitch);
        this.setRotation(pYaw, pPitch);
    }

    public void applyRawRotation(float yaw, float pitch){
        this.setRotation(yaw, pitch);
    }

    
    public void Deflect(float power){
        Vec3d dir = getVelocity().multiply(-1).add(new Vec3d(0,4,0)).normalize();
        dir = dir.multiply(getVelocity().length() * power);
        setVelocity(dir);

        if (Maxed){Maxed = false;}
        
        if (!world.isClient){
            world.playSound(null, getBlockPos(), SoundEvents.BLOCK_METAL_HIT, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }
    }

    public void Attack(EntityHitResult entityHitResult){
        
            // Need to work on the damage calculation to accomodate enchantments and various affects
           
            Entity entity = entityHitResult.getEntity();
            
            if (entity == null){
                return;
            }

            if ((entity instanceof LivingEntity) && (((LivingEntity)entity).isBlocking())){
                Deflect(0.3f);
                if (!entity.world.isClient){
                    entity.world.playSound(null, entity.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.HOSTILE, 1.0f, 1.0f);
                }
                return;
            }

            
            if (entity instanceof LivingEntity && (EnchantmentHelper.getLevel(BNSCore.PinnedTool, itemToRender) > 0 || EnchantmentHelper.getLevel(BNSCore.PinnedWeapon, itemToRender) > 0)&& this.Maxed){

                this.PinEntity(entityHitResult);
            }
            else{
                Deflect(0.3f);
            }

            Item item = itemToRender.getItem();
            float attackDamage = item instanceof MiningToolItem ? ((MiningToolItem)item).getAttackDamage(): item instanceof SwordItem ? ((SwordItem)item).getAttackDamage() : 0;
            
            
            entity.damage(DamageSource.player((PlayerEntity) getOwner()), attackDamage + 0.5f * attackDamage * bonusAttack);

        

            PlayerEntity source = (PlayerEntity) getOwner();

            if (source != null){
                Util.ApplyOnTargetDamageEnchantments(source, entity, itemToRender);
            }

            if (enchantmentData != null){
                IWorldBehvaior behavior = (IWorldBehvaior)enchantmentData.enchantment;

               
               
                behavior.OnEntityThrownHit(world, source, entityHitResult, enchantmentData.level, Maxed);
            }

         

           

            
            
        
    }

    public void PinEntity(EntityHitResult entityHitResult){
        try{
            LivingEntity e = (LivingEntity) entityHitResult.getEntity();
            
            ISavedItem eSaved = (ISavedItem) e;

           

           
            if (!world.isClient){

                e.addStatusEffect(new StatusEffectInstance(BNSCore.Paralysis, 999999999), this);
                
                eSaved.setSavedItem(itemToRender);
                eSaved.setSavedItemOwner(Owner.name);

                // cant be sure if this will be an issue if done only on server
               

                int id = BNSCore.pushEntityOntoStack((ServerWorld)world, Owner.name, e.getUuid());
                eSaved.setIndexIntoStack(id);

                BNSCore.removeEntityFromStack((ServerWorld)world, Owner.name, StackID);
            }
            
            if (e instanceof MobEntity){
                ((MobEntity) e).setPersistent();
            }

            discard();


        }
        catch(Exception e){

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
        else{
           
        }

        if (!world.isClient && this.isDespawned()){ // maybe also add if the server has ended?

             world.setBlockState(getBlockPos(),BNSCore.GENERIC_ITEM_BLOCK.getDefaultState());

        
            GenericItemBlockEntity be = (GenericItemBlockEntity)world.getBlockEntity(getBlockPos());
            

            BNSCore.removeEntityFromStack((ServerWorld) world, Owner.name, StackID);

            int id = BNSCore.pushBEOntoStack((ServerWorld) world, Owner.name, getBlockPos());

            be.Initalize(itemToRender, new Quaternion(0,0,0,1), (float)Util.getRandomDouble(100, 200),  id, Owner);

            this.kill();

        }

        Vec3d Pos = getPos();
        Vec3d Pos2 = getVelocity();
        BlockHitResult hitResult = world.raycast(new RaycastContext(Pos, Pos.add(Pos2), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this));

        if (hitResult.getType() != HitResult.Type.MISS){
            BlockPos hitpos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(hitpos);
            Block b = state.getBlock();
            float h = state.getHardness(world, hitpos);
            
            if (!state.isToolRequired() && h < 0.25 && h != -1.0){
                world.breakBlock(hitpos,true);
            }
            
            
        }
       
        //BNSCore.LOGGER.info(this.getRotationVector().toString());
        ActiveState.Tick();

        if (enchantmentData != null){
            IWorldBehvaior behvaior = (IWorldBehvaior)enchantmentData.enchantment;
            behvaior.OnTick(this, world);
        }
        
    }

    public int getState(){
        return this.dataTracker.get(STATE);
    }

    public void setState(int i){
        this.dataTracker.set(STATE, (byte)i);
        
    }

    public boolean canHit(Entity e){
        return super.canHit(e);
    }

    public boolean isDespawned(){
        Box box = new Box(getBlockPos().add(new BlockPos(-128,-128,-128)), getBlockPos().add(new BlockPos(128,128,128)) );

        List<PlayerEntity> players = Util.getNearbyPlayers(world, box);

        if (players.isEmpty()){
            return true;

        }
        int i = this.getType().getSpawnGroup().getImmediateDespawnRange(); // might need to set this manually!
        boolean res = true;
        for (PlayerEntity player : players){
            if (player.isDead()){continue;}
            double d = player.squaredDistanceTo(this);
            if (d < (double)(i * i)) {
                res = false;
            }
        }

        return res;
        
        
      
        /*

        PlayerEntity entity = world.getClosestPlayer(this, -1.0); // Mob entity does it like that?
        
        if (entity == null){
            return true;
        }
        if (Owner.isOwner(entity) && entity.isDead()){
            entity = world.getClosestPlayer(this, -1.0); // Mob entity does it like that?
        }

        if (entity != null) {
            int i = this.getType().getSpawnGroup().getImmediateDespawnRange(); // might need to set this manually!
            double d = entity.squaredDistanceTo(this);
            if (d > (double)(i * i)) {
                return true;
            }
        }

        return false;
        */

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
       nbt.putInt("stackid", this.StackID);

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

        this.StackID = nbt.getInt("stackid");
        this.rand = new Random(StackID);

        enchantmentData = Util.getSpecialThrownEnchantment(itemToRender);
    }

    @Override
	public Packet<?> createSpawnPacket() {
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

        createPacket.writeInt(this.StackID);

        return ServerPlayNetworking.createS2CPacket(NetworkConstants.EstablishThrownItem, createPacket);
	}

    public void SpawnTrailingParticles(){
        if (enchantmentData == null){return;}

       
        Vec3f dir = customRotationVec();
        Vec3d direction = new Vec3d(dir.getX(), dir.getY(), dir.getZ());

        IWorldBehvaior behavior = (IWorldBehvaior)enchantmentData.enchantment;

        behavior.SpawnTrailingParticles(world, getPos(), direction, enchantmentData.level, Maxed);

        
    }
   
    protected Item getDefaultItem() {
        // TODO Auto-generated method stub
        return itemToRender.getItem();
    }

    public void setItem(ItemStack s){
        itemToRender = s;

        enchantmentData = Util.getSpecialThrownEnchantment(itemToRender);

        if (EnchantmentHelper.getLevel(BNSCore.FrostTool, itemToRender) > 0 || EnchantmentHelper.getLevel(BNSCore.FrostWeapon, itemToRender) > 0){
            PTypeToUse = ParticleRegistery.FROST_PARTICLE;
        }
        else{

        }
    }

    

    public void setQuat(Quaternion q){
        originalRot = q;

        Vec3f rot = q.toEulerXyz();
        this.setRotation(rot.getY(), rot.getX());
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
        this.rand = new Random(i);
    }

    public void SetMaxed(boolean m){
        Maxed = m;

        if (m){
            this.TimeToGrav = 15;
        }
        else{
            this.TimeToGrav = 5;
        }
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
                
                
				

			
				e.setRSpeed(80f);

                

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
        if (uuid == null){
            uuid = e.getUuid();
        }
        e.setPos(pos.x, pos.y, pos.z);
        e.updatePosition(pos.x, pos.y, pos.z);
        e.updateTrackedPosition(pos.x, pos.y, pos.z);
        e.setOwner(e); // Assume the thrower is always THE owner!
        e.SetOwner(owner, uuid);
        e.setItem(itemstack.copy());
        e.SetMaxed(false);
        
       

        e.SetStackID(e.getId());
        
        //e.setVelocity(client, client.getPitch(), client.getYaw(), 0, timeHeld / 40f , 0f);
        e.setBonusAttack(1);
        e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 10, true));
        e.setRotation(0, 90);

        /**
         *  pitch = asin(-d.Y);
         *  yaw = atan2(d.X, d.Z)
         */
        
        
        

    
        e.setRSpeed(80f);

        

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
                

                //e.setRotation(client.getYaw(), client.getPitch());

                float speed = 0;

                if (timeHeld > 15){
                    e.SetMaxed(true);
                    speed = 1.2f;
                }
                else{
                    e.SetMaxed(false);
                    speed = 0.8f;
                }
                Quaternion q;
                if (!random){
                    q = new Quaternion(Vec3f.POSITIVE_Y, 180 - client.getYaw(), true);
				    e.setVelocity(client, client.getPitch(), client.getYaw(), 0, speed , 0f);
				    e.setBonusAttack(timeHeld / 40f);
                    e.setQuat(q);
                    
                    Vec3f rot = q.toEulerXyzDegrees();
                    //e.setRotation(rot.getY(), 0);
                   //e.setRotation(rot.getY(), rot.getX());
                   //e.setRotation(client.getYaw(), client.getPitch());
                }
                else{
                    float yaw = Util.randgen.nextFloat() * 360;
                    e.setVelocity(client, Util.randgen.nextFloat() * -90 + 10, yaw, 0, Util.randgen.nextFloat() * 0.5f , 0.4f);
				    e.setBonusAttack(timeHeld / 40f);
                    e.setQuat(new Quaternion(Vec3f.POSITIVE_Y, 180 - yaw, true));

                    e.setRotation(yaw, 90);

                }
				

                BNSCore.LOGGER.info(e.getRotationVector().toString());
				e.setRSpeed(speed*80f);

				//UUIDStackComponent stack = mycomponents.EntityUUIDs.get(world.getLevelProperties());
                //int id = stack.Push(client.getEntityName(), e.getUuid());

                int id = BNSCore.pushEntityOntoStack(world, client.getEntityName(), e.uuid);

				

				e.SetStackID(id);

                return e;
        
    }

    public void ThrowRandom(float force){
        
        float yaw = this.rand.nextFloat() * 360;
        float pitch =  this.rand.nextFloat() * -90 + 10;
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
