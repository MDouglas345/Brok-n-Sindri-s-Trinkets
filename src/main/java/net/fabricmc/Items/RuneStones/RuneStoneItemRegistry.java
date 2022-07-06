package net.fabricmc.Items.RuneStones;

import net.fabricmc.Items.RuneStones.FlameStone.FlameStoneItem;
import net.fabricmc.Items.RuneStones.FrostStone.FrostStoneItem;
import net.fabricmc.Items.RuneStones.LightningStone.LightningStoneItem;
import net.minecraft.item.Item;

public class RuneStoneItemRegistry {
    public static Item LIGHTNING_STONE = new LightningStoneItem();
    public static Item FLAME_STONE = new FlameStoneItem();
    public static Item FROST_STONE = new FrostStoneItem();
}
