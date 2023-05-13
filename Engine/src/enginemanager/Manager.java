package enginemanager;

import dto.*;
import flow.Continuation;
import flow.Flow;
import flow.FlowHistory;
import generated.*;
import hardcodeddata.HCSteps;
import step.*;
import exception.*;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;


public class Manager implements EngineApi, Serializable {
    private List<Flow> flows;
    private List<FlowHistory> flowsHistory;
    private Map<String, Statistics> flowsStatistics;
    private Map<String, Statistics> stepsStatistics;
    private Flow currentFlow;
    private Map<String,Integer> flowNames2Index;



    public Manager() {
        flows = new ArrayList<>();
        flowsHistory = new ArrayList<>();
        flowsStatistics = new LinkedHashMap<>();
        stepsStatistics = new LinkedHashMap<>();
    }


    @Override
    public List<String> getFlowsNames() {
        List<String> namesList = new ArrayList<>();
        for (Flow flow : flows) {
            namesList.add(flow.getName());
        }
        return namesList;
    }

    @Override
    public void loadXmlFile(String path) throws JAXBException {
        STStepper stepper;
        try {
            File file = checkXMLPathAndGetFile(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(STStepper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            stepper = (STStepper) jaxbUnmarshaller.unmarshal(file);
            createFlows(stepper);
        } catch (JAXBException e) {
            throw e;
        }
    }

    private void createFlows(STStepper stepper) {
        Map<String,Integer> flowNames = new HashMap<>();
        List<STFlow> stFlows = stepper.getSTFlows().getSTFlow();
        List<Flow> flowList = new ArrayList<>();
        Map<String,List<Continuation>> continuationMap = new HashMap<>();
        Map<String, Statistics> statisticsMap = new LinkedHashMap<>();
        int i = 0;

        for (STFlow stFlow : stFlows) {
            String flowName = stFlow.getName();
            if (flowNames.containsKey(flowName)) {
                throw new FlowNameExistException("The xml file contains two flows with the same name");
            }
            flowNames.put(flowName,i);

            currentFlow = new Flow(flowName, stFlow.getSTFlowDescription());
            addFlowOutputs(stFlow);
            addSteps(stFlow);
            implementAliasing(stFlow);
            currentFlow.customMapping(getCustomMappings(stFlow));
            currentFlow.automaticMapping();
            currentFlow.setInitialValues(getInitialValues(stFlow));
            currentFlow.calculateFreeInputs();
            currentFlow.setFlowOutputs();
            if(getContinuations(stFlow).size() != 0)
                continuationMap.put(currentFlow.getName(),getContinuations(stFlow));
            currentFlow.checkFlowIsValid();
            statisticsMap.put(currentFlow.getName(), new Statistics());
            flowList.add(currentFlow);
            i++;
        }
        this.flowNames2Index = flowNames;
        createContinuations(continuationMap,flowList);

        flows.clear();
        flowsStatistics.clear();
        flowsHistory.clear();
        currentFlow = null;

        flows = flowList;
        setFlowsContinuations(continuationMap);
        flowsStatistics = statisticsMap;
        stepsStatistics = HCSteps.getStatisticsMap();
    }

    private void setFlowsContinuations(Map<String, List<Continuation>> continuationMap) {
        for(String flowName: continuationMap.keySet()) {
            flows.get(flowNames2Index.get(flowName)).setContinuations(continuationMap.get(flowName));
        }
    }

    private void createContinuations(Map<String,List<Continuation>> mapping,List<Flow> flowList) {
        for(String name : mapping.keySet()) {
            Integer sourceIndex = flowNames2Index.get(name);
            List<Continuation> currContinuationList = mapping.get(name);
            for (Continuation continuation : currContinuationList) {
                Integer targetIndex = flowNames2Index.get(continuation.getTargetFlow());
                if (targetIndex == null)
                    throw new ContinuationException("The flow \""+name+"\" contains a continuation to a flow that doesn't exists, flow name: " + continuation.getTargetFlow());

                Flow targetFlow = flowList.get(targetIndex);
                Flow sourceFlow = flowList.get(sourceIndex);
                continuation.createContinuation(sourceFlow, targetFlow);
            }
        }
    }

    private List<Continuation> getContinuations(STFlow stFlow) {
        List<Continuation> continuations = new ArrayList<>();
        if(stFlow.getSTContinuations() == null)
            return continuations;

        List<STContinuation> XMLContinuations = stFlow.getSTContinuations().getSTContinuation();
        for(STContinuation continuation : XMLContinuations) {
            Continuation currContinuation = new Continuation(continuation.getTargetFlow());
            List<STContinuationMapping> mapping = continuation.getSTContinuationMapping();
            if (mapping != null) {
                for (STContinuationMapping currMapping : mapping) {
                    String from = currMapping.getSourceData();
                    String to = currMapping.getTargetData();
                    currContinuation.addForcedConnection(from, to);
                }
            }
            continuations.add(currContinuation);
        }

        return continuations;
    }


    private Map<String,String> getInitialValues(STFlow stFlow)
    {
        Map<String,String> initialValues = new HashMap<>();
        if(stFlow.getSTInitialInputValues() == null)
            return initialValues;

        List<STInitialInputValue> initialInputValues = stFlow.getSTInitialInputValues().getSTInitialInputValue();
        for(STInitialInputValue currValue : initialInputValues) {
            initialValues.put(currValue.getInputName(),currValue.getInitialValue());
        }

        return initialValues;
    }



    private Map<Pair<String, String>, Pair<String, String>> getCustomMappings(STFlow stFlow) {
        Map<Pair<String, String>, Pair<String, String>> customMappings = new HashMap<>();

        if (stFlow.getSTCustomMappings() == null)
            return customMappings;

        List<STCustomMapping> customMappingsList = stFlow.getSTCustomMappings().getSTCustomMapping();


        for (STCustomMapping customMapping : customMappingsList) {
            customMappings.put(new Pair<>(customMapping.getTargetStep(), customMapping.getTargetData()),
                    new Pair<>(customMapping.getSourceStep(), customMapping.getSourceData()));
        }
        return customMappings;
    }

    private void implementAliasing(STFlow stFlow) {
        if (stFlow.getSTFlowLevelAliasing() == null)
            return;

        List<STFlowLevelAlias> aliases = stFlow.getSTFlowLevelAliasing().getSTFlowLevelAlias();

        for (STFlowLevelAlias alias : aliases) {
            implementAlias(alias);
        }
    }

    private void implementAlias(STFlowLevelAlias alias) {
        Boolean found = false;
        String stepName = alias.getStep();
        Integer stepIndex = currentFlow.getStepIndexByName(stepName);

        if (stepIndex == null) {
            throw new StepNameNotExistException("In the flow named: " + currentFlow.getName()
                    + "\nthere is an attempt to perform FlowLevelAliasing"
                    + " in the step by the name: " + stepName + " that was not defined in the flow");
        }

        Step step = currentFlow.getStep(stepIndex);
        String oldName = alias.getSourceDataName(), newName = alias.getAlias();

        if(oldName.equals(newName))
            return;

        if (step.getNameToInputIndex().get(newName) != null) {
            throw new InputNameExistException("In the flow named: " + currentFlow.getName()
                    + "\nThere is an attempt to perform FlowLevelAliasing for a data "
                    + "in the step: " + stepName
                    + "\nTo the name: " + newName +
                    " which is already used as a name for another input data");
        }

        if (step.getNameToOutputIndex().get(newName) != null) {
            throw new OutputNameExistException("In the flow named: " + currentFlow.getName()
                    + "\nThere is an attempt to perform FlowLevelAliasing for a data "
                    + "in the step: " + stepName
                    + "\nTo the name: " + newName +
                    " which is already used as a name for another output data");
        }

        if (step.getNameToInputIndex().get(oldName) != null) {
            step.changeInputName(oldName, newName);
            found = true;
        }

        if (!found && step.getNameToOutputIndex().get(oldName) != null) {
            step.changeOutputName(oldName, newName);
            found = true;
        }

        if (!found) {
            throw new InputOutputNotExistException("In the flow named: " + currentFlow.getName()
                    + "\nthere is an attempt to perform FlowLevelAliasing "
                    + "in the step: " + stepName + " for the data: "
                    + oldName + "\nthat was not defined in the step");
        }
    }

    private void addSteps(STFlow stFlow) {
        List<STStepInFlow> steps = stFlow.getSTStepsInFlow().getSTStepInFlow();
        for (STStepInFlow step : steps) {
            String alias = step.getAlias(), name = step.getName();
            if (alias != null) {
                if (currentFlow.getStepIndexByName(alias) != null) {
                    throw new StepNameExistException("In the flow named: " + currentFlow.getName()
                            + "\nthere is an attempt to perform aliasing "
                            + "for name of the step: " + name
                            + "\nTo the name: " + alias + " that was defined before");
                }
            } else if (currentFlow.getStepIndexByName(name) != null) {
                throw new StepNameExistException("In the flow named: " + currentFlow.getName()
                        + "\nthere is an attempt to define the step by the name: " + name
                        + " that was defined before");
            }
            currentFlow.addStep(createStep(step));
        }
    }

    private Step createStep(STStepInFlow step) {
        Step newStep = null;
        boolean continueIfFailing = false;
        String finalName = step.getName(), name = step.getName();

        if (step.getAlias() != null)
            finalName = step.getAlias();

        if (step.isContinueIfFailing() != null)
            continueIfFailing = true;

        switch (name) {
            case "Spend Some Time":
                newStep = new SpendSomeTime(finalName, continueIfFailing);
                break;
            case "Collect Files In Folder":
                newStep = new CollectFiles(finalName, continueIfFailing);
                break;
            case "Files Renamer":
                newStep = new FilesRenamer(finalName, continueIfFailing);
                break;
            case "Files Content Extractor":
                newStep = new FilesContentExtractor(finalName, continueIfFailing);
                break;
            case "CSV Exporter":
                newStep = new CSVExporter(finalName, continueIfFailing);
                break;
            case "Properties Exporter":
                newStep = new PropertiesExporter(finalName, continueIfFailing);
                break;
            case "File Dumper":
                newStep = new FileDumper(finalName, continueIfFailing);
                break;
            case "Files Deleter":
                newStep = new FilesDeleter(finalName, continueIfFailing);
                break;
            case "Zipper":
                newStep = new Zipper(finalName, continueIfFailing);
                break;
            case "Command Line":
                newStep = new CommandLine(finalName, continueIfFailing);
                break;
            default:
                throw new StepNameNotExistException("In the flow named: " + currentFlow.getName()
                        + "\nthere is an attempt to define a step by the name: "
                        + step.getName() + " that does not exist");
        }
        return newStep;
    }


    private void addFlowOutputs(STFlow stFlow) {
        String[] outputs = stFlow.getSTFlowOutput().split(",");
        for (String output : outputs)
            currentFlow.addFormalOutput(output);
    }


    @Override
    public FlowDefinitionDTO getFlowDefinition(int flowIndex) {
        return flows.get(flowIndex).getFlowDefinition();
    }


    @Override
    public InputsDTO getFlowInputs(int flowIndex) {
        currentFlow = flows.get(flowIndex);
        return currentFlow.getInputList();
    }


    @Override
    public ResultDTO processInput(String inputName, String data) {
        return currentFlow.processInput(inputName, data);
    }


    @Override
    public boolean isFlowReady() {
        return currentFlow.isFlowReady();
    }


    @Override
    public FlowResultDTO runFlow() {
        FlowResultDTO res = currentFlow.executeFlow();
        addFlowHistory();
        addStatistics();
        currentFlow.resetFlow();
        return res;
    }


    @Override
    public List<String> getInitialHistoryList() {
        List<String> res = new ArrayList<>();
        for (FlowHistory history : flowsHistory) {
            String currHistory = "Flow name: " + history.getFlowName() + "\nFlow ID: " + history.getID() + "\nFlow activation time: " + history.getActivationTime() + "\n";
            res.add(currHistory);
        }
        return res;
    }


    @Override
    public FlowExecutionDTO getFullHistoryData(int flowIndex) {
        return flowsHistory.get(flowIndex).getFullData();
    }



    @Override
    public ResultDTO saveDataOfSystemToFile(String FILE_NAME) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE_NAME))) {
            out.writeObject(flows);
            out.writeObject(flowsHistory);
            out.writeObject(flowsStatistics);
            out.writeObject(stepsStatistics);
            out.flush();
            return new ResultDTO(true, "System's parameters saved successfully");
        } catch (Exception e) {
            return new ResultDTO(false, "The System's parameters saving failed " +
                    "because: " + e.getMessage());
        }
    }

    @Override
    public ResultDTO loadDataOfSystemFromFile(String FILE_NAME) {
        File file = new File(FILE_NAME);
        Manager manager = null;
        if (file.exists()) {
            try (ObjectInputStream in =
                         new ObjectInputStream(
                                 new FileInputStream(FILE_NAME))) {
                flows = (List<Flow>) in.readObject();
                flowsHistory = (List<FlowHistory>) in.readObject();
                flowsStatistics = (Map<String, Statistics>) in.readObject();
                stepsStatistics = (Map<String, Statistics>) in.readObject();

                return new ResultDTO(true, "Loaded the system successfully");
            }
            catch (IOException  e) {
                return new ResultDTO(false, "Failed to load data from the given file " +
                        "because: " + e.getMessage());
            }
            catch (ClassNotFoundException e) {
                return new ResultDTO(false, "Failed to load data from the given file " +
                        "because: " + e.getMessage());
            }
            catch (NoClassDefFoundError e){
                return new ResultDTO(false, "Failed to load data from the given file " +
                        "because: " + e.getMessage());
            }



        }
        return new ResultDTO(false, "The file in the given path doesn't exist");
    }

    private void addFlowHistory() {
        FlowHistory flowHistory = new FlowHistory(currentFlow.getName(),
                currentFlow.getFlowId(), currentFlow.getActivationTime(), currentFlow.getFlowHistoryData());
        flowsHistory.add(0, flowHistory);
    }

    @Override
    public StatisticsDTO getStatistics()
    {
        List<StatisticsUnitDTO> statisticsOfFlows= packageStatistics(flowsStatistics);
        List<StatisticsUnitDTO> statisticsOfSteps= packageStatistics(stepsStatistics);
        return new StatisticsDTO(statisticsOfFlows,statisticsOfSteps);
    }


    private List<StatisticsUnitDTO> packageStatistics(Map<String,Statistics> statisticsMap) {
        List<StatisticsUnitDTO> statisticsOfFlows=new ArrayList<>();

        for (String name : statisticsMap.keySet()) {
            Statistics statistics = statisticsMap.get(name);
            StatisticsUnitDTO statisticsUnitDTO= new StatisticsUnitDTO(statistics.getTimesActivated(),statistics.getAvgRunTime(), name);
            statisticsOfFlows.add(statisticsUnitDTO);
        }
        return  statisticsOfFlows;
    }



    private void addStatistics() {
        Integer size = currentFlow.getNumberOfSteps();
        Statistics statistics = flowsStatistics.get(currentFlow.getName());
        statistics.addRunTime(currentFlow.getRunTime());
        boolean flowStopped = false;

        for (int i = 0; i < size && !flowStopped; i++) {
            Step step = currentFlow.getStep(i);
            statistics = stepsStatistics.get(step.getDefaultName());
            statistics.addRunTime(step.getRunTime());
            if (step.getStateAfterRun() == State.FAILURE && !step.isContinueIfFailing())
                flowStopped = true;
        }
    }

    private File checkXMLPathAndGetFile(String path) {
        File file = new File(path);

        if (!file.exists()) {
            throw new XmlFileException("The file does not exist in the given path");
        } else if (!path.endsWith(".xml")) {
            throw new XmlFileException("The file name does not end with an .xml extension");
        }

        return file;
    }

    public int getCurrInitializedFlowsCount() {
        return flows.size();
    }


}
