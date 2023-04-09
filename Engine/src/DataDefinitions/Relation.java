package DataDefinitions;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Relation {

    private List<Map<String, DataDefinition>> rows;
    private List<String> columnNames;




    public Relation(String[] names)
    {
        this.rows = new ArrayList<>();
        this.columnNames = new ArrayList<>();
        this.columnNames.addAll(Arrays.asList(names));
    }

    public void addRow(Map<String, DataDefinition> row) {
        rows.add(row);
    }

    public List<Map<String, DataDefinition>> getRows() {
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


    public void setData(Pair<List<Map<String, DataDefinition>>, List<String>> data) {
        this.rows = data.getKey();
        this.columnNames = data.getValue();
    }


    public Pair<List<Map<String, DataDefinition>>, List<String>> getData()
    {
        return new Pair<List<Map<String, DataDefinition>>, List<String>>(rows,columnNames);
    }
}
