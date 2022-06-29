package net.fabricmc.Entity.PassiveDwarf.PassiveDwarfBrain;

import com.google.common.collect.ImmutableList;

import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
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

    public static Brain<?> create(PassiveDwarf piglin, Brain<PassiveDwarf> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        return brain;
    }

    private static void addCoreActivities(Brain<PassiveDwarf> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask()));
     }

     private static void addIdleActivities(Brain<PassiveDwarf> brain) {
        brain.setTaskList(Activity.IDLE, 0, ImmutableList.of(new LookAroundTask(45, 90), new WanderAroundTask(), new OpenDoorsTask()));
     }
  
    

     public static void updateActivities(PassiveDwarf axolotl) {
        Brain<PassiveDwarf> brain = axolotl.getBrain();
        Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse((Activity)null);
        if (activity != Activity.PLAY_DEAD) {
           brain.resetPossibleActivities(ImmutableList.of(Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE));
           if (activity == Activity.FIGHT && brain.getFirstPossibleNonCoreActivity().orElse((Activity)null) != Activity.FIGHT) {
              brain.remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
           }
        }
  
     }
}
