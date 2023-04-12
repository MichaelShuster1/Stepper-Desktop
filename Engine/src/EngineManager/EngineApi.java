package EngineManager;

import DTO.FreeInputsDTO;

import java.util.List;

public interface EngineApi
{
    String loadXmlFile(String path);
    List<String> getFlowsNames();
    String getFlowDefinition(int flowIndex);
    FreeInputsDTO getFlowInputs(int flowIndex);
    void processInput(String inputName,String data);
    boolean IsFlowReady();
    String runFlow(); //getFlowInputs
    List<String> getInitialHistoryList();
    String getFullHistoryData(int flowIndex);
    public String getStatistics();
}
