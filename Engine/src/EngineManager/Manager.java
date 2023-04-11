package EngineManager;

import Flow.Flow;
import Flow.FlowHistory;

import java.util.*;


public class Manager
{
    private List<Flow> flows;
    private List<FlowHistory> flowsHistory;
    private Map<String,Statistics> flowsStatistics;
    private Map<String,Statistics> stepsStatistics;

    public Manager(List<Flow> flows, List<FlowHistory> flowsHistory)
    {
        this.flows = flows;
        this.flowsHistory = flowsHistory;
        flowsStatistics=new LinkedHashMap<>();
        stepsStatistics=new LinkedHashMap<>();
    }

    public void addFlow(Flow flow)
    {
        flows.add(flow);
    }

    public void addFlowHistory(FlowHistory history)
    {
        flowsHistory.add(history);
    }

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






    public String getFullHistoryData(int index)
    {
      return flowsHistory.get(index).getFullData();
    }

    public List<String> getAllFlowsNames()
    {
        List<String> namesList = new ArrayList<>();
        for(Flow flow: flows)
        {
            namesList.add(flow.getName());
        }
        return namesList;
    }




}
