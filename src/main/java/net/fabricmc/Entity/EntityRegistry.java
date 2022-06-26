package net.fabricmc.Entity;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Entity.Brok.BrokEntity;
import net.fabricmc.Entity.Brok.BrokRenderer;
import net.fabricmc.Entity.PassiveDwarf.PassiveDwarf;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegistry {

    public static final EntityType<BrokEntity> BROK = Registry.register(
                                                                Registry.ENTITY_TYPE, 
                                                                new Identifier(BNSCore.ModID, "brok"),                 
                                                                FabricEntityTypeBuilder.<BrokEntity>create(SpawnGroup.CREATURE, BrokEntity::new)
                                                                .dimensions(EntityDimensions.fixed(0.75f, 1.4f)).build()); 
    
    public static void registerClient(){
        EntityRendererRegistry.register(BROK, BrokRenderer::new);   
    }

    public static void registerAttributes(){
        FabricDefaultAttributeRegistry.register(BROK, BrokEntity.setDefaultAttributes());
    }
}
