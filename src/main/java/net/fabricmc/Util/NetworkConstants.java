package net.fabricmc.Util;

import net.minecraft.util.Identifier;

public class NetworkConstants {
    public static Identifier EstablishThrownItem = new Identifier("bns:establish_thrown_item");
    public static Identifier ShouldMovePacket = new Identifier("bns:should_move_packet");
    public static Identifier SpawnBranchLightning = new Identifier(("bns:spawnbranchlightning"));
    public static Identifier SpawnFrostContact = new Identifier(("bns:spawnfrostcontact"));
    public static Identifier SpawnFlameContact = new Identifier(("bns:spawnflamecontact"));

    public static Identifier SpawnFlameAffectEntities = new Identifier("bns:spawnflameaffectentities");
    public static Identifier SpawnFrostAffectEntities = new Identifier("bns:spawnfrostaffectentities");
    public static Identifier SpawnLightningAffectEntities = new Identifier("bns:spawnlightningaffectentities");
    
}
