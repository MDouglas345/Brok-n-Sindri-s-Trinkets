package net.fabricmc.Util;

import java.util.Random;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Enchantments.IWorldBehvaior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentHelper.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.PlayerEntity;


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

    public static boolean ContainsSpecialThrownEnchantment(ItemStack stack){
        EnchantmentData data = getSpecialThrownEnchantment(stack);

        return data != null;
    }

    public static boolean isEnchantmentSpecialThrown(Enchantment enchantment){
        return enchantment instanceof IWorldBehvaior;
    }

    public static boolean isItemThrowValid(Item item){
        if (!(item instanceof SwordItem) && !(item instanceof MiningToolItem)){return false;}
        return true;
    }

    public static ItemStack setEnchantment(ItemStack stack, Enchantment enchantment, int level){

        Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
        Integer orig = map.get(enchantment);

        if (orig != null && orig >= level){
            return stack;
        }

        if (isEnchantmentSpecialThrown(enchantment) && ContainsSpecialThrownEnchantment(stack)){
            return stack;
        }

        map.put(enchantment, level);

        EnchantmentHelper.set(map, stack);
        
        return stack;
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
        
        double yaw = Util.getRandomDouble(0, 360);
        double pitch = Util.getRandomDouble(0, 360);

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


    public static void ApplyOnTargetDamageEnchantments(LivingEntity user, Entity target, ItemStack stack){
         Consumer consumer = (enchantments, level) -> enchantments.onTargetDamaged(user, target, level);
        if (stack.isEmpty()) {
            return;
        }
        NbtList nbtList = stack.getEnchantments();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(enchantment -> consumer.accept((Enchantment)enchantment, EnchantmentHelper.getLevelFromNbt(nbtCompound)));
        }
    }

    public static EnchantmentData getSpecialThrownEnchantment(ItemStack stack){
        int level = EnchantmentHelper.getLevel(BNSCore.FrostWeapon, stack);
        if ( level > 0){
            return new EnchantmentData(BNSCore.FrostWeapon, level);
        }
        
        level = EnchantmentHelper.getLevel(BNSCore.FrostTool, stack);
        if ( level > 0){
            return new EnchantmentData(BNSCore.FrostTool, level);
        }


        level = EnchantmentHelper.getLevel(BNSCore.FlameWeapon, stack);
        if ( level > 0){
            return new EnchantmentData(BNSCore.FlameWeapon, level);
        }
        
        level = EnchantmentHelper.getLevel(BNSCore.FlameTool, stack);
        if ( level > 0){
            return new EnchantmentData(BNSCore.FlameTool, level);
        }

        level = EnchantmentHelper.getLevel(BNSCore.LightningWeapon, stack);
        if ( level > 0){
            return new EnchantmentData(BNSCore.LightningWeapon, level);
        }
        
        level = EnchantmentHelper.getLevel(BNSCore.LightningTool, stack);
        if ( level > 0){
            return new EnchantmentData(BNSCore.LightningTool, level);
        }

        return null;
    }

    public static List<PlayerEntity> getNearbyPlayers(World world, Box box){
        List<PlayerEntity> res = new ArrayList<>();
        
        for (PlayerEntity player : world.getPlayers()){
            if (box.contains(player.getPos())){
                res.add(player);
            }
        }

        return res;
    } 

    public static void createVisualLightningStrike(BlockPos target, ServerWorld world){
        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);

        if (lightningEntity != null) {
            lightningEntity.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
            lightningEntity.setCosmetic(true);
            world.spawnEntity(lightningEntity);
        }
    }

    public static void createLightningStrike(BlockPos target, ServerWorld world, Entity source, int radius){
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);

            createVisualLightningStrike(target, world);

            List<Entity> list = world.getOtherEntities(null, new Box(target.getX() - radius, target.getY() - radius, target.getZ() - radius, target.getX() + radius, target.getY() + radius, target.getZ() + radius), Entity::isAlive);
            for (Entity entity2 : list) {
                if (source != null && source.equals(entity2)){
                    continue;
                }
                createVisualLightningStrike(entity2.getBlockPos(), world);
                entity2.onStruckByLightning((ServerWorld)world, lightningEntity);
            }

           
        
    }

    public static double angleBetween(Vec3d A, Vec3d B){
        Vec3d cross = A.crossProduct(B);
        double mag = cross.length();
        double dot = A.dotProduct(B);

        return MathHelper.atan2(mag, dot);
    }

    public static Quaternion rotateItemForBlock(ItemStack item, Quaternion original, BlockHitResult blockHitResult){
        
        Quaternion res = blockHitResult.getSide().getRotationQuaternion();
        res.hamiltonProduct(original);
        if (item.getItem() instanceof SwordItem){
            Vec3f angles = original.toEulerXyz();
           // original.conjugate();
            // hamiltonProduct here so that sword rotates along the Y 
            //res.hamiltonProduct(Quaternion.fromEulerXyz((float)Util.getRandomDouble(8*3.14159 / 9, 41*3.14159/36), 0, 0));
            
            res.hamiltonProduct(Quaternion.fromEulerXyz((float)Util.getRandomDouble(8*3.14159 / 9, 41*3.14159/36), 0, 0));
            //res.hamiltonProduct(Quaternion.fromEulerXyz(0, 0, angles.getX()));
        }
        else{
            //res.hamiltonProduct(Quaternion.fromEulerXyz((float)Util.getRandomDouble(13*3.14159 / 12, 7*3.14159/6), 0, 0));
            //original.conjugate();
           
            res.hamiltonProduct(Quaternion.fromEulerXyz((float)Util.getRandomDouble(13*3.14159 / 12, 7*3.14159/6), 0, 0));
        }

        return res;
    }


    @FunctionalInterface
    static interface Consumer {
        public void accept(Enchantment var1, int level);
    }
}
