package HardCodedData;

import EngineManager.Statistics;
import Steps.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HCSteps {
    List<String> stepNames;



    public HCSteps()
    {
        stepNames.add("Spend some Time");
        stepNames.add("Collect Files In Folder");
        stepNames.add("Files Deleter");
        stepNames.add("Files Renamer");
        stepNames.add("Files Content Extractor");
        stepNames.add("CSV Exporter");
        stepNames.add("Properties Exporter");
        stepNames.add("File Dumper");
    }

    public Map<String, Statistics> getStatisticsMap()
    {
        Map<String,Statistics> res = new HashMap<>();
        for(String name: stepNames)
        {
            res.put(name, new Statistics());
        }

        return res;
    }



}
