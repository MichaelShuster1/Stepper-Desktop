package hardcodeddata;

import enginemanager.Statistics;

import java.util.LinkedHashMap;
import java.util.Map;

public enum HCSteps {
    SPEND_SOME_TIME("Spend Some Time"),
    COLLECT_FILES("Collect Files In Folder"),
    FILES_RENAMER("Files Renamer"),
    FILES_CONTENT("Files Content Extractor"),
    CSV_EXPORTER("CSV Exporter"),
    PROPERTIES_EXPORTER("Properties Exporter"),
    FILE_DUMPER("File Dumper"),
    FILE_DELETER("Files Deleter");

    private String stepName;

    HCSteps(String stepName) {
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }


    public static Map<String, Statistics> getStatisticsMap() {
        Map<String, Statistics> res = new LinkedHashMap<>();
        for (HCSteps step : HCSteps.values()) {
            res.put(step.getStepName(), new Statistics());
        }
        return res;
    }
}
