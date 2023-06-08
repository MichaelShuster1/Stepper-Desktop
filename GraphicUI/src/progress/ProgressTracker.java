package progress;

import controllers.AppController;
import dto.FlowExecutionDTO;
import enginemanager.EngineApi;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressTracker extends Task<Boolean> {

    final List<String> flowsId;

    String currentFlowId;

    AppController appController;

    EngineApi engine;

    public ProgressTracker(AppController appController,EngineApi engine)
    {
        flowsId=new ArrayList<>();
        this.appController=appController;
        this.engine=engine;
    }

    public void addFlowId(String id)
    {
        synchronized (flowsId) {
            flowsId.add(id);
            currentFlowId = id;
        }
    }

    public void resetCurrentFlowId()
    {
        synchronized (flowsId) {
            currentFlowId = null;
        }
    }

    public boolean finishedFollowingLastActivatedFlow()
    {
        synchronized (flowsId){
            return (!flowsId.contains(currentFlowId) && currentFlowId!=null);
        }
    }

    public boolean areFlowsRunning()
    {
        synchronized (flowsId){
            return (flowsId.size()!=0);
        }
    }

    @Override
    protected Boolean call()  {
        while (appController!=null)
        {
            synchronized (flowsId) {
                for (int i = 0;i<flowsId.size();i++) {
                    String flowId = flowsId.get(i);
                    FlowExecutionDTO flowExecutionDTO=engine.getHistoryDataOfFlow(flowId);

                    if(flowId.equals(currentFlowId)) {
                        Platform.runLater(()->appController.updateProgressFlow(flowExecutionDTO));
                    }


                    if(flowExecutionDTO.getStateAfterRun() != null) {
                        Platform.runLater(() -> appController.updateStatistics());
                        Platform.runLater(()->appController.addRowInHistoryTable(flowExecutionDTO));
                        flowsId.remove(i);
                    }
                }
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }
}
