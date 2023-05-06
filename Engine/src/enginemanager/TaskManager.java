package enginemanager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TaskManager implements Runnable{

    List<Future> currentFlows;

    public TaskManager(List<Future> currentFlows) {
        this.currentFlows = currentFlows;
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
               }
           }

           if(currentFlows.size() == 0) {
               //System.out.println("No flows....going to sleep zzz :(");
               try {
                   Thread.sleep(1000);
               } catch (Exception e) {

               }
           }
           // if(!found)
                //System.out.println("Still executing...");
        }

    }
}
