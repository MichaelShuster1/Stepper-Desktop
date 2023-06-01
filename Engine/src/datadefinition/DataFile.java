package datadefinition;


import java.io.File;
import java.util.List;

public class DataFile extends DataDefinition<String> {
    private String data;

    public DataFile(String name) {
        super(name, "DataFile");
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public List<String> getSecondaryData() {
        return null;
    }
}

