package enginemanager;

import java.io.Serializable;

public class Statistics implements Serializable {
    private Integer amount_times_activated;
    private Long sum_of_run_time;

    public Statistics() {
        amount_times_activated = 0;
        sum_of_run_time = 0L;
    }

    public void addRunTime(Long runTIme) {
        sum_of_run_time += runTIme;
        amount_times_activated++;
    }

    public Integer getTimesActivated() {
        return amount_times_activated;
    }

    public Double getAvgRunTime() {
        if (amount_times_activated == 0)
            return 0.0;
        return (double) (sum_of_run_time / amount_times_activated);
    }
}
