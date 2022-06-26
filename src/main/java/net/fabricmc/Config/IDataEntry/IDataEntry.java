package net.fabricmc.Config.IDataEntry;

import java.io.FileWriter;

public interface IDataEntry {
    public void writeEntry(FileWriter writer);

    public void readEntry(String key, String value);
}
