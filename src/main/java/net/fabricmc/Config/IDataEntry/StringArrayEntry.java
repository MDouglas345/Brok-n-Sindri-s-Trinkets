package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;

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
                if (i > data.length-2){
                    writer.write(",");
                }
            }
        }
        catch(Exception e){

        }
        
    }
    
}
