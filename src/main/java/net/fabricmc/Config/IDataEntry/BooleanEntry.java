package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;

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
    
}
