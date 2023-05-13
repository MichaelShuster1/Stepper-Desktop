package datadefinition;

import exception.EnumerationDataException;

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
}
