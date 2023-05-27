package controllers.flowexecution;


import controllers.AppController;
import datadefinition.Relation;
import dto.*;
import elementlogic.ElementLogic;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExecutionController {

    @FXML
    private FlowPane mandatoryInputsView;

    @FXML
    private FlowPane optionalInputsView;

    @FXML
    private Button executeButton;

    @FXML
    private ChoiceBox<String> choiceBoxView;

    @FXML
    private VBox elementDetailsView;

    @FXML
    private VBox elementChoiceView;

    @FXML
    private ProgressBar progressBarView;

    private AppController appController;

    private EngineApi engine;

    private List<Button> mandatoryInputButtons;

    private List<Button> optionalInputButtons;

    private ElementLogic elementLogic;


    @FXML
    public void initialize() {
    }



    public void setAppController(AppController appController)
    {
        this.appController=appController;
    }

    public void setStage(Stage stage)
    {
        elementLogic=new ElementLogic(elementChoiceView,elementDetailsView,stage);
    }

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    public void setTabView(InputsDTO inputsDTO)
    {
        clearTab();
        int numberOfMandatoryInputs=0;
        int numberOfInsertedMandatoryInputs=0;

        mandatoryInputButtons=new ArrayList<>();
        optionalInputButtons=new ArrayList<>();

        for(int i=0;i<inputsDTO.getNumberOfInputs();i++)
        {
            InputData inputData=inputsDTO.getFreeInput(i);
            Button button=new Button();
            button.setId(inputData.getSystemName());
            button.setOnAction(e->inputClick(button,new ActionEvent()));
            button.setText(inputData.getUserString());
            FlowPane.setMargin(button,new Insets(0,10,10,0));

            if(inputData.getNecessity())
            {
                button.setStyle("-fx-background-color: #ff0000; ");
                mandatoryInputsView.getChildren().add(button);
                mandatoryInputButtons.add(button);
                numberOfMandatoryInputs++;
            }
            else {
                optionalInputsView.getChildren().add(button);
                optionalInputButtons.add(button);
            }

            if (inputData.IsInserted()) {
                button.setStyle("-fx-background-color: #40ff00; ");
                numberOfInsertedMandatoryInputs++;
            }
        }
        executeButton.setDisable(numberOfInsertedMandatoryInputs != numberOfMandatoryInputs);
    }

    public void clearTab() {
        mandatoryInputsView.getChildren().clear();
        optionalInputsView.getChildren().clear();
        choiceBoxView.getItems().clear();
        elementLogic.clear();
        progressBarView.setProgress(0);
    }


    @FXML
    public void inputClick(Button button,ActionEvent event)
    {

        TextInputDialog inputDialog = new TextInputDialog();


        inputDialog.setTitle("submit input");
        inputDialog.setHeaderText(null);
        inputDialog.setGraphic(null);
        inputDialog.setContentText("please enter the input here:");

        Optional<String> result =inputDialog.showAndWait();
        if(result.isPresent())
        {
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
        elementDetailsView.getChildren().clear();
        String flowId=engine.runFlow();
        appController.addFlowId(flowId);
        executeButton.setDisable(true);

        for(Button button:mandatoryInputButtons)
            button.setStyle("-fx-background-color: #ff0000; ");
        for (Button button:optionalInputButtons)
            button.setStyle("");


    }

    public void updateProgressFlow(FlowExecutionDTO flowExecutionDTO)
    {
        elementLogic.setElementDetailsView(flowExecutionDTO);
        progressBarView.setProgress(flowExecutionDTO.getProgress());
        if(flowExecutionDTO.getStateAfterRun()!=null)
        {
            ContinutionMenuDTO continutionMenuDTO=engine.getContinutionMenuDTO();
            if(continutionMenuDTO!=null)
            {
                List<String> targetFlows = continutionMenuDTO.getTargetFlows();
                choiceBoxView.setItems(FXCollections.observableArrayList(targetFlows));
            }
            progressBarView.setProgress(1);
        }

    }



    @FXML
    void showFlowInfo(MouseEvent event) {
        elementLogic.updateFlowInfoView();
    }

    @FXML
    void continueToFlow(ActionEvent event) {
        System.out.println(choiceBoxView.getValue());
    }

}
