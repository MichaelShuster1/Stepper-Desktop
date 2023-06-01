package datadefinition;


import java.io.File;

public class DataFile extends DataDefinition<String> {
    private String data;

    public DataFile(String name) {
        super(name, "DataFile");
    }

    public File getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data.getAbsolutePath();
    }
}

