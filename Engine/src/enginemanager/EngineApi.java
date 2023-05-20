package enginemanager;

import dto.*;

import javax.xml.bind.JAXBException;
import java.util.List;

public interface EngineApi {
    void loadXmlFile(String path) throws JAXBException;

    List<String> getFlowsNames();

    List<AvailableFlowDTO> getAvailableFlows();

    int getFlowIndexByName(String name);

    FlowDefinitionDTO getFlowDefinition(int flowIndex);

    FlowDefinitionDTO getFlowDefinition(String flowName);

    InputsDTO getFlowInputs(int flowIndex);

    ResultDTO processInput(String inputName, String data);

    boolean isFlowReady();

    FlowResultDTO runFlow(); //getFlowInputs

    List<String> getInitialHistoryList();

    FlowExecutionDTO getFullHistoryData(int flowIndex);

    StatisticsDTO getStatistics();

    ResultDTO saveDataOfSystemToFile(String path);

    ResultDTO loadDataOfSystemFromFile(String path);

    int getCurrInitializedFlowsCount();

    ContinutionMenuDTO getContinutionMenuDTO();

    void reUseInputsData(FlowExecutionDTO flowExecutionDTO);
}
