package net.fabricmc.Util;

import java.util.Random;


import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

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

    public static Quaternion QuaternionFromDirection(Vec3d v){
        /**
         * angle = atan2( vector.x, vector.z ) // Note: I expected atan2(z,x) but OP reported success with atan2(x,z) instead! Switch around if you see 90° off.
                qx = 0
                qy = 1 * sin( angle/2 )
                qz = 0
                qw = cos( angle/2 )

         */

         double angle = MathHelper.atan2(v.z, v.x);

         return new Quaternion(0f, 1 * MathHelper.sin((float)angle/2f), 0f, MathHelper.cos((float)angle/2f));
    }

    public static Quaternion getDirectionalRotation(Vec3d from, Vec3d to){

        float rotationAngle = (float)Math.acos(from.dotProduct(to));

        Vec3d rotationAxis = from.crossProduct(to).normalize();

        return new Quaternion(new Vec3f((float)rotationAxis.x, (float)rotationAxis.y, (float)rotationAxis.z), rotationAngle, false);
    }

    public static double getRandomDouble(double min, double max){
        return randgen.nextDouble() * (max - min) + min;
    }

    public static BlockPos getAdjacentBlock(BlockPos pos, Direction dir){
        

        switch (dir){
            case DOWN :
                return pos.down();
            case UP :
                return pos.up();
            case NORTH :
                return pos.north();
            case SOUTH :
                return pos.south();
            case WEST :
                return pos.west();
            case EAST :
                return pos.east();
            default:
                break;
        }

        return null;
    }
}
