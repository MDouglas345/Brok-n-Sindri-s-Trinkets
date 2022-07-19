package net.fabricmc.Compat;

import net.fabricmc.loader.api.FabricLoader;

public class Compatibility {
    public static boolean InventorioLoaded = false;

    public static void InitCompatibility(){
        InventorioLoaded = FabricLoader.getInstance().isModLoaded("inventorio");
        FabricLoader.getInstance().getModContainer("inventorio");
    }
}
