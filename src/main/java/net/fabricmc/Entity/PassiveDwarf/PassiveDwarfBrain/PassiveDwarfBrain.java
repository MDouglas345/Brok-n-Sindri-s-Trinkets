package net.fabricmc.Entity.PassiveDwarf.PassiveDwarfBrain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.Entity.ScheduleRegistry.ScheduleRegistry;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.RemoveOffHandItemTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;

public class PassiveDwarfBrain {

    public static Brain<PassiveDwarf> create(PassiveDwarf passivedwarf, Brain<PassiveDwarf> brain) {
        return brain;
    }

    public static void init(PassiveDwarf passivedwarf, Brain<PassiveDwarf> brain){
      brain.setSchedule(ScheduleRegistry.PASSIVE_DWARF);
         
      addIdleActivities(brain);
      addWorkActivities(brain);
      addPlayActivities(brain);
      addRestActivities(brain);

      brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
      brain.setDefaultActivity(Activity.IDLE);
      brain.doExclusively(Activity.IDLE);
      brain.refreshActivities(passivedwarf.world.getTimeOfDay(), passivedwarf.world.getTime());
    }

    

     private static void addIdleActivities(Brain<PassiveDwarf> brain) {
        brain.setTaskList(Activity.IDLE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask()));
     }

     private static void addWorkActivities(Brain<PassiveDwarf> brain) {
      brain.setTaskList(Activity.WORK, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask()));
   }

   private static void addPlayActivities(Brain<PassiveDwarf> brain) {
      brain.setTaskList(Activity.PLAY, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask()));
   }

   private static void addRestActivities(Brain<PassiveDwarf> brain) {
      brain.setTaskList(Activity.REST, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask()));
   }
  
    

     public static void updateActivities(PassiveDwarf axolotl) {
        Brain<PassiveDwarf> brain = axolotl.getBrain();
        Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse((Activity)null);
        if (activity != Activity.PLAY_DEAD) {
           brain.resetPossibleActivities(ImmutableList.of(Activity.CORE, Activity.IDLE));
           if (activity == Activity.FIGHT && brain.getFirstPossibleNonCoreActivity().orElse((Activity)null) != Activity.FIGHT) {
              brain.remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
           }
        }
  
     }
}
