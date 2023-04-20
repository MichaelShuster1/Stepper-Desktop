package EngineManager;

import DTO.InputsDTO;
import DTO.ResultDTO;
import Flow.Flow;
import Flow.FlowHistory;
import Generated.*;
import HardCodedData.HCSteps;
import Steps.*;
import exceptions.FlowNameExistException;
import exceptions.InputOutputNotExist;
import exceptions.StepNameNotExistException;
import exceptions.XmlFileException;
import javafx.util.Pair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;


public class Manager implements EngineApi, Serializable
{
    private List<Flow> flows;
    private List<FlowHistory> flowsHistory;
    private Map<String,Statistics> flowsStatistics;
    private Map<String,Statistics> stepsStatistics;
    private Flow currentFlow;


    public Manager(Map<String, Statistics> flowsStatistics, Map<String, Statistics> stepsStatistics)
    {
        flows = new ArrayList<>();
        flowsHistory = new ArrayList<>();
        this.flowsStatistics = flowsStatistics;
        this.stepsStatistics = stepsStatistics;
    }


    public Manager()
    {
        flows = new ArrayList<>();
        flowsHistory = new ArrayList<>();
        flowsStatistics=new LinkedHashMap<>();
        stepsStatistics= new HashMap<>();
    }


    @Override
    public List<String> getFlowsNames()
    {
        List<String> namesList = new ArrayList<>();
        for(Flow flow: flows)
        {
            namesList.add(flow.getName());
        }
        return namesList;
    }

    @Override
    public String loadXmlFile(String path)
    {
        STStepper stepper;
        try {
            File file = checkXMLPathAndGetFile(path);
            JAXBContext jaxbContext = JAXBContext.newInstance(STStepper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            stepper = (STStepper) jaxbUnmarshaller.unmarshal(file);
            createFlows(stepper);
        }
        catch (JAXBException e)
        {
            System.out.println(e.getMessage());

        }

        return null;
    }

    private void createFlows(STStepper stepper)
    {
        Set<String> flowNames=new HashSet<>();
        List<STFlow> stFlows= stepper.getSTFlows().getSTFlow();
        List<Flow> flowList=new ArrayList<>();
        Map<String,Statistics> statisticsMap=new LinkedHashMap<>();

        for (STFlow stFlow: stFlows)
        {
            String flowName=stFlow.getName();
            if(flowNames.contains(flowName))
            {
                throw new FlowNameExistException("The xml file contains two flows with the same name");
            }
            flowNames.add(flowName);

            currentFlow=new Flow(flowName,stFlow.getSTFlowDescription());
            addFlowOutputs(stFlow);
            addSteps(stFlow);
            implementAliasing(stFlow);
            currentFlow.CustomMapping(getCustomMappings(stFlow));
            currentFlow.AutomaticMapping();
            currentFlow.CalculateFreeInputs();
            currentFlow.checkFlowIsValid();
            statisticsMap.put(currentFlow.getName(), new Statistics());
            flowList.add(currentFlow);
        }

        flows.clear();
        flowsStatistics.clear();
        currentFlow=null;

        flows=flowList;
        flowsStatistics=statisticsMap;
        stepsStatistics=HCSteps.getStatisticsMap();
    }

    private Map<Pair<String,String>,Pair<String,String>> getCustomMappings(STFlow stFlow)
    {
        Map<Pair<String,String>,Pair<String,String>> customMappings=new HashMap<>();

        if(stFlow.getSTCustomMappings()==null)
            return  customMappings;

        List<STCustomMapping> customMappingsList= stFlow.getSTCustomMappings().getSTCustomMapping();

        for(STCustomMapping customMapping:customMappingsList)
        {
            customMappings.put(new Pair<>(customMapping.getSourceStep(),customMapping.getSourceData()),
                    new Pair<>(customMapping.getTargetStep(),customMapping.getTargetData()));
        }
        return  customMappings;
    }

    private void implementAliasing(STFlow stFlow)
    {
        if(stFlow.getSTFlowLevelAliasing()==null)
            return;

        List<STFlowLevelAlias> aliases= stFlow.getSTFlowLevelAliasing().getSTFlowLevelAlias();

        for(STFlowLevelAlias alias:aliases)
        {
            implementAlias(alias);
        }
    }

    private void implementAlias(STFlowLevelAlias alias)
    {
        Boolean found=false;
        String stepName=alias.getStep();
        Integer stepIndex= currentFlow.getStepIndexByName(stepName);

        if(stepIndex==null)
        {
            throw new StepNameNotExistException("In the flow named: " +currentFlow.getName()
                    + "\nthere is an attempt to perform FlowLevelAliasing "
                    + " in the step by the name: "+stepName+" that was not defined in the flow");
        }

        Step step=currentFlow.getStep(stepIndex);
        String oldName= alias.getSourceDataName(),newName= alias.getAlias();

        if(step.getNameToInputIndex().get(oldName)!=null)
        {
            step.changeInputName(oldName, newName);
            found=true;
        }

        if(!found&& step.getNameToOutputIndex().get(oldName)!=null)
        {
            step.changeOutputName(oldName,newName);
            found=true;
        }

        if(!found)
        {
            throw new InputOutputNotExist("In the flow named: " +currentFlow.getName()
                    + "\nthere is an attempt to perform FlowLevelAliasing "
                    + "in the step: "+stepName+" for the data: "
                    + oldName+"\nthat was not defined in the step");
        }
    }

    private void addSteps(STFlow stFlow)
    {
        List<STStepInFlow> steps= stFlow.getSTStepsInFlow().getSTStepInFlow();
        for(STStepInFlow step:steps)
        {
            currentFlow.AddStep(createStep(step));
        }
    }

    private Step createStep(STStepInFlow step)
    {
        Step newStep=null;
        boolean continueIfFailing=false;
        String finalName=step.getName(),name=step.getName();

        if(step.getAlias()!=null)
            finalName=step.getAlias();

        if(step.isContinueIfFailing()!=null)
            continueIfFailing=true;

        switch (name)
        {
            case "Spend Some Time":
                newStep= new SpendSomeTime(finalName,continueIfFailing);
                break;
            case "Collect Files In Folder":
                newStep= new CollectFiles(finalName,continueIfFailing);
                break;
            case "Files Renamer":
                newStep= new FilesRenamer(finalName,continueIfFailing);
                break;
            case "Files Content Extractor":
                newStep= new FilesContentExtractor(finalName,continueIfFailing);
                break;
            case "CSV Exporter":
                newStep= new CSVExporter(finalName,continueIfFailing);
                break;
            case "Properties Exporter":
                newStep= new PropertiesExporter(finalName,continueIfFailing);
                break;
            case "File Dumper":
                newStep=  new FileDumper(finalName,continueIfFailing);
                break;
            case "Files Deleter":
                newStep= new FilesDeleter(finalName,continueIfFailing);
                break;
            default:
                throw new StepNameNotExistException("In the flow named: "+ currentFlow.getName()
                        + "\nthere is an attempt to define a step by the name: "
                        + step.getName()+" that does not exist");
        }
        return newStep;
    }


    private void addFlowOutputs(STFlow stFlow)
    {
        String[] outputs= stFlow.getSTFlowOutput().split(",");
        for(String output:outputs)
            currentFlow.addFormalOutput(output);
    }


    @Override
    public String getFlowDefinition(int flowIndex)
    {
       return flows.get(flowIndex).flowPrintData();
    }


    @Override
    public InputsDTO getFlowInputs(int flowIndex)
    {
        currentFlow=flows.get(flowIndex);
        return currentFlow.getInputList();
    }


    @Override
    public ResultDTO processInput(String inputName, String data)
    {
        return currentFlow.processInput(inputName,data);
    }


    @Override
    public boolean IsFlowReady()
    {
        return currentFlow.isFlowReady();
    }


    @Override
    public String runFlow()
    {
        String res=currentFlow.executeFlow();
        addFlowHistory();
        addStatistics();
        currentFlow.resetFlow();
        return res;
    }


    @Override
    public List<String> getInitialHistoryList()
    {
        List<String> res = new ArrayList<>();
        for(FlowHistory history: flowsHistory)
        {
            String currHistory = "Flow name: " + history.getFlowName() + "\nFlow ID: " + history.getID() + "\nFlow activation time: " + history.getActivationTime() +"\n";
            res.add(currHistory);
        }
        return res;
    }


    @Override
    public String getFullHistoryData(int flowIndex)
    {
        return flowsHistory.get(flowIndex).getFullData();
    }


    @Override
    public String getStatistics()
    {
        String res="The Statistics of the flows: \n";
        res+=getFlowsStatistics()+"\nThe Statistics of the steps: \n"+getStepsStatistics();
        return res;
    }


    @Override
    public ResultDTO saveDataOfSystemToFile(String FILE_NAME)
    {
        try(ObjectOutputStream out =
                    new ObjectOutputStream(
                            new FileOutputStream(FILE_NAME)))
        {
            out.writeObject(flows);
            out.writeObject(flowsHistory);
            out.writeObject(flowsStatistics);
            out.writeObject(stepsStatistics);
            out.flush();
            return new ResultDTO(true,"System's parameters saved successfully");
        }
        catch (Exception e)
        {
            return new ResultDTO(false,"the System's parameters were not saved successfully " +
                    "because: "+e.getMessage());
        }
    }

    @Override
    public ResultDTO loadDataOfSystemFromFile(String FILE_NAME)
    {
        File file = new File(FILE_NAME);
        Manager manager = null;
        if(file.exists())
        {
            try(ObjectInputStream in =
                        new ObjectInputStream(
                                new FileInputStream(FILE_NAME)))
            {
                flows = (List<Flow>) in.readObject();
                flowsHistory=(List<FlowHistory>)  in.readObject();
                flowsStatistics=(Map<String, Statistics>) in.readObject();
                stepsStatistics=(Map<String, Statistics>) in.readObject();

                return new ResultDTO(true,"Loaded the system successfully");
            }
            catch (Exception e)
            {
                return new ResultDTO(false,"Failed to load data from the given file " +
                        "because: "+e.getMessage());
            }
        }
        return new ResultDTO(false,"The file in the given path dont exist");
    }


    public void addFlow(Flow flow)
    {
        flows.add(flow);
    }


    private void addFlowHistory()
    {
        FlowHistory flowHistory=new FlowHistory(currentFlow.getName(),
                currentFlow.getFlowId(),currentFlow.getActivationTime(),currentFlow.getFlowHistoryData());
        flowsHistory.add(0,flowHistory);
    }


    private String getFlowsStatistics()
    {
        String currFlowStatistics,res="";
        Statistics statistics;

        for(String flowName:flowsStatistics.keySet())
        {
            statistics=flowsStatistics.get(flowName);
            currFlowStatistics = flowName+ "\nNumber of times activated: "
                    +statistics.getTimesActivated()+ "\nAverage run time: " +statistics.getAvgRunTime()+"\n\n";
            res+=currFlowStatistics;
        }

        return res;
    }


    private String getStepsStatistics()
    {
        String currFlowStatistics,res="";
        Statistics statistics;
        for(String stepName:stepsStatistics.keySet())
        {
            statistics=stepsStatistics.get(stepName);
            currFlowStatistics = stepName+ "\nNumber of times activated: "
                    +statistics.getTimesActivated()+ "\nAverage run time: " +statistics.getAvgRunTime()+"\n\n";
            res+=currFlowStatistics;
        }
        return res;
    }

    private void addStatistics()
    {
        Integer size=currentFlow.getNumberOfSteps();
        Statistics statistics=flowsStatistics.get(currentFlow.getName());
        statistics.addRunTime(currentFlow.getRunTime());
        boolean flowStopped = false;

        for(int i=0;i<size && !flowStopped;i++)
        {
            Step step =currentFlow.getStep(i);
            statistics=stepsStatistics.get(step.getDefaultName());
            statistics.addRunTime(step.getRunTime());
            if(step.getState_after_run() == State.FAILURE && !step.isContinue_if_failing())
                flowStopped = true;
        }
    }

    private File checkXMLPathAndGetFile(String path)
    {
        File file = null;
        if(!path.endsWith(".xml")) {
            throw new XmlFileException("The given file does not end with an extension xml");
        }
        else {
            file = new File(path);
            if (!file.exists()) {
                throw new XmlFileException("The given file does not exist in the given path");
            }
        }
        return file;
    }







}
