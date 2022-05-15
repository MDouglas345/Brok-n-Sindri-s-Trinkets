package net.fabricmc.CardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.util.Identifier;

public class mycomponents implements ScoreboardComponentInitializer, LevelComponentInitializer{

    public static final ComponentKey<BlockPosStackComponent> BlockEntityPositions = 
        ComponentRegistry.getOrCreate(new Identifier("bns", "blockentitypositions"), BlockPosStackComponent.class);

    public static final ComponentKey<UUIDStackComponent> EntityUUIDs = 
        ComponentRegistry.getOrCreate(new Identifier("bns", "entityuuids"), UUIDStackComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(BlockEntityPositions, BlockPosStackComponent::new);
        
    }

    @Override
    public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
        // TODO Auto-generated method stub
        registry.register(BlockEntityPositions, BlockPosStackComponent::new);
        registry.register(EntityUUIDs, UUIDStackComponent::new);
    }
}
