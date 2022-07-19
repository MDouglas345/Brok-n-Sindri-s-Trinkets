package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;
import java.io.IOException;

public class DoubleEntry implements IDataEntry{
    public double value;

    public DoubleEntry(double v){
        value = v;
    }
    @Override
    public void writeEntry(FileWriter writer) {
        // TODO Auto-generated method stub
        try {
            writer.write(String.valueOf(value));
        } catch (IOException e) {
            // TODO Auto-generated catch block
           return;
        }
        
    }

    @Override
    public void readEntry(String key, String value) {
        // TODO Auto-generated method stub
        
    }
    
}
