package DataDefinitions;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DataRelation extends DataDefinition<Relation>
{
    private Relation data;


    public DataRelation(String name)
    {
        super(name, "DataRelation");
    }


    @Override
    public String toString() {
        return  data.toString();
    }

    @Override
    public void setData(Relation data) {
        this.data= data;
    }

    @Override
    public Relation getData() {
        return data;
    }
}

