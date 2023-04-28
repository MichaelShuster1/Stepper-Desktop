package dto;

public class StatisticsUnitDTO {
    private final int amountTimesActivated;
    private final double averageRunTime;
    private final String name;

    public StatisticsUnitDTO(int amountTimesActivated, double averageRunTime, String name) {
        this.amountTimesActivated = amountTimesActivated;
        this.averageRunTime = averageRunTime;
        this.name = name;
    }

    public int getAmountTimesActivated() {
        return amountTimesActivated;
    }

    public double getAverageRunTime() {
        return averageRunTime;
    }

    public String getName() {
        return name;
    }
}
