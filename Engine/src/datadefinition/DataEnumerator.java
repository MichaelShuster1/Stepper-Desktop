package datadefinition;

import exception.EnumerationDataException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataEnumerator extends DataDefinition<String>{
    private Set<String> values;
    private String data;


    public DataEnumerator(String name, Set<String> values) {
        super(name, "DataEnumerator");
        this.values = values;
    }

    public String getAllowedValues() {
        return values.toString();
    }

    public List<String> getAllowedValuesList() {
        List<String> allowed = new ArrayList<>();
        allowed.addAll(values);
        return allowed;
    }


    @Override
    public void setData(String data) {
        if(values.contains(data) || data == null)
            this.data=data;
        else
            throw new EnumerationDataException(values.toString());
    }

    @Override
    public String toString()
    {
        return getData();
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public List<String> getSecondaryData() {
        return getAllowedValuesList();
    }
}
