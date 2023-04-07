package DataDefinitions;

import java.util.HashMap;
import java.util.Map;

/*
public class DataMapping extends DataDefinition{

    private Map<String,DataDefinition> data;

    public DataMapping(String name, String typeStream, boolean mandatory, DataDefinition data1 , DataDefinition data2)
    {
        super(name,"Mapping", typeStream, mandatory , false);
        data = new HashMap<>();
        data.put("car", data1);
        data.put("cdr", data2);
    }

    public Map<String, DataDefinition> getData() {
        return data;
    }

    public void setData(Map<String, DataDefinition> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "car: " + data.get("car").toString() + "\n" + "cdr: " + data.get("cdr").toString();
    }
}
*/