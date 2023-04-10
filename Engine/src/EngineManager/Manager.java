package EngineManager;

import Flow.Flow;
import Flow.FlowHistory;

import java.util.ArrayList;
import java.util.List;


public class Manager {
    private List<Flow> flows;
    private List<FlowHistory> flowsHistory;

    public Manager(List<Flow> flows, List<FlowHistory> flowsHistory) {
        this.flows = flows;
        this.flowsHistory = flowsHistory;
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
