package datadefinition;


import java.util.List;

public class DataNumber extends DataDefinition<Integer> {
    private Integer data;

    public DataNumber(String name) {
        super(name, "DataNumber");
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void setData(Integer data) {
        this.data = data;
    }

    @Override
    public Integer getData() {
        return data;
    }

    @Override
    public List<String> getSecondaryData() {
        return null;
    }
}
