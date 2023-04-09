package DataDefinitions;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Relation {

    private List<Map<String,String>> rows;
    private List<String> columnNames;




    public Relation(String[] names)
    {
        this.rows = new ArrayList<>();
        this.columnNames = new ArrayList<>();
        this.columnNames.addAll(Arrays.asList(names));
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


    public void setData(Pair<List<Map<String, String>>, List<String>> data) {
        this.rows = data.getKey();
        this.columnNames = data.getValue();
    }


    public Pair<List<Map<String, String>>, List<String>> getData()
    {
        return new Pair<List<Map<String, String>>, List<String>>(rows,columnNames);
    }
}
