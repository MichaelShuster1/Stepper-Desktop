package enginemanager;

import dto.InputsDTO;
import dto.ResultDTO;

import javax.xml.bind.JAXBException;
import java.util.List;

public interface EngineApi {
    void loadXmlFile(String path) throws JAXBException;

    List<String> getFlowsNames();

    String getFlowDefinition(int flowIndex);

    InputsDTO getFlowInputs(int flowIndex);

    ResultDTO processInput(String inputName, String data);

    boolean isFlowReady();

    String runFlow(); //getFlowInputs

    List<String> getInitialHistoryList();

    String getFullHistoryData(int flowIndex);

    String getStatistics();

    ResultDTO saveDataOfSystemToFile(String path);

    ResultDTO loadDataOfSystemFromFile(String path);

    int getCurrInitializedFlowsCount();
}
