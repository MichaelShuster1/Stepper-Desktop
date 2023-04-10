package Flow;

import Steps.StepStatistics;

import java.util.List;

public class FlowStatistics
{
    String name;
    Integer amount_times_activated;
    Long sum_of_run_time;
    List<StepStatistics> stepStatisticsList;


    public FlowStatistics(String name)
    {
        this.name=name;
        amount_times_activated=0;
        sum_of_run_time=0L;
    }

    public Double getAvgRunTime()
    {
        return (double) (sum_of_run_time/amount_times_activated);
    }

    public void addRunTime(Long runTIme)
    {
        sum_of_run_time+=runTIme;
        amount_times_activated++;
    }

    public void StepStatistics(StepStatistics stepStatistics)
    {
        stepStatisticsList.add(stepStatistics);
    }



}
