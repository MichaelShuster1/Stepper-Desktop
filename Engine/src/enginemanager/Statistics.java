package enginemanager;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Statistics implements Serializable {
    private Integer amountTimesActivated;
    private Long sumOfRunTime;
    private final static DecimalFormat decimalFormat = new DecimalFormat("#.##");

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
            return 0.00;
        double number= ((double) sumOfRunTime / amountTimesActivated);
        String formattedNumber = decimalFormat.format(number);
        return  Double.parseDouble(formattedNumber);
    }
}
