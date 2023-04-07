package DataDefinitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataRelation extends DataDefinition{
    private List<Map<String, String>> rows;
    private List<String> columnNames;

    public DataRelation(String name, String typeStream, boolean mandatory, List<String> columnNames)
    {
        super(name, "DataRelation", typeStream, mandatory, false);
        this.rows = new ArrayList<>();
        this.columnNames = columnNames;
    }

    public void addRow(Map<String, String> row) {
        rows.add(row);
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    @Override
    public String toString() {
        String res = "";
        for(String string : columnNames)
            res = res + string + "\n";
        res = res + rows.size();

        return res;
    }
}
