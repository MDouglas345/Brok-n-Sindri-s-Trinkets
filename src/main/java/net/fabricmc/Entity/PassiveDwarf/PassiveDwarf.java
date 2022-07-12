package net.fabricmc.Entity.PassiveDwarf;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.PassiveDwarf.Goals.DwarfWander;
import net.fabricmc.Entity.PassiveDwarf.Goals.EscapeWaterGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.FindDwarvenForgeGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.GoToDwarvenForgeGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.PickupWantedGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.ProcessMaterialsGoal;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarfBrain.PassiveDwarfBrain;
import net.fabricmc.Items.ItemGroup.ItemGroupRegistry;
import net.fabricmc.Util.IHasOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Brain.Profile;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public  class PassiveDwarf extends PassiveEntity implements InventoryOwner{

    public boolean      foundForge = false;
    public BlockPos     lastKnownForgeLocation = null;

    public String[]      ItemOwners = {"",""};

    public SimpleInventory     inventory = new SimpleInventory(3); // 0 = WeaponToEnchant 1 = RuneStone

    protected static final ImmutableList<SensorType<? extends Sensor<? super PassiveDwarf>>> SENSOR_TYPES;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES;

    protected static final TrackedData<Boolean> WALKING = DataTracker.registerData(PassiveDwarf.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Integer> MOOD = DataTracker.registerData(PassiveDwarf.class, TrackedDataHandlerRegistry.INTEGER);

    protected String ANIMATION_WALK_ID;
    protected String ANIMATION_IDLE_ID;
    protected String ANIMATION_MOOD_HAPPY_ID;
    protected String ANIMATION_MOOD_UNHAPPY_ID;

    public int improveLevel = 1;

    public int tickSinceAte = 0;

    public static final int TICKS_TO_HUNGRY = 1000;


    static{
        SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY);
        MEMORY_MODULE_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, 
                                                MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, 
                                                MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.ADMIRING_ITEM,
                                                MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.INTERACTION_TARGET);          
    }

    protected PassiveDwarf(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        //TODO Auto-generated constructor stub
        
    }
    public int getMood(){
        return dataTracker.get(MOOD);
    }

    protected <E extends IAnimatable> PlayState walkpredicate(AnimationEvent<E> event) {
        if (getDataTracker().get(WALKING)){
            
		    event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIMATION_WALK_ID, true));
            return PlayState.CONTINUE;    
        }
        else{
            event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIMATION_IDLE_ID, true));
            return PlayState.CONTINUE;    
        }
        
		
	}

    protected <E extends IAnimatable> PlayState moodpredicate(AnimationEvent<E> event) {
        int mood = getDataTracker().get(MOOD);
        if (!getDataTracker().get(WALKING)){
            return PlayState.STOP;
        }
        switch (mood){
            case 0 : event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIMATION_MOOD_HAPPY_ID, true));
                    break;
            case 1 : event.getController().setAnimation(new AnimationBuilder().addAnimation(ANIMATION_MOOD_UNHAPPY_ID, true));
                    break;
        }

       return PlayState.CONTINUE;
        
		
	}


   

    protected void initGoals(){
        
        this.goalSelector.add(0, new FindDwarvenForgeGoal(this));
        this.goalSelector.add(0, new EscapeWaterGoal(this, 1.7f));
        this.goalSelector.add(1, new PickupWantedGoal(this, 1.3f));
        this.goalSelector.add(2, new GoToDwarvenForgeGoal(this, 1.56f));
        this.goalSelector.add(3, new ProcessMaterialsGoal(this));
        this.goalSelector.add(4, new DwarfWander(this, 1f));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 4));
        
    }

    @Override
    public void initDataTracker(){
        super.initDataTracker();
        DataTracker dt = this.getDataTracker();
        dt.startTracking(WALKING, false);
        dt.startTracking(MOOD, 0);
    }

    public Brain<PassiveDwarf> getBrain() {
        return (Brain<PassiveDwarf>) super.getBrain();
     }
  
    protected Profile<PassiveDwarf> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
     }
  
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<PassiveDwarf> brain = this.createBrainProfile().deserialize(dynamic);
        this.initBrain(brain);
        return brain;
     }
  
    public void reinitializeBrain(ServerWorld world) {
        Brain<PassiveDwarf> brain = this.getBrain();
        brain.stopAllTasks(world, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
     }

     private void initBrain(Brain<PassiveDwarf> brain) {
        PassiveDwarfBrain.init(this, brain);
     }

     public boolean isWalking(){
        return this.onGround && this.getVelocity().lengthSquared() != 0;
     }
     
     public void EjectInventory(){
        ItemStack weapon = inventory.getStack(0);
        ItemStack rune = inventory.getStack(1);
        ItemStack food = inventory.getStack(2);

        if (!weapon.getItem().equals(Items.AIR)){
            // drop the weapon
            Vec3d pos = getPos();
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), weapon);
            world.spawnEntity(entity);
        }

        
        if (!rune.getItem().equals(Items.AIR)){
            Vec3d pos = getPos();
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), rune);
            world.spawnEntity(entity);
        }

        if (!food.getItem().equals(Items.AIR)){
            Vec3d pos = getPos();
            ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), food);
            world.spawnEntity(entity);
        }

        resetInventory();
     }

     public boolean consumeFood(){
        ItemStack food = inventory.getStack(2);

        if (food.getItem().equals(Items.AIR)){return false;}

        tickSinceAte = 0;
        inventory.setStack(2, new ItemStack(Items.AIR));
        getDataTracker().set(MOOD, 0);
        return true;
     }

    public void despawn(){
        /**
         * Drop any weapon or runic stone and then discard
         */
        
        EjectInventory();
        this.discard();
        
    }

    @Override
    public void tick(){
        super.tick();

        if (world.isClient){return;}

        

        this.getDataTracker().set(WALKING, !navigation.isIdle());

        tickSinceAte++;
        if (tickSinceAte < TICKS_TO_HUNGRY){return;}

        getDataTracker().set(MOOD, consumeFood() ? 0 : 1);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
  
        nbt.put("Inventory", this.inventory.toNbtList());

        NbtList boollist = new NbtList();

        boollist.add(NbtString.of(ItemOwners[0]));
        boollist.add(NbtString.of(ItemOwners[1]));

        nbt.put("ItemOwners", boollist);
     }
  
     public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", 10));

        NbtList boollist = (NbtList) nbt.get("ItemOwners");

        if (boollist == null){
            return;
        }

        if (boollist.isEmpty()){
            return;
        }

        ItemOwners[0] = boollist.getString(0);
        ItemOwners[1] = boollist.getString(1);
     }

    @Override
    public SimpleInventory getInventory() {
        // TODO Auto-generated method stub
        return inventory;
    }

    @Override
    public PassiveEntity createChild(ServerWorld arg0, PassiveEntity arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isInventoryFull(){
       if (inventoryContainsRune() && inventoryContainsWeapon() && inventoryContainsFood()){return true;}
       return false;
    }

    @Override
    public void dropLoot(DamageSource source, boolean causedByPlayer){
        this.EjectInventory();
        super.dropLoot(source, causedByPlayer);
    }

    public boolean inventoryContainsWeapon(){
            
            ItemStack stack = inventory.getStack(0);
            ItemGroup group = stack.getItem().getGroup();
            if (group == null){return false;}
            if (group.equals(ItemGroup.COMBAT) || group.equals(ItemGroup.TOOLS)){return true;}
            return false;
    }

    public boolean inventoryContainsRune(){

        ItemStack stack = inventory.getStack(1);
        ItemGroup group = stack.getItem().getGroup();
        if (group == null){return false;}
        if (group.equals(ItemGroupRegistry.RUNE_STONE)){return true;}
        return false;
       
    }

    public boolean inventoryContainsFood(){
        ItemStack stack = inventory.getStack(2);
        Item item = stack.getItem();
        if (stack == null || item == null){return false;}
        if (item.equals(Items.COOKED_BEEF)){return true;}
        return false;
    }

    public boolean isCloseToDF(){
        if (lastKnownForgeLocation == null){
            return false;
        }
        return lastKnownForgeLocation.isWithinDistance(getBlockPos(), 2);
    }

    public boolean lootWanted(ItemEntity item){
        if (isInventoryFull()|| item == null){
            return false;
        }

        ItemStack stack = item.getStack();
        Item itEm = stack.getItem();
        ItemGroup group = itEm.getGroup();

        if (item.getThrower() == null || group == null){
            return false;
        }

        if (inventoryContainsWeapon() && (group.equals(ItemGroup.COMBAT) || group.equals(ItemGroup.TOOLS)) ){
            return false;
        }

        if (inventoryContainsRune() && group.equals(ItemGroupRegistry.RUNE_STONE)){
            return false;
        }

        if (inventoryContainsFood() && itEm.equals(Items.COOKED_BEEF)){
            return false;
        }
       

        if (group.equals(ItemGroup.COMBAT) || group.equals(ItemGroup.TOOLS)){
            inventory.setStack(0, stack);
            ItemOwners[0] =((ServerWorld)world).getServer().getUserCache().getByUuid(item.getThrower()).get().getName();
            this.sendPickup(item, 1);
            item.discard();
            return true;
        }

        if (stack.getItem().getGroup().equals(ItemGroupRegistry.RUNE_STONE)){
            inventory.setStack(1, stack);
            ItemOwners[1] = ((ServerWorld)world).getServer().getUserCache().getByUuid(item.getThrower()).get().getName();
            this.sendPickup(item, 1);
            item.discard();
            return true;
        }    

        if (itEm.equals(Items.COOKED_BEEF)){
            inventory.setStack(2, stack);
            if (tickSinceAte >= TICKS_TO_HUNGRY){consumeFood();}
            this.sendPickup(item, 1);
            item.discard();
            return true;
        }   

        return false;
    }

    public void resetInventory(){
        inventory.setStack(0, new ItemStack(Items.AIR));
        inventory.setStack(1, new ItemStack(Items.AIR));
        inventory.setStack(2, new ItemStack(Items.AIR));

        ItemOwners = new String[]{"", ""};
        
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (world.isClient){return ActionResult.FAIL;}

        for (int i = 0; i < 2; i++){
            ItemStack slot0 = inventory.getStack(i);
            
            if (!ItemOwners[i].equals(player.getEntityName())){continue;}
        
            if (player.getInventory().insertStack(slot0)){
                inventory.removeStack(i);
            }
        }

        return ActionResult.SUCCESS;
     }

    
  
}
