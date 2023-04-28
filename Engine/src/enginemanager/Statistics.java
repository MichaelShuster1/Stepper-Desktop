package enginemanager;

import java.io.Serializable;

public class Statistics implements Serializable {
    private Integer amountTimesActivated;
    private Long sumOfRunTime;

    public Statistics() {
        amountTimesActivated = 0;
        sumOfRunTime = 0L;
    }

    public void addRunTime(Long runTIme) {
        sumOfRunTime += runTIme;
        amountTimesActivated++;
    }

    public Integer getTimesActivated() {
        return amountTimesActivated;
    }

    public Double getAvgRunTime() {
        if (amountTimesActivated == 0)
            return 0.0;
        return  ((double) sumOfRunTime / amountTimesActivated);
    }
}
