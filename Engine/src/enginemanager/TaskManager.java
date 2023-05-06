package enginemanager;

import flow.FlowExecution;
import flow.FlowHistory;

import java.util.List;
import java.util.concurrent.Future;

public class TaskManager implements Runnable{

    private List<Future> currentFlows;

    private List<FlowExecution> flowExecutions;

    private List<FlowHistory> flowsHistory;


    public TaskManager(List<Future> currentFlows, List<FlowExecution> flowExecutions, List<FlowHistory> flowsHistory) {
        this.currentFlows = currentFlows;
        this.flowExecutions = flowExecutions;
        this.flowsHistory = flowsHistory;
    }


    @Override
    public void run() {
        boolean found = false;
        while(true) {
           for(int i=0;i<currentFlows.size();i++) {
               if(currentFlows.get(i).isDone()) {
                   found = true;
                   System.out.println("flow is finished");
                   currentFlows.remove(i);
                   addFlowHistory(flowExecutions.get(i));
                   flowExecutions.remove(i);
               }
           }
           if(currentFlows.size() == 0) {
               //System.out.println("No flows....going to sleep zzz :(");
               try {
                   Thread.sleep(1000);
               } catch (Exception ignored) {

               }
           }
           // if(!found)
                //System.out.println("Still executing...");
        }

    }


    private void addFlowHistory(FlowExecution currentFlow) {
        FlowHistory flowHistory = new FlowHistory(currentFlow.getName(),
                currentFlow.getFlowId(), currentFlow.getActivationTime(), currentFlow.getFlowHistoryData());
        flowsHistory.add(0, flowHistory);
    }
}
