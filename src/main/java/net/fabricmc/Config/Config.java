package net.fabricmc.Config;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import net.fabricmc.BNSCore.BNSCore;
import net.fabricmc.Config.IDataEntry.BooleanEntry;
import net.fabricmc.Config.IDataEntry.DoubleEntry;
import net.fabricmc.Config.IDataEntry.IDataEntry;
import net.fabricmc.Config.IDataEntry.IntEntry;
import net.fabricmc.Config.IDataEntry.StringArrayEntry;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

   public HashMap<String, IDataEntry> data;

    static String filepath =  FabricLoader.getInstance()
                                            .getConfigDir()
                                                    .resolve("bns.properties").toString();

    public Config(){
        data = new HashMap<String,IDataEntry>();
        /**
         * Init data structure here. Then read to replace values.
         */
    }

    public void Init(){
        generateDefaults();
        readFile();
    }

    public void readFile(){
        try{
            File configfile = new File(filepath);
            Scanner filereader = new Scanner(configfile);

            while (filereader.hasNextLine()){
                String line = filereader.nextLine();
                String[] entryPairs = getPairs(line);
                enterData(entryPairs[0], entryPairs[1]);
            }

           writeFile();
        }
        catch(Exception e){
            BNSCore.LOGGER.info("Config file not found. Creating one.");
            writeFile();
        }

    }

    public void enterData(String key, String v){
        
        ConfigReader.readBoolean(key, v);
        ConfigReader.readInt(key, v);
        ConfigReader.readStringArray(key, v);
        ConfigReader.readDouble(key, v);

        /**
        IDataEntry entry = null;

        if (v.equals("True") | v.equals("False")){
            entry = new BooleanEntry(v);
        }
        else if (v.contains(",")){
            

             String[] entries = v.split(",");

             if (entries[0].equals("String")){
                entry = new StringArrayEntry(entries);
             }
             else if (entries[0].equals("Int")){
                // This is an int array
             }
        }

        if (entry == null){
            BNSCore.LOGGER.info("ERROR : entering " + key + " with value " + v);
        }

        //not sure if this updates the record if it exists, or creates a new one even though it exists.
        data.put(key, entry);

        */

        
    }

    public String[] getPairs(String line){
        return line.split(":");
    }

    public void generateDefaults(){
        enterData("ThrowEnchantment", "False");
        enterData("NotAllowedThrow", "String,null");
        enterData("DFDistance", "400");
        enterData("ThrowSpeed","0.8");
        enterData("ThrowSpeedMax", "1.2");
        enterData("AttackMultiplier", "0.6");
        enterData("MaxAttackMultiplier", "1.2");
        enterData("ReturnSpeed", "0.8");
        enterData("ItemDamage", "1");
        enterData("AntiGrief", "False");
        enterData("RuneDropChance", "0.01");

    }

    public void writeFile(){
        try{
            File newconfig = new File(filepath);
            if (newconfig.createNewFile()){
                BNSCore.LOGGER.info("Successfully created config!");
            }
            else{
                BNSCore.LOGGER.info(("Failed to create config!"));
            }

            
            FileWriter filewriter = new FileWriter(filepath);

            for(Entry<String, IDataEntry> entry :data.entrySet() ){

                filewriter.write(entry.getKey() + ":" );
                entry.getValue().writeEntry(filewriter);
                filewriter.write("\n");
            }

            filewriter.close();
        }
        catch(Exception e){
            BNSCore.LOGGER.info("ERROR WRITING CONFIG!!");
        }

    }

    public Boolean getBoolean(String key){
        try{
            BooleanEntry d = (BooleanEntry) data.get(key);
            return d.value;
        }
        catch(Exception e){
            return null;
        }
    }

    public void updateBoolean(String key, boolean value){
        if (!data.containsKey(key)){
            return;
        }

        data.put(key, new BooleanEntry(value));

    }


    public int getInt(String key){
        try{
            IntEntry d = (IntEntry) data.get(key);
            return d.value;
        }
        catch(Exception e){
            return -1;
        }
    }

    public double getDouble(String key){
        try{
            DoubleEntry d = (DoubleEntry) data.get(key);
            return d.value;
        }
        catch(Exception e){
            return -1;
        }
    }

    public Boolean  isInStringArray(String key, String value) {
        if (!data.containsKey(key)){
            return null;
        }

        StringArrayEntry array = (StringArrayEntry) data.get(key);

        for (String record : array.data){
            if (record.equals(value)){
                return true;
            }
        }

        return false;
    }
}
