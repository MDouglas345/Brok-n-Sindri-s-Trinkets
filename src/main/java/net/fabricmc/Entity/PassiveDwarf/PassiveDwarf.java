package net.fabricmc.Entity.PassiveDwarf;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;

import net.fabricmc.Entity.PassiveDwarf.Goals.EscapeWaterGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.FindDwarvenForgeGoal;
import net.fabricmc.Entity.PassiveDwarf.Goals.GoToDwarvenForgeGoal;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarfBrain.PassiveDwarfBrain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Brain.Profile;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public  class PassiveDwarf extends PassiveEntity implements InventoryOwner{

    public boolean      foundForge = false;
    public BlockPos     lastKnownForgeLocation = null;


    SimpleInventory     inventory = new SimpleInventory(2); // 0 = WeaponToEnchant 1 = RuneStone

    protected static final ImmutableList<SensorType<? extends Sensor<? super PassiveDwarf>>> SENSOR_TYPES;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULE_TYPES;


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

    protected void initGoals(){
        /* 
        this.goalSelector.add(0, new FindDwarvenForgeGoal(this));
        this.goalSelector.add(0, new EscapeWaterGoal(this, 1.7f));
        
        this.goalSelector.add(2, new GoToDwarvenForgeGoal(this, 1.56f));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1f));
        */
    }

    public Brain<PassiveDwarf> getBrain() {
        return (Brain<PassiveDwarf>) super.getBrain();
     }
  
    protected Profile<PassiveDwarf> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULE_TYPES, SENSOR_TYPES);
     }
  
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return (Brain<PassiveDwarf>) PassiveDwarfBrain.create(this, this.createBrainProfile().deserialize(dynamic));
     }
  
    public void reinitializeBrain(ServerWorld world) {
        Brain<PassiveDwarf> brain = this.getBrain();
        brain.stopAllTasks(world, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
     }

     private void initBrain(Brain<PassiveDwarf> brain) {
        
     }

     protected void mobTick() {
        this.world.getProfiler().push("PassiveDwarfBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("DwarfActivityUpdate");
        PassiveDwarfBrain.updateActivities(this);
        this.world.getProfiler().pop();
        if (!this.isAiDisabled()) {
           
        }
  
     }


    public void despawn(){
        /**
         * Drop any weapon or runic stone and then discard
         */
        
        this.discard();
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
  
        nbt.put("Inventory", this.inventory.toNbtList());
     }
  
     public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.inventory.readNbtList(nbt.getList("Inventory", 10));
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

    
  
}
