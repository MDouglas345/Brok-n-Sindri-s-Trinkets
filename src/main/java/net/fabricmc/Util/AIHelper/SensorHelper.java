package net.fabricmc.Util.AIHelper;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.core.jmx.Server;

import com.google.common.base.Predicate;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;


public class SensorHelper {

    public static <E extends Entity> List<E> getNearestEntityByClass(ServerWorld world, Class<E> classttype, LivingEntity mobEntity){
        return getNearestEntityByClass(world, classttype, mobEntity, (entity) ->{return true;});
    }

    public static <E extends Entity> List<E> getNearestEntityByClass(ServerWorld world, Class<E> classttype, LivingEntity mobEntity, Predicate<E> pred){
        return world.getEntitiesByClass(classttype, mobEntity.getBoundingBox().expand(32.0D, 16.0D, 32.0D), pred);
    }

    public static <E extends BlockEntity> List<E> getNearestBlockEntityByClass(ServerWorld world, Class<E> classtpe, LivingEntity mobEntity, Predicate<E> pred){
        return null;
    }

    

}


