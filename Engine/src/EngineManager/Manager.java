package EngineManager;

import Flow.Flow;
import Flow.FlowHistory;

import java.util.*;


public class Manager implements EngineApi
{
    private List<Flow> flows;
    private List<FlowHistory> flowsHistory;
    private Map<String,Statistics> flowsStatistics;
    private Map<String,Statistics> stepsStatistics;
    private Flow currentFlow;

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
    public List<String> getFlowInputs(int flowIndex)
    {
        currentFlow=flows.get(flowIndex);
        return currentFlow.getInputList();
    }


    @Override
    public boolean processInput(String inputName, String data)
    {
        return currentFlow.processInput(inputName,data);
    }


    @Override
    public String runFlow()
    {
        return currentFlow.executeFlow();
    }


    @Override
    public List<String> getInitialHistoryList()
    {
        List<String> res = new ArrayList<>();
        for(FlowHistory history: flowsHistory)
        {
            String currHistory = "Flow name:" + history.getFlowName() + "\n Flow ID: " + history.getID() + "\n Flow activation time: " + history.getActivationTime() +"\n";
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
    public List<String> getStatistics()
    {
        return null;
    }


    public void addFlow(Flow flow)
    {
        flows.add(flow);
    }


    public void addFlowHistory(FlowHistory history)
    {
        flowsHistory.add(history);
    }

    public List<String> getFlowsStatistics()
    {
        List<String> res=new ArrayList<>();
        String currFlowStatistics;
        Statistics statistics;

        for(String flowName:flowsStatistics.keySet())
        {
            statistics=flowsStatistics.get(flowName);
            currFlowStatistics = flowName+ "\nNumber of times activated: "
                    +statistics.getTimesActivated()+ "\nAverage run time: " +statistics.getAvgRunTime();
            res.add(currFlowStatistics);
        }

        return res;
    }


    public List<String> getStepsStatistics()
    {
        List<String> res=new ArrayList<>();
        String currFlowStatistics;
        Statistics statistics;
        for(String stepName:stepsStatistics.keySet())
        {
            statistics=stepsStatistics.get(stepName);
            currFlowStatistics = stepName+ "\nNumber of times activated: "
                    +statistics.getTimesActivated()+ "\nAverage run time: " +statistics.getAvgRunTime();
            res.add(currFlowStatistics);
        }
        return res;
    }




}
