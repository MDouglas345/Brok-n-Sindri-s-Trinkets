package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.ConfigRegistery;

public class IntEntry implements IDataEntry{
    public int value;

    public IntEntry(int i){
        value = i;
    }

    @Override
    public void writeEntry(FileWriter writer) {
        try{
            writer.write(Integer.toString(value));
        }
        catch(Exception e){
            BNSCore.LOGGER.info("ERROR : Well. We arent supposed to be here. but Here we are, int -> string error");
        }
        
    }

    @Override
    public void readEntry(String key, String value) {
       
        
    }
    
}
