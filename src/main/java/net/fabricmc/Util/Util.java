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

        Quaternion q = new Quaternion(new Vec3f((float)rotationAxis.x, (float)rotationAxis.y, (float)rotationAxis.z), rotationAngle, false);
        q.normalize();

        return q;
    }

    public static Vec3d rotatePointByQuat(Vec3d orig, Quaternion rotation){

        // pray that this works
        Vec3d u = new Vec3d(rotation.getX(), rotation.getY(), rotation.getZ());

        float s = rotation.getW();

        Vec3d res = u.multiply(u.dotProduct(orig) * 2.0f);

        res = res.add( orig.multiply(s*s - u.dotProduct(u)));

        res = res.add(u.crossProduct(orig).multiply(2.0f * s));

        return res;
    }

    public static Vec3d getRandomDirectionUnitSphere(double minyaw, double maxyaw, double minpitch, double maxpitch){
        double yaw = Util.RadiansToDegree((float) Util.getRandomDouble(minyaw, maxyaw)) ;
        double pitch = Util.RadiansToDegree((float) Util.getRandomDouble(minpitch, maxpitch));
        

        return Vec3d.fromPolar((float)pitch, (float)yaw);

    }

    public static float RadiansToDegree(float rad){
        return rad * (180/MathHelper.PI);
    }

    public static Vec3d getRandomDirectionUnitSphere(){
        
        double yaw = Util.getRandomDouble(0, 2 * MathHelper.PI);
        double pitch = Util.getRandomDouble(0, 2 * MathHelper.PI);

        return Vec3d.fromPolar((float)pitch, (float)yaw);

    }

    public static double getRandomDouble(double min, double max){
        return randgen.nextDouble() * (max - min) + min;
    }

    public static double getRandomDouble(Random rand, double min, double max){
        return rand.nextDouble() * (max - min) + min;
    }

    public static float getRandomFloat(float min, float max){
        return randgen.nextFloat() * (max - min) + min;
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
