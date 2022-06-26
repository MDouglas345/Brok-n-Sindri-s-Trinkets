package net.fabricmc.Config;

public class ConfigRegistery {
    public static Config configuration;

    public static void initConfig(){
        configuration = new Config();
        configuration.Init();
    }

}
