package datadefinition;

import java.io.Serializable;
import java.util.List;

public abstract class DataDefinition<T> implements Serializable {
    protected String name;
    protected final String type;

    protected DataDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public abstract void setData(T data);

    public abstract T getData();

    public abstract List<String> getSecondaryData();
}
