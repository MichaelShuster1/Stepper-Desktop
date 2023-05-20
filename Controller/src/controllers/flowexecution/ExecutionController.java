package controllers.flowexecution;


import controllers.AppController;
import dto.*;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class ExecutionController {

    @FXML
    private FlowPane mandatoryInputsView;

    @FXML
    private FlowPane optionalInputsView;

    @FXML
    private Button executeButton;

    @FXML
    private TitledPane stepsTitleVIew;

    @FXML
    private Label stepDetailsView;

    @FXML
    private Text flowInfoView;

    @FXML
    private ChoiceBox<String> choiceBoxView;



    private TableView<StepExecutionDTO> stepsTableView;

    private TableColumn<StepExecutionDTO,String> stepColumnView;

    private TableColumn<StepExecutionDTO,String> stateColumnView;


    private AppController appController;

    private EngineApi engine;

    private ObservableList<StepExecutionDTO> observableList= FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        stepsTableView=new TableView<>();
        stepColumnView=new TableColumn<>("step");
        stateColumnView=new TableColumn<>("state");

        stepColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));

        stepsTableView.getColumns().addAll(stepColumnView,stateColumnView);
    }



    public void setAppController(AppController appController)
    {
        this.appController=appController;
    }

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    public void setTabView(InputsDTO inputsDTO)
    {
        mandatoryInputsView.getChildren().clear();
        optionalInputsView.getChildren().clear();
        choiceBoxView.getItems().clear();
        stepsTableView.getItems().clear();
        stepDetailsView.setText(null);

        for(int i=0;i<inputsDTO.getNumberOfInputs();i++)
        {
            InputData inputData=inputsDTO.getFreeInput(i);
            Button button=new Button();
            button.setId(inputData.getSystemName());
            FlowPane.setMargin(button,new Insets(0,10,10,0));

            if(inputData.getNecessity())
            {
                button.setStyle("-fx-background-color: #ff0000; ");
                mandatoryInputsView.getChildren().add(button);
            }
            else
                optionalInputsView.getChildren().add(button);
            button.setOnAction(e->inputClick(button,new ActionEvent()));
            button.setText(inputData.getUserString());
        }
    }

    @FXML
    public void inputClick(Button button,ActionEvent event)
    {
        System.out.println("input click");

        TextInputDialog inputDialog = new TextInputDialog();

        inputDialog.setTitle("submit input");
        inputDialog.setHeaderText(null);
        inputDialog.setGraphic(null);
        inputDialog.setContentText("please enter the input here:");

        Optional<String> result =inputDialog.showAndWait();
        if(result.isPresent())
        {
            System.out.println(result.get());
            ResultDTO resultDTO=engine.processInput(button.getId(),result.get());
            if(resultDTO.getStatus())
            {
                button.setStyle("-fx-background-color: #40ff00; ");
                if(engine.isFlowReady())
                    executeButton.setDisable(false);
            }
            else
            {
                Alert alert =new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(resultDTO.getMessage());
                alert.showAndWait();
            }
        }
    }



    @FXML
    private void executeFlow(ActionEvent event)
    {
        System.out.println("execute click");
        System.out.println(getFlowExecutionStrData(engine.runFlow()));
        FlowExecutionDTO flowExecutionDTO =engine.getFullHistoryData(0);
        flowInfoView.setText(getFlowHistoryData(flowExecutionDTO));
        appController.addRowInHistoryTable(flowExecutionDTO);
        appController.updateStatistics();

        observableList.addAll(flowExecutionDTO.getSteps());
        stepsTableView.setOnMouseClicked(e->rowClick(new ActionEvent()));
        stepsTableView.setItems(observableList);
        stepsTitleVIew.setContent(stepsTableView);

        ContinutionMenuDTO continutionMenuDTO=engine.getContinutionMenuDTO();
        if(continutionMenuDTO!=null)
        {
            List<String> targetFlows = continutionMenuDTO.getTargetFlows();
            choiceBoxView.setItems(FXCollections.observableArrayList(targetFlows));
        }
    }


    @FXML
    private void rowClick(ActionEvent event)
    {
        StepExecutionDTO stepExecutionDTO=stepsTableView.getSelectionModel().getSelectedItem();
        String details = "Name: " + stepExecutionDTO.getName() + "\n";
        details += "Run time: " + stepExecutionDTO.getRunTime() + " ms\n";
        details += "Finish state: " + stepExecutionDTO.getStateAfterRun() + "\n";
        details+= "STEP LOGS:\n\n";
        details += getStrLogs(stepExecutionDTO.getLogs());
        stepDetailsView.setText(details);
    }


    @FXML
    void continueToFlow(ActionEvent event) {
        System.out.println(choiceBoxView.getValue());
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


    private String getFreeInputsHistoryData(List<FreeInputExecutionDTO> flowFreeInputs,boolean mandatoryOrNot) {
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


    public String getFlowExecutionStrData(FlowResultDTO flowResult) {
        String res = "Flows unique ID: " + flowResult.getId() + "\n";
        res += "Flow name: " + flowResult.getName() + "\n";
        res += "Flow's final state : " + flowResult.getState() + "\n";
        List<OutputExecutionDTO> formalOutputs = flowResult.getFormalOutputs();
        if (formalOutputs.size() > 0) {
            res += "FLOW'S FORMAL OUTPUTS:\n";
            for (OutputExecutionDTO output: formalOutputs) {
                res += output.getType() + "\n";
                if (output.getData() != null)
                    res += output.getData() + "\n";
                else
                    res += "Not created due to failure in flow\n";

            }
        } else {
            res += "THE FLOW HAVE NO OUTPUTS\n";
        }
        return res;
    }




}
