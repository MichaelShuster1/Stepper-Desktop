package controllers.history;

import controllers.AppController;
import dto.FlowExecutionDTO;
import dto.FreeInputExecutionDTO;
import dto.OutputExecutionDTO;
import dto.StepExecutionDTO;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.List;

public class HistoryController {

    @FXML
    private Label stepDetailsView;

    @FXML
    private Text flowInfoView;

    @FXML
    private TitledPane stepsPaneView;

    @FXML
    private StackPane stackTableView;

    @FXML
    private ChoiceBox<String> stateFilterView;

    private AppController appController;

    private EngineApi engine;


    private TableView<FlowExecutionDTO> historyTableView;

    private ObservableList<FlowExecutionDTO> tableData;

    private TableColumn<FlowExecutionDTO,String> flowNameColumnView;

    private TableColumn<FlowExecutionDTO,String> activationTimeColumnView;

    private TableColumn<FlowExecutionDTO,String> flowStateColumnView;


    private TableView<StepExecutionDTO> stepsTableView;

    private TableColumn<StepExecutionDTO,String> stepColumnView;

    private TableColumn<StepExecutionDTO,String> stateColumnView;




    @FXML
    public void initialize() {

        historyTableView=new TableView<>();
        flowNameColumnView=new TableColumn<>("flow name");
        activationTimeColumnView=new TableColumn<>("activation time");
        flowStateColumnView =new TableColumn<>("state after run");

        flowNameColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        flowStateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));
        activationTimeColumnView.setCellValueFactory(new PropertyValueFactory<>("activationTime"));

        historyTableView.getColumns().addAll(flowNameColumnView,activationTimeColumnView, flowStateColumnView);
        historyTableView.getColumns().forEach(column -> column.setMinWidth(200));
        historyTableView.setOnMouseClicked(e-> HistoryTableRowClick(new ActionEvent()));
        stackTableView.getChildren().add(historyTableView);

        stepsTableView=new TableView<>();
        stepColumnView=new TableColumn<>("step");
        stateColumnView=new TableColumn<>("state");

        stepColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));

        stepsTableView.setOnMouseClicked(e->stepTableRowClick(new ActionEvent()));
        stepsTableView.getColumns().addAll(stepColumnView,stateColumnView);
        tableData = FXCollections.observableArrayList();
        historyTableView.setItems(tableData);

        stateFilterView.getItems().addAll("NONE", "SUCCESS", "WARNING", "FAILURE");
        stateFilterView.setValue("NONE");
        stateFilterView.setOnAction(event -> filterTable());
    }



    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    @FXML
    void reRunFlow(ActionEvent event) {
        if(!historyTableView.getSelectionModel().isEmpty()) {
            FlowExecutionDTO flowExecutionDTO = historyTableView.getSelectionModel().getSelectedItem();
            engine.reUseInputsData(flowExecutionDTO);
            int index=engine.getFlowIndexByName(flowExecutionDTO.getName());
            appController.streamFlow(index);
        }

    }

    @FXML
    private void HistoryTableRowClick(ActionEvent event) {

        if(!historyTableView.getSelectionModel().isEmpty()) {
            FlowExecutionDTO flowExecutionDTO = historyTableView.getSelectionModel().getSelectedItem();
            flowInfoView.setText(getFlowHistoryData(flowExecutionDTO));
            stepsTableView.setItems(FXCollections.observableArrayList(flowExecutionDTO.getSteps()));
            stepsPaneView.setContent(stepsTableView);
        }
    }

    @FXML
    private void stepTableRowClick(ActionEvent event)
    {
        if(!stepsTableView.getSelectionModel().isEmpty()) {
            StepExecutionDTO stepExecutionDTO = stepsTableView.getSelectionModel().getSelectedItem();
            String details = "Name: " + stepExecutionDTO.getName() + "\n";
            details += "Run time: " + stepExecutionDTO.getRunTime() + " ms\n";
            details += "Finish state: " + stepExecutionDTO.getStateAfterRun() + "\n";
            details += "STEP LOGS:\n\n";
            details += getStrLogs(stepExecutionDTO.getLogs());
            stepDetailsView.setText(details);
        }
    }


    public void addRow(FlowExecutionDTO flowExecutionDTO)
    {
        tableData.add(flowExecutionDTO);
    }

    public void filterTable()
    {
        String choice = stateFilterView.getValue();
        FilteredList<FlowExecutionDTO> filteredData = new FilteredList<>(tableData);
        switch(choice) {
            case "NONE":
                historyTableView.setItems(tableData);
                break;
            case "SUCCESS":
                filteredData.setPredicate(item -> {
                    return item.getStateAfterRun().equals("SUCCESS");
                });
                historyTableView.setItems(filteredData);
                break;
            case "FAILURE":
                filteredData.setPredicate(item -> {
                    return item.getStateAfterRun().equals("FAILURE");
                });
                historyTableView.setItems(filteredData);
                break;
            case "WARNING":
                filteredData.setPredicate(item -> {
                    return item.getStateAfterRun().equals("WARNING");
                });
                historyTableView.setItems(filteredData);
                break;

        }
        historyTableView.refresh();
    }


    private String getFlowHistoryData(FlowExecutionDTO flowExecutionDTO) {
        String res = getFlowNameIDAndState(flowExecutionDTO);
        String temp;

        res += "Flow total run time: " + flowExecutionDTO.getRunTime() + " ms\n\n";
        res += "------------------------------\n";
        res += "FREE INPUTS THAT RECEIVED DATA:\n\n";
        temp = getFreeInputsHistoryData(flowExecutionDTO.getFreeInputs(),true);
        temp += getFreeInputsHistoryData(flowExecutionDTO.getFreeInputs(),false);
        if (temp.length() == 0)
            res += "NO FREE INPUTS HAVE RECEIVED DATA\n\n";
        else
            res += temp;
        res += "------------------------------\n";
        res += "DATA PRODUCED (OUTPUTS):\n\n";
        temp = getOutputsHistoryData(flowExecutionDTO.getOutputs());
        if (temp.length() == 0)
            res += "NO DATA WAS PRODUCED\n\n";
        else
            res += temp;
        res += "------------------------------\n";
        res += "FLOW STEPS DATA:\n\n";
        res += getStepsHistoryData(flowExecutionDTO.getSteps());

        return res;
    }


    private String getFlowNameIDAndState(FlowExecutionDTO flowExecutionDTO) {
        String res = "FLOW EXECUTION DATA:\n";
        res += "Flows unique ID: " + flowExecutionDTO.getId() + "\n";
        res += "Flow name: " + flowExecutionDTO.getName() + "\n";
        res += "Flow's final state : " + flowExecutionDTO.getStateAfterRun() + "\n";
        return res;
    }


    private String getFreeInputsHistoryData(List<FreeInputExecutionDTO> flowFreeInputs, boolean mandatoryOrNot) {
        String res = "";
        String currInput;
        for (FreeInputExecutionDTO freeInput : flowFreeInputs) {
            if (freeInput.getData() != null) {
                currInput = "Name: " + freeInput.getName() + "\n";
                currInput += "Type: " + freeInput.getType() + "\n";
                if(freeInput.getType().equals("List") || freeInput.getType().equals("Relation") || freeInput.getType().equals("Mapping"))
                    currInput += "Input data:\n" + freeInput.getData() + "\n";
                else
                    currInput += "Input data: " + freeInput.getData() + "\n";
                if (freeInput.isMandatory())
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";

                if (mandatoryOrNot && freeInput.isMandatory())
                    res += currInput;
                else if (!mandatoryOrNot && !freeInput.isMandatory())
                    res += currInput;
            }
        }

        return res;
    }

    private String getOutputsHistoryData(List<OutputExecutionDTO> outputs) {
        String res = "";

        for (OutputExecutionDTO output : outputs) {
            res += "Name: " + output.getName() + "\n";
            res += "Type: " + output.getType() + "\n";
            if (output.getData() != null) {
                if(output.getType().equals("List") || output.getType().equals("Relation") || output.getType().equals("Mapping"))
                    res += "Data:\n" + output.getData() + "\n\n";
                else
                    res += "Data: " + output.getData() + "\n\n";

            }
            else
                res += "Data: Not created due to failure in flow\n\n";

        }
        return res;
    }


    private String getStepsHistoryData(List<StepExecutionDTO> steps) {
        String res = "";
        for (StepExecutionDTO step: steps) {
            res += "Name: " + step.getName() + "\n";
            res += "Run time: " + step.getRunTime() + " ms\n";
            res += "Finish state: " + step.getStateAfterRun() + "\n";
            res += "Step summary:" + step.getSummaryLine()+ "\n";
            res += "STEP LOGS:\n\n";
            res += getStrLogs(step.getLogs());
            res += "------------------------------\n";
        }
        return res;
    }


    private String getStrLogs(List<String> logs) {
        String res = "";
        if (logs.size() == 0)
            return "The step had no logs\n\n";
        else {
            for (String currLog : logs) {
                res += currLog + "\n\n";
            }
        }
        return res;
    }
}
