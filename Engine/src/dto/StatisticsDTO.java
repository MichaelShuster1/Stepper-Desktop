package dto;

import java.util.List;

public class StatisticsDTO {
    private final List<StatisticsUnitDTO> flowsStatistics;
    private final List<StatisticsUnitDTO> stepsStatistics;

    public StatisticsDTO(List<StatisticsUnitDTO> flowsStatistics, List<StatisticsUnitDTO> stepsStatistics) {
        this.flowsStatistics = flowsStatistics;
        this.stepsStatistics = stepsStatistics;
    }

    public List<StatisticsUnitDTO> getFlowsStatistics() {
        return flowsStatistics;
    }

    public List<StatisticsUnitDTO> getStepsStatistics() {
        return stepsStatistics;
    }
}
