package net.fabricmc.LootTables;

import net.fabricmc.Config.ConfigRegistery;
import net.fabricmc.Items.ItemRegistry;
import net.fabricmc.Items.RuneStones.RuneStoneItemRegistry;
import net.fabricmc.fabric.api.loot.v2.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class LootTableRegistry {
    
    public static String ALL_ENTITIES = "minecraft:entities/";

    public static void register(){
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Let's only modify built-in loot tables and leave data pack loot tables untouched by checking the source.
            // We also check that the loot table ID is equal to the ID we want.
            if (source.isBuiltin()) {
                // Our code will go here
                String ID = id.toString();

                if (ID.contains(ALL_ENTITIES)){
                    LootPool poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder((float) ConfigRegistery.configuration.getDouble("RuneDropChance")))
                    .with(ItemEntry.builder(RuneStoneItemRegistry.WORTHY_STONE))
                    .with(ItemEntry.builder(RuneStoneItemRegistry.FLAME_STONE))
                    .with(ItemEntry.builder(RuneStoneItemRegistry.FROST_STONE))
                    .with(ItemEntry.builder(RuneStoneItemRegistry.LIGHTNING_STONE))
                    .with(ItemEntry.builder(RuneStoneItemRegistry.PINNED_STONE))
                    .build();
     
                    tableBuilder.pool(poolBuilder);

     
                   
                }

            }
        });
    }
}
