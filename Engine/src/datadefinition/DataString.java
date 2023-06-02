package datadefinition;

import java.util.List;

public class DataString extends DataDefinition<String> {
    private String data;

    public DataString(String name) {
        super(name, "DataString");
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
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
