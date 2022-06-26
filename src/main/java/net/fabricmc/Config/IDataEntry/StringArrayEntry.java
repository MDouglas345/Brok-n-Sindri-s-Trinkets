package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import net.fabricmc.Config.ConfigRegistery;

public class StringArrayEntry implements IDataEntry {
    public String[] data;
    public StringArrayEntry(String[] array){
        data = array;
    }
    @Override
    public void writeEntry(FileWriter writer) {
        try{
            writer.write("String,");    
            for (int i = 1; i < data.length; i++){
                writer.write(data[i]);
                if (i < data.length-1){
                    writer.write(",");
                }
            }
        }
        catch(Exception e){

        }
        
    }
    @Override
    public void readEntry(String key, String value) {
        // TODO Auto-generated method stub
        IDataEntry entry = null;
        if (!value.contains(",")){return;}

        String[] entries = value.split(",");


           

        if (!entries[0].equals("String")){
           return;
        }

        entry = new StringArrayEntry(entries);
        ConfigRegistery.configuration.data.put(key, entry);
           
    
    }
    
}
