package net.fabricmc.Entity.ScheduleRegistry;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ScheduleRegistry {
    public static Schedule PASSIVE_DWARF;

    public static void register(){
        PASSIVE_DWARF = register("passive_dwarf").withActivity(0,Activity.IDLE).withActivity(2000, Activity.WORK).withActivity(9000, Activity.PLAY).withActivity(11000, Activity.IDLE).withActivity(12000, Activity.REST).build();
    }

    protected static ScheduleBuilder register(String id) {
        Schedule schedule = (Schedule)Registry.register(Registry.SCHEDULE, new Identifier(BNSCore.ModID,id), new Schedule());
        return new ScheduleBuilder(schedule);
     }
}
