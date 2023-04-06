package DataDefinitions;

public class DataString extends DataDefinition{
    private String data;

    public DataString(String name, String typeStream, boolean mandatory, String data) {
        super(name, "String", typeStream, mandatory, true);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }
}
