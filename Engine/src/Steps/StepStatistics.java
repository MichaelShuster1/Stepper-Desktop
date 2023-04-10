package Steps;

public class StepStatistics
{
    String name;
    Integer amount_times_activated;
    Long sum_of_run_time;
    Double avg_run_time;


    public StepStatistics(String name)
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
}
