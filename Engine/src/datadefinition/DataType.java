package datadefinition;

public enum DataType {
    FILE("File"),
    STRING("String"),
    NUMBER("Number"),
    LIST("List"),
    DOUBLE("Double"),
    ENUMERATOR("Enumerator"),
    MAPPING("Mapping"),
    RELATION("Relation");

    private final String type;
    DataType(String type) {
        this.type=type;
    }

    @Override
    public String toString(){
        return type;
    }


}
