package datadefinition;


import java.util.List;

public class DataRelation extends DataDefinition<Relation> {
    private Relation data;


    public DataRelation(String name) {
        super(name, "DataRelation");
        this.data = null;
    }


    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void setData(Relation data) {
        this.data = data;
    }

    @Override
    public Relation getData() {
        return data;
    }

    @Override
    public List<String> getSecondaryData() {
        return null;
    }
}

