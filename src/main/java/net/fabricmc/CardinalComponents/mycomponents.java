package net.fabricmc.CardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class mycomponents implements ScoreboardComponentInitializer, LevelComponentInitializer, WorldComponentInitializer   {

    public static final ComponentKey<BlockPosStackComponent> BlockEntityPositions = 
        ComponentRegistry.getOrCreate(new Identifier("bns", "blockentitypositions"), BlockPosStackComponent.class);

    public static final ComponentKey<UUIDStackComponent> EntityUUIDs = 
        ComponentRegistry.getOrCreate(new Identifier("bns", "entityuuids"), UUIDStackComponent.class);
    
    public static final ComponentKey<PlayerBlockComponent> PlayerBlocks =
        ComponentRegistry.getOrCreate(new Identifier("bns", "playerblocks"), PlayerBlockComponent.class);    

    public static final ComponentKey<GlobalPosRecordComponent> DwarvenForges =
        ComponentRegistry.getOrCreate(new Identifier("bns", "dwarvenforges"), GlobalPosRecordComponent.class);    

    public static final ComponentKey<PinnedEntityComponent> PinnedEntities =
        ComponentRegistry.getOrCreate(new Identifier("bns", "pinnedentities"), PinnedEntityComponent.class);    

    public static final ComponentKey<WeaponStackComponent> WeaponStacks =
        ComponentRegistry.getOrCreate(new Identifier("bns", "weaponstacks"), WeaponStackComponent.class);    

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(BlockEntityPositions, BlockPosStackComponent::new);
        
    }

    @Override
    public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
        // TODO Auto-generated method stub
        //registry.register(BlockEntityPositions, BlockPosStackComponent::new);
        //registry.register(EntityUUIDs, UUIDStackComponent::new);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        //registry.register(BlockEntityPositions, BlockPosStackComponent::new);
        //registry.register(EntityUUIDs, UUIDStackComponent::new);
        //registry.register(PlayerBlocks, PlayerBlockComponent::new);
        registry.register(DwarvenForges, GlobalPosRecordComponent::new);
        registry.register(PinnedEntities, PinnedEntityComponent::new);
        registry.register(WeaponStacks, WeaponStackComponent::new);
        
    }
}
