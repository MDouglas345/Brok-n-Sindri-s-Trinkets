package net.fabricmc.Enchantments;

import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IWorldBehvaior {
    public void OnBlockThrownHit(World world, BlockPos pos, int level);

    public void OnEntityThrownHit(World world, EntityHitResult result, int level);

    public void SpawnTrailingParticles(World world, Vec3d pos, int level);

    public void SpawnBlockContactParticles(World world, Vec3d pos, int level);
}
