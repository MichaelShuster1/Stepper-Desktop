package EngineManager;

import DTO.InputsDTO;
import DTO.ResultDTO;
import Flow.Flow;
import Flow.FlowHistory;
import Steps.State;
import Steps.Step;

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
        stepsStatistics=new LinkedHashMap<>();
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
    public String loadXmlFile(String path) {
        return null;
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
            //out.writeObject(this);
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
}
