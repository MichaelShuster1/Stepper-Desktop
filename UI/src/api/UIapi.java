package api;

import datadefinition.Input;
import datadefinition.Output;
import dto.*;
import enginemanager.EngineApi;
import enginemanager.Manager;
import step.State;
import step.Step;

import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class UIapi {

    public enum CODE {
        EMPTY(-2), BACK(-1);

        private int numVal;

        CODE(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    Scanner inputStream;

    EngineApi engine;


    public UIapi(Manager engine) {
        this.inputStream = new Scanner(System.in);
        this.engine = engine;
    }


    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    public void runSystem() {
        Boolean exit = false;
        boolean correctInput;
        System.out.println("Welcome to the Stepper application!");
        while (!exit) {
            correctInput = false;
            printMainMenu();
            while (!correctInput) {
                Integer userChoice = getIntInput();
                if (userChoice != null) {
                    if (userChoice >= 1 && userChoice <= 8) {
                        exit = processInput(userChoice);
                        correctInput = true;
                    } else
                        System.out.println("Incorrect index, please enter an index between 1-8");
                }
            }

        }
        System.out.println("Goodbye!");
    }


    public void printMainMenu() {
        System.out.println("\nThe main menu:");
        System.out.println("Please select one of the following commands:");
        System.out.println("1. Load new XML file");
        System.out.println("2. Show current flows definitions");
        System.out.println("3. Execute a flow");
        System.out.println("4. Show past flows executions");
        System.out.println("5. Show statistics");
        System.out.println("6. Load previous system state from a file");
        System.out.println("7. Save the current system's parameters to a file");
        System.out.println("8. Exit");
        System.out.println("Please enter the index of the desired action [number] :");
    }

    public Boolean processInput(int index) {
        Boolean exit = false;
        switch (index) {
            case 1:
                loadXMLFile();
                break;
            case 2:
                showFlowsDefinitions();
                break;
            case 3:
                ExecuteFlow();
                break;
            case 4:
                showFlowsHistory();
                break;
            case 5:
                showStatistics();
                break;
            case 6:
                loadSystemDataFromFile();
                break;
            case 7:
                saveSystemDataToFile();
                break;
            case 8:
                exit = true;
                break;
        }
        return exit;
    }

    public void loadXMLFile() {
        String path;
        System.out.println("Please enter the full path of the XML file: ");
        path = inputStream.nextLine();
        try {
            engine.loadXmlFile(path);
            System.out.println("The given xml file has been loaded successfully to the system");
        } catch (RuntimeException e) {
            System.out.println("The given file has failed to load into the system because:");
            System.out.println(e.getMessage());
        } catch (JAXBException e) {
            System.out.println("The given file was not successfully loaded into the system because:"
                    + "\nInvalid xml schema");
        }

    }


    public void saveSystemDataToFile() {
        String pathFile;
        System.out.println("Please enter the full path of the file to save the system to (including the file name):");
        pathFile = inputStream.nextLine();
        ResultDTO result = engine.saveDataOfSystemToFile(pathFile);
        System.out.println(result.getMessage());
    }

    public boolean loadSystemDataFromFile() {
        String pathFile;
        System.out.println("Please enter the full path of the file to load the system from:");
        pathFile = inputStream.nextLine();
        ResultDTO result = engine.loadDataOfSystemFromFile(pathFile);
        System.out.println(result.getMessage());
        return result.getStatus();
    }


    public void showStatistics() {
        System.out.println(engine.getStatistics());
    }


    public void showFlowsDefinitions() {
        if (engine.getCurrInitializedFlowsCount() > 0)
            System.out.println("Please choose one of the following flows to get its full definition:");
        Integer userChoice = chooseFlow();

        if (userChoice != CODE.EMPTY.getNumVal() && userChoice != CODE.BACK.getNumVal()) {
            FlowDefinitionDTO flowDefinition=engine.getFlowDefinition(userChoice);
            String data;
            data = "SELECTED FLOW DATA:\n\n";
            data += "Flow name: " + flowDefinition.getName() + "\n";
            data += "Flow description: " + flowDefinition.getDescription() + "\n\n";
            data += getStrFormalOutputs(flowDefinition.getFormal_outputs()) + "\n";
            data += getStrReadOnlyStatus(flowDefinition.isReadOnly());
            data += "------------------------------\n";
            data += getStrStepsData(flowDefinition.getSteps());
            data += "------------------------------\n";
            data += getStrFreeInputs(flowDefinition.getFreeInputs());
            data += "------------------------------\n";
            data += getStrOutPuts(flowDefinition.getOutputs());
            data += "------------------------------\n";
            System.out.println(data);
        }
    }


    private String getStrFormalOutputs(Set<String> formal_outputs) {
        String res;
        if (formal_outputs.size() > 0) {
            res = "The formal outputs of the flow are:\n";
            for (String currOutput : formal_outputs) {
                res = res + currOutput + "\n";
            }
        } else
            res = "The flow doesn't have formal outputs\n";

        return res;
    }

    private String getStrReadOnlyStatus(boolean read_only) {
        if (read_only)
            return "The flow is Read-Only: YES\n";
        else
            return "The flow is Read-Only: NO\n";
    }

    private String getStrStepsData(List<StepDefinitionDTO> steps) {
        String res = "THE FLOW'S STEPS:\n";
        String currStep;
        for (StepDefinitionDTO step : steps) {
            if (step.getName().equals(step.getDefaultName()))
                currStep = "Step name: " + step.getName() + "\n";
            else {
                currStep = "Step name: " + step.getDefaultName() + "\n";
                currStep += "Step alias: " + step.getName() + "\n";
            }
            if (step.isReadOnly())
                currStep = currStep + "The step is Read-Only: YES\n";
            else
                currStep = currStep + "This step is Read-Only: No\n";
            currStep = currStep + "\n";
            res += currStep;
        }

        return res;
    }

    private String getStrFreeInputs(List<FreeInputDefinitionDTO> flowFreeInputs) {
        String res;
        if (flowFreeInputs.isEmpty())
            res = "The flow have no free inputs\n";
        else {
            res = "Flow's free input's are:\n\n";
            String currInput;
            for (FreeInputDefinitionDTO freeInput : flowFreeInputs) {
                currInput = "Name: " + freeInput.getName() + "\n";
                currInput += "Type: " + freeInput.getType() + "\n";
                currInput += "Steps that are related to that input: ";
                for (String stepName : freeInput.getRelatedSteps())
                    currInput += stepName + ", ";
                currInput = currInput.substring(0, currInput.length() - 2);
                currInput += "\n";
                if (freeInput.isMandatory())
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";
                res += currInput;
            }
        }

        return res;
    }

    public String getStrOutPuts(List<OutputDefintionDTO> outputs) {
        String res = "THE FLOW'S OUTPUTS:\n";

        for (OutputDefintionDTO output : outputs) {
                res += "Output name: " + output.getName() + "\n";
                res += "Type: " + output.getType() + "\n";
                res += "Belongs to step: " + output.getStepName() + "\n\n";
        }

        if (outputs.size() > 0)
            return res;
        else
            return "THIS FLOW HAVE NO OUTPUTS";
    }


    public String getFlowExecutionStrData(FlowResultDTO flowResult) {
        String res = "Flows unique ID: " + flowResult.getId() + "\n";
        res += "Flow name: " + flowResult.getName() + "\n";
        res += "Flow's final state : " + flowResult.getState() + "\n";
        List<OutputExecutionDTO> formalOutputs = flowResult.getFormalOutputs();
        if (formalOutputs.size() > 0) {
            res += "FLOW'S FORMAL OUTPUTS:\n";
            for (OutputExecutionDTO output: formalOutputs) {
                res += output.getType() + "\n";
                if (output.getData() != null)
                    res += output.getData() + "\n";
                else
                    res += "Not created due to failure in flow\n";

            }
        } else {
            res += "THE FLOW HAVE NO OUTPUTS\n";
        }
        return res;
    }


    public void showFlowsHistory() {
        Integer userChoice = null;
        List<String> initialList = engine.getInitialHistoryList();
        if (initialList.size() > 0) {
            System.out.println("Please choose one of the following flows to get its past execution data:");
            printIndexedList(initialList, "\n");
            userChoice = getUserIndexInput(initialList.size());

            if (userChoice != CODE.BACK.getNumVal()) {
                String history=getFlowHistoryData(engine.getFullHistoryData(userChoice));
                System.out.println(history);
            }
        } else System.out.println("There are currently no past executions\n");
    }


    private String getFlowHistoryData(FlowExecutionDTO flowExecutionDTO) {
        String res = getFlowNameIDAndState(flowExecutionDTO);
        String temp;

        res += "Flow total run time: " + flowExecutionDTO.getRunTime() + " ms\n\n";
        res += "------------------------------\n";
        res += "FREE INPUTS THAT RECEIVED DATA:\n\n";
        temp = getFreeInputsHistoryData(flowExecutionDTO.getFreeInputs(),true);
        temp += getFreeInputsHistoryData(flowExecutionDTO.getFreeInputs(),false);
        if (temp.length() == 0)
            res += "NO FREE INPUTS HAVE RECEIVED DATA\n\n";
        else
            res += temp;
        res += "------------------------------\n";
        res += "DATA PRODUCED (OUTPUTS):\n\n";
        temp = getOutputsHistoryData(flowExecutionDTO.getOutputs());
        if (temp.length() == 0)
            res += "NO DATA WAS PRODUCED\n\n";
        else
            res += temp;
        res += "------------------------------\n";
        res += "FLOW STEPS DATA:\n\n";
        res += getStepsHistoryData(flowExecutionDTO.getSteps());

        return res;
    }


    private String getFlowNameIDAndState(FlowExecutionDTO flowExecutionDTO) {
        String res = "FLOW EXECUTION DATA:\n";
        res += "Flows unique ID: " + flowExecutionDTO.getId() + "\n";
        res += "Flow name: " + flowExecutionDTO.getName() + "\n";
        res += "Flow's final state : " + flowExecutionDTO.getStateAfterRun() + "\n";
        return res;
    }


    private String getFreeInputsHistoryData(List<FreeInputExecutionDTO> flowFreeInputs,boolean mandatoryOrNot) {
        String res = "";
        String currInput;
        for (FreeInputExecutionDTO freeInput : flowFreeInputs) {
            if (freeInput.getData() != null) {
                currInput = "Name: " + freeInput.getName() + "\n";
                currInput += "Type: " + freeInput.getType() + "\n";
                if(freeInput.getType().equals("List") || freeInput.getType().equals("Relation") || freeInput.getType().equals("Mapping"))
                     currInput += "Input data:\n" + freeInput.getData() + "\n";
                else
                    currInput += "Input data: " + freeInput.getData() + "\n";
                if (freeInput.isMandatory())
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";

                if (mandatoryOrNot && freeInput.isMandatory())
                    res += currInput;
                else if (!mandatoryOrNot && !freeInput.isMandatory())
                    res += currInput;
            }
        }

        return res;
    }

    private String getOutputsHistoryData(List<OutputExecutionDTO> outputs) {
        String res = "";

        for (OutputExecutionDTO output : outputs) {
            res += "Name: " + output.getName() + "\n";
            res += "Type: " + output.getType() + "\n";
            if (output.getData() != null) {
                if(output.getType().equals("List") || output.getType().equals("Relation") || output.getType().equals("Mapping"))
                     res += "Data:\n" + output.getData() + "\n\n";
                else
                     res += "Data: " + output.getData() + "\n\n";

            }
            else
                res += "Data: Not created due to failure in flow\n\n";

        }
        return res;
    }


    private String getStepsHistoryData(List<StepExecutionDTO> steps) {
        String res = "";
        for (StepExecutionDTO step: steps) {
            res += "Name: " + step.getName() + "\n";
            res += "Run time: " + step.getRunTime() + " ms\n";
            res += "Finish state: " + step.getStateAfterRun() + "\n";
            res += "Step summary:" + step.getSummaryLine()+ "\n";
            res += "STEP LOGS:\n\n";
            res += getStrLogs(step.getLogs());
            res += "------------------------------\n";
        }
        return res;
    }


    private String getStrLogs(List<String> logs) {
        String res = "";
        if (logs.size() == 0)
            return "The step had no logs\n\n";
        else {
            for (String currLog : logs) {
                res += currLog + "\n\n";
            }
        }
        return res;
    }


    public int chooseFlow() {
        Integer userChoice = null;
        List<String> flowNames = engine.getFlowsNames();
        if (flowNames.size() > 0) {
            System.out.println("The flows:");
            printIndexedList(flowNames);
            userChoice = getUserIndexInput(flowNames.size());
        } else {
            System.out.println("There are currently no defined flows in the system.\nYou can load flows to the system by using command 1 in the main menu.\n");
            userChoice = CODE.EMPTY.getNumVal();
        }

        return userChoice;
    }

    public boolean checkIndexInRange(int lastIndex, int index) {
        if (index > lastIndex || index < 0) {
            System.out.println("Incorrect index, please enter an index between 1-" + lastIndex + ", or 0 to go back to the menu");
            return false;
        } else
            return true;
    }

    public int getUserIndexInput(int lastIndex) {
        boolean correctInput = false;
        Integer userChoice = null;
        while (!correctInput) {
            System.out.println("Enter the index of the desired flow, or 0 to go back to the menu [number] : ");
            userChoice = getIntInput();
            if (userChoice != null) {
                correctInput = checkIndexInRange(lastIndex, userChoice);
            }
        }
        return userChoice - 1;
    }


    private void printIndexedList(List<String> list) {
        printIndexedList(list, "");
    }

    private void printIndexedList(List<String> list, String changeAfterIndex) {
        int index = 1;
        for (String data : list) {
            System.out.println(index + ". " + changeAfterIndex + data);
            index++;
        }
        System.out.println("0. Exit");
    }


    private Integer getIntInput() {
        Integer res = null;

        try {
            res = inputStream.nextInt();
            if (inputStream.hasNextLine())
                inputStream.nextLine();
        } catch (Exception e) {
            System.out.println("Incorrect input. please enter an Integer only");
            inputStream.nextLine();
        }

        return res;
    }


    public void ExecuteFlow() {
        InputsDTO inputsInfo;
        Integer flowIndex, choice, size;
        String inputMenu, data;
        boolean flowReady = false, runFlow = false;

        flowIndex = chooseFlow();

        if (flowIndex == CODE.BACK.getNumVal() || flowIndex == CODE.EMPTY.getNumVal())
            return;

        inputsInfo = engine.getFlowInputs(flowIndex);
        inputMenu = createInputMenu(inputsInfo);
        size = inputsInfo.getNumberOfInputs();

        while (!runFlow) {
            System.out.println(inputMenu);

            flowReady = engine.isFlowReady();

            printInstructions(size, flowReady);

            do {
                choice = getIntInput();
            } while (choice == null);


            if (choice <= size && choice >= 1) {
                InputData input = inputsInfo.getFreeInput(choice - 1);
                processDataInput(input);
            } else if (choice.equals(size + 1) && flowReady) {
                runFlow = true;
                System.out.println(getFlowExecutionStrData(engine.runFlow()));
            } else if (choice.equals(0))
                return;
            else {
                if (flowReady)
                    System.out.println("Incorrect index, please enter an index between 1-" + (size + 1) + ", or 0 to go back to the menu");
                else
                    System.out.println("Incorrect index, please enter an index between 1-" + size + ", or 0 to go back to the menu");
            }

        }
    }

    private void processDataInput(InputData input) {
        String data;
        String inputName;

        inputName = input.getSystemName();

        System.out.println("Please enter the input here: ");

        data = inputStream.nextLine();

        ResultDTO res = engine.processInput(inputName, data);
        System.out.println(res.getMessage() + "\n");
    }

    private void printInstructions(Integer size, boolean flowReady) {
        if (flowReady)
            System.out.println((size + 1) + ".Execute the flow");

        System.out.println("Enter the number 0 to go back to the main menu");
        System.out.println("Enter a number between 1 to " + size + " to enter the data  of the desired input you want");
        if (flowReady)
            System.out.println("Enter the number " + (size + 1) + " to to execute the current flow");
        if (!flowReady)
            System.out.println("When all mandatory inputs will be inserted, you will be able to execute the flow");
        System.out.println("Please enter your choice: ");
    }


    private String createInputMenu(InputsDTO inputsInfo) {
        String inputMenu = "", inputToShow = "";
        Integer size = inputsInfo.getNumberOfInputs();
        inputMenu += "The input menu:\n";
        inputMenu += "0. Exit\n";
        for (int i = 0; i < size; i++) {
            InputData input = inputsInfo.getFreeInput(i);
            inputToShow = input.getUserString();

            inputToShow += " [";
            inputToShow += input.getSystemName().toLowerCase().replace("_", " ");
            inputToShow += "]";

            inputToShow += " [";
            if (input.getNecessity())
                inputToShow += "mandatory";
            else
                inputToShow += "optional";
            inputToShow += "]";

            inputMenu += (i + 1) + ". " + inputToShow + "\n";
        }
        return inputMenu;
    }


}
