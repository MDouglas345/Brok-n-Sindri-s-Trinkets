package net.fabricmc.Util;

import java.util.Random;

import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class Util {

    public static Random randgen = new Random();

    public static Vec3d getVec3FromQuat(Quaternion q){
        /**
         *  x = 2 * (x*z + w*y)
         *  y = 2 * (y*z - w*x)
         *  z = 1 - 2 * (x*x + y*y)
         */
        float x = q.getX();
        float y = q.getY();
        float z = q.getZ();
        float w = q.getW();

        return new Vec3d(2 * (x*z + w*y), 2 * (y*z - w*x), 1 - 2 * (x*x - y*y));
    }

    public static double getRandomDouble(double min, double max){
        return randgen.nextDouble() * (max - min) + min;
    }
}
