package net.fabricmc.Config;

import net.fabricmc.Config.IDataEntry.BooleanEntry;
import net.fabricmc.Config.IDataEntry.IDataEntry;
import net.fabricmc.Config.IDataEntry.IntEntry;
import net.fabricmc.Config.IDataEntry.StringArrayEntry;

public class ConfigReader {
    public static void readStringArray(String key, String value){
        IDataEntry entry = null;
        if (!value.contains(",")){return;}

        String[] entries = value.split(",");


           

        if (!entries[0].equals("String")){
           return;
        }

        entry = new StringArrayEntry(entries);
        ConfigRegistery.configuration.data.put(key, entry);
    }

    public static void readBoolean(String key, String v){
        IDataEntry entry = null;

        if (v.equals("True") | v.equals("False")){
            entry = new BooleanEntry(v);
            ConfigRegistery.configuration.data.put(key, entry);
            
        }
    }

    public static void readInt(String key, String value){
        try{
            int i = Integer.parseInt(value);
            IDataEntry entry = new IntEntry(i);
            ConfigRegistery.configuration.data.put(key, entry);
        }
        catch(Exception e){
            return;
        }
    }
}
