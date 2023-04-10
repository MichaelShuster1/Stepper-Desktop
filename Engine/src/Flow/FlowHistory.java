package Flow;

public class FlowHistory {
    String flowName;
    String ID;
    String activationTime;
    String fullData;

    public FlowHistory(String flowName, String ID, String activationTime, String fullData) {
        this.flowName = flowName;
        this.ID = ID;
        this.activationTime = activationTime;
        this.fullData = fullData;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(String activationTime) {
        this.activationTime = activationTime;
    }

    public String getFullData() {
        return fullData;
    }

    public void setFullData(String fullData) {
        this.fullData = fullData;
    }
}
