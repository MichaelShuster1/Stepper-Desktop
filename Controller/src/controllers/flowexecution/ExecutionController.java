package controllers.flowexecution;


import controllers.AppController;
import dto.InputData;
import dto.InputsDTO;
import dto.ResultDTO;
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
    private FlowPane inputsView;

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
        List<Button> buttons=new ArrayList<>();
        for(int i=0;i<inputsDTO.getNumberOfInputs();i++)
        {
            InputData inputData=inputsDTO.getFreeInput(i);
            Button button=new Button();
            button.setId(inputData.getSystemName());
            FlowPane.setMargin(button,new Insets(0,10,10,0));
            if(inputData.getNecessity())
                button.setStyle("-fx-background-color: #ff0000; ");
            button.setOnAction(e->inputClick(button,new ActionEvent()));
            button.setText(inputData.getUserString());
            buttons.add(button);
        }
        inputsView.getChildren().addAll(buttons);
    }

    @FXML
    public void inputClick(Button button,ActionEvent event)
    {
        System.out.println("input click");

        TextInputDialog inputDialog = new TextInputDialog();

        inputDialog.setTitle("Submit input");
        inputDialog.setHeaderText(null);
        inputDialog.setGraphic(null);
        inputDialog.setContentText("please enter the input here:");

        Optional<String> result =inputDialog.showAndWait();
        if(result.isPresent())
        {
            System.out.println(result.get());
            ResultDTO resultDTO=engine.processInput(button.getId(),result.get());
            if(resultDTO.getStatus())
                button.setStyle("-fx-background-color: #40ff00; ");
            else
            {
                Alert alert =new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(resultDTO.getMessage());
            }
        }
    }





}
