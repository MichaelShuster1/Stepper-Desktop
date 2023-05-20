package controllers.flowexecution;


import controllers.AppController;
import dto.*;
import enginemanager.EngineApi;
import flow.Flow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExecutionController {

    @FXML
    private FlowPane mandatoryInputsView;

    @FXML
    private FlowPane optionalInputsView;

    @FXML
    private Button executeButton;

    private AppController appController;

    private EngineApi engine;


    @FXML
    public void initialize() {
    }



    public void setAppController(AppController appController)
    {
        this.appController=appController;
    }

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    public void setInputsView(InputsDTO inputsDTO)
    {
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
    void executeFlow(ActionEvent event)
    {
        System.out.println("execute click");
        System.out.println(getFlowExecutionStrData(engine.runFlow()));
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
