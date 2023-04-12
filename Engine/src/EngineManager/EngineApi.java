package EngineManager;

import DTO.InputsDTO;
import DTO.StatusDTO;

import java.util.List;

public interface EngineApi
{
    String loadXmlFile(String path);
    List<String> getFlowsNames();
    String getFlowDefinition(int flowIndex);
    InputsDTO getFlowInputs(int flowIndex);
    void processInput(String inputName,String data);
    boolean IsFlowReady();
    String runFlow(); //getFlowInputs
    List<String> getInitialHistoryList();
    String getFullHistoryData(int flowIndex);
    String getStatistics();
    StatusDTO saveDataOfSystemToFile(String path);
    StatusDTO loadDataOfSystemFromFile(String path);
}
