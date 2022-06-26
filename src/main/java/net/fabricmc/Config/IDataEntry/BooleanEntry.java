package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;

import net.fabricmc.Config.ConfigRegistery;

public class BooleanEntry implements IDataEntry {
    public boolean value;

    public BooleanEntry(boolean bool){value = bool;}

    public BooleanEntry(String bool){
        value = bool.equals("True") ? true : false;
    }

    @Override
    public void writeEntry(FileWriter writer) {
        try{
            writer.write(value ? "True" : "False");
        }
        catch(Exception e){
            
        }
        
    }

    @Override
    public void readEntry(String key, String v) {
        IDataEntry entry = null;

        if (v.equals("True") | v.equals("False")){
            entry = new BooleanEntry(v);
            ConfigRegistery.configuration.data.put(key, entry);
            
        }
        
        
    }
    
}
