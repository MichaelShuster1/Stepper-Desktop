package DataDefinitions;

import java.util.HashMap;
import java.util.Map;


public class DataMapping extends DataDefinition<Map<String,DataDefinition>>{

    private Map<String,DataDefinition> data;


    public DataMapping(String name)
    {
        super(name, "DataMapping");
        this.data = new HashMap<>();
    }

    public void setData(DataDefinition data1, DataDefinition data2)
    {
        data.put("car", data1);
        data.put("cdr", data2);
    }

    @Override
    public void setData(Map<String,DataDefinition> data) {
        this.data = data;
    }

    @Override
    public Map<String,DataDefinition> getData() {
        return data;
    }


    @Override
    public String toString() {
        return "car: " + data.get("car").toString() + "\n" + "cdr: " + data.get("cdr").toString();
    }
}
