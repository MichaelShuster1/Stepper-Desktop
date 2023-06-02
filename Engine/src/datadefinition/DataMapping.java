package datadefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataMapping<T> extends DataDefinition<Map<String, T>> {

    private Map<String, T> data;


    public DataMapping(String name) {
        super(name, "DataMapping");
    }

    public void setData(T data1, T data2) {
        data.put("car", data1);
        data.put("cdr", data2);
    }

    @Override
    public void setData(Map<String, T> data) {
        this.data = data;
    }

    @Override
    public Map<String, T> getData() {
        return data;
    }


    @Override
    public String toString() {
        return "car: " + data.get("car").toString() + "\n" + "cdr: " + data.get("cdr").toString();
    }

    @Override
    public List<String> getSecondaryData() {
        return null;
    }
}
