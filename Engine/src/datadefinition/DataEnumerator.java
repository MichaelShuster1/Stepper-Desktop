package datadefinition;

import java.util.Set;

public class DataEnumerator extends DataDefinition<String>{
    private Set<String> values;
    private String data;


    public DataEnumerator(String name, Set<String> values) {
        super(name, "DataEnumerator");
        this.values = values;
    }

    @Override
    public void setData(String data) {
        if(values.contains(data))
            this.data=data;
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
