package net.fabricmc.Effects;

import net.fabricmc.BNSCore.BNSCore;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.math.Vec3d;

public class ParalysisEffect extends StatusEffect  {

    public ParalysisEffect() {
        super(
        StatusEffectCategory.HARMFUL, // whether beneficial or harmful for entities
         0xA9A9A9); // color in RGB
    }

    // This method is called every tick to check whether it should apply the status effect or not
  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    // In our case, we just make it return true so that it applies the status effect every tick.
    return true;
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.slowMovement(BNSCore.GENERIC_ITEM_BLOCK.getDefaultState(), new Vec3d(0.001,0.001,0.001));
    // need to find out how to stop entity from being able to attack
  }
    
}
