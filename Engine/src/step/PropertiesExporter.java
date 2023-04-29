package step;

import datadefinition.*;

import java.util.List;
import java.util.Map;

public class PropertiesExporter extends Step {
    public PropertiesExporter(String name, boolean continue_if_failing) {
        super(name, true, continue_if_failing);
        defaultName = "Properties Exporter";

        DataRelation input = new DataRelation("SOURCE");
        inputs.add(new Input(input, false, true, "Source data"));
        nameToInputIndex.put("SOURCE", 0);

        outputs.add(new Output(new DataString("RESULT"), "Properties export result"));
        nameToOutputIndex.put("RESULT", 0);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Relation relation = (Relation) inputs.get(0).getData();
        String result = "";
        if (relation == null || relation.getRows().size() == 0) {
            stateAfterRun = State.WARNING;
            outputs.get(0).setData(result);
            addLineToLog("No table was given to convert from to properties format string");
            summaryLine = "Warning: no table was given to convert to properties format";
        } else {
            List<Map<String, String>> rows = relation.getRows();
            List<String> columnNames = relation.getColumnNames();
            Integer index = 1, total_properties = 0;

            addLineToLog("About to process " + rows.size() + " lines of data");
            for (Map<String, String> row : rows) {
                int a = 1;
                for (String columnName : columnNames) {
                    String value = row.get(columnName);
                    result += "row-" + index + "." + columnName + "=" + value + "\r\n";
                    ;
                    total_properties++;
                    a++;
                }
                index++;
            }
            stateAfterRun = State.SUCCESS;
            outputs.get(0).setData(result);
            addLineToLog("Extracted total of " + total_properties);
            summaryLine = ("Step ended successfully, Extracted total of " + total_properties);
        }
        runTime = System.currentTimeMillis() - startTime;

    }
}
