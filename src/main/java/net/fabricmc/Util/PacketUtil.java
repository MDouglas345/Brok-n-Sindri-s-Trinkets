package net.fabricmc.Util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class PacketUtil {

    public static void WriteVec3d(PacketByteBuf buf, Vec3d vec){
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }

    public static void WriteVec3f(PacketByteBuf buf, Vec3f vec){
        buf.writeFloat(vec.getX());
        buf.writeFloat(vec.getY());
        buf.writeFloat(vec.getZ());
    }

    public static void WriteQuaternion(PacketByteBuf buf, Quaternion quat){
        buf.writeFloat(quat.getX());
        buf.writeFloat(quat.getY());
        buf.writeFloat(quat.getZ());
        buf.writeFloat(quat.getW());
    }

    public static Vec3d ReadVec3d(PacketByteBuf buf){
        return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()); 
    }

    public static Vec3f ReadVec3f(PacketByteBuf buf){
        return new Vec3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static Quaternion ReadQuaternion(PacketByteBuf buf){
        return new Quaternion(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }


    
}
