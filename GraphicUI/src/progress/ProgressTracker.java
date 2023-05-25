package progress;

import controllers.AppController;
import dto.FlowExecutionDTO;
import enginemanager.EngineApi;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressTracker extends Task<Boolean> {

    List<String> flowsId;

    AppController appController;

    EngineApi engine;

    public ProgressTracker(AppController appController,EngineApi engine)
    {
        flowsId=new HashSet<>();
        this.appController=appController;
        this.engine=engine;
    }

    public void addFlowId(String id)
    {
        synchronized (flowsId) {
            flowsId.add(id);
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


                    if(flowExecutionDTO.getStateAfterRun() != null) {
                        Platform.runLater(() -> appController.updateStatistics());
                        Platform.runLater(()->appController.addRowInHistoryTable(flowExecutionDTO));
                        flowsId.remove(i);
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        return true;
    }
}
