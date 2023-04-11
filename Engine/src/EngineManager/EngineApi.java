package EngineManager;

import java.util.List;

public interface EngineApi
{
    String loadXmlFile(String path);
    List<String> getFlowsNames();
    String getFlowDefinition(int flowIndex);
    List<String> getFlowInputs(int flowIndex);
    boolean processInput(String inputName,String data);
    String runFlow(); //getFlowInputs
    List<String> getInitialHistoryList();
    String getFullHistoryData(int flowIndex);
    public List<String> getStatistics();
}
