package controllers.flowexecution;


import controllers.AppController;
import datadefinition.Relation;
import dto.*;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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


    private TableView<StepExecutionDTO> stepsTableView;

    private TableColumn<StepExecutionDTO,String> stepColumnView;

    private TableColumn<StepExecutionDTO,String> stateColumnView;


    private AppController appController;

    private EngineApi engine;

    private List<Button> mandatoryInputButtons;

    private List<Button> optionalInputButtons;

    private FlowExecutionDTO flowExecutionDTO;


    @FXML
    public void initialize() {
        stepsTableView=new TableView<>();
        stepColumnView=new TableColumn<>("step");
        stateColumnView=new TableColumn<>("state");

        stepColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));

        stepsTableView.getColumns().addAll(stepColumnView,stateColumnView);
        stepsTableView.setOnMouseClicked(e->rowClick(new ActionEvent()));
        stepsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        elementChoiceView.getChildren().add(stepsTableView);
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
        clearTab();

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
            }
            else {
                optionalInputsView.getChildren().add(button);
                optionalInputButtons.add(button);
            }

            if (inputData.IsInserted())
                button.setStyle("-fx-background-color: #40ff00; ");

        }
    }

    private void clearTab() {
        mandatoryInputsView.getChildren().clear();
        optionalInputsView.getChildren().clear();
        elementDetailsView.getChildren().clear();
        choiceBoxView.getItems().clear();
        stepsTableView.getItems().clear();
        flowExecutionDTO=null;
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
        this.flowExecutionDTO=flowExecutionDTO;
        ObservableList<StepExecutionDTO> items= FXCollections.observableArrayList();
        items.addAll(flowExecutionDTO.getSteps());
        stepsTableView.setItems(items);

        if(flowExecutionDTO.getStateAfterRun()!=null)
        {
            ContinutionMenuDTO continutionMenuDTO=engine.getContinutionMenuDTO();
            if(continutionMenuDTO!=null)
            {
                List<String> targetFlows = continutionMenuDTO.getTargetFlows();
                choiceBoxView.setItems(FXCollections.observableArrayList(targetFlows));
            }
        }
    }



    @FXML
    void showFlowInfo(MouseEvent event) {
        elementDetailsView.getChildren().clear();
        updateFlowInfoView();
    }

    @FXML
    private void rowClick(ActionEvent event)
    {
        if(!stepsTableView.getSelectionModel().isEmpty()) {
            elementDetailsView.getChildren().clear();
            StepExecutionDTO stepExecutionDTO=stepsTableView.getSelectionModel().getSelectedItem();
            StepExtensionDTO stepExtensionDTO =stepExecutionDTO.getStepExtensionDTO();


            addKeyValueLine("Name: ",stepExecutionDTO.getName());
            addKeyValueLine("Run Time: ",stepExecutionDTO.getRunTime()+ "ms");
            addKeyValueLine("Finish state: ",stepExecutionDTO.getStateAfterRun());
            addKeyValueLine("Step's Input Data:","");
            addStepInputsOrOutputsData(stepExtensionDTO.getInputs());
            addKeyValueLine("Step's Outputs Data:","");
            addStepInputsOrOutputsData(stepExtensionDTO.getOutputs());
            addKeyValueLine("STEP LOGS:","");
            addStepLogs(stepExtensionDTO.getLogs());


            /*
            String details = "Name: " + stepExecutionDTO.getName() + "\n";
            details += "Run time: " + stepExecutionDTO.getRunTime() + " ms\n";
            details += "Finish state: " + stepExecutionDTO.getStateAfterRun() + "\n";
            details += "Step's Inputs Data:\n";
            details += addStepInputsOrOutputsData(stepExtensionDTO.getInputs());
            details += "\n\nStep's Outputs Data:\n";
            details += addStepInputsOrOutputsData(stepExtensionDTO.getOutputs());
            details+= "STEP LOGS:\n\n";
            details += getStrLogs(stepExtensionDTO.getLogs());
            stepDetailsView.setText(details);
            */
        }
    }


    private HBox getNewHbox()
    {
        HBox hBox =new HBox();
        hBox.setAlignment(Pos.BASELINE_LEFT);
        hBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        hBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        return  hBox;
    }

    private void addKeyValueLine(String name, String value)
    {
        HBox hBox = getNewHbox();

        Label label =new Label(name);
        label.setFont(Font.font("System",FontWeight.BOLD,12));

        Text text =new Text(value);

        hBox.getChildren().add(label);
        hBox.getChildren().add(text);

        elementDetailsView.getChildren().add(hBox);
    }

    private void addKeyHyperLinkValueLine(String name, String value, Object data)
    {
        HBox hBox = getNewHbox();

        Label label =new Label(name+": ");
        label.setFont(Font.font("System",FontWeight.BOLD,12));

        Hyperlink hyperlink=new Hyperlink(value);

        switch (value)
        {
            case "Relation":
                hyperlink.setOnMouseClicked(e->relationPopUp((Relation) data));
                break;
            case "List":
                hyperlink.setOnMouseClicked(e->listPopUp((List<Object>) data));
                break;
        }



        hBox.getChildren().add(label);
        hBox.getChildren().add(hyperlink);

        elementDetailsView.getChildren().add(hBox);
    }

    private void addTitleLine(String title)
    {
        HBox hBox= getNewHbox();
        Label label =new Label(title);
        label.setFont(Font.font("System",FontWeight.BOLD,14));
        elementDetailsView.getChildren().add(label);
    }





    private void addStepInputsOrOutputsData(Map<DataDefintionDTO,Object> io)
    {
        String value="";
        for(DataDefintionDTO dataDefintionDTO:io.keySet())
        {
            String name=dataDefintionDTO.getName();
            String type=dataDefintionDTO.getType();
            Object data=io.get(dataDefintionDTO);

            if(data!=null) {
                if(type.equals("Relation")||type.equals("List"))
                    addKeyHyperLinkValueLine(name,type,data);
                else
                    addKeyValueLine(name+": ",data.toString());
            }
            else
                addKeyValueLine(name+": ","No Data Received");


        }
    }


    private void showNewPopUp(Parent root)
    {
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }


    private TableView<Map<String, String>> createTableView(Relation data)
    {
        TableView<Map<String, String>> tableView=new TableView<>();
        List<String> columnNames=data.getColumnNames();

        for(String columnName :columnNames)
        {
            TableColumn<Map<String, String>,String> columnView=new TableColumn<>(columnName);
            columnView.setCellValueFactory(new MapValueFactory(columnName));
            tableView.getColumns().add(columnView);
        }

        ObservableList<Map<String,String>> items=FXCollections.observableArrayList();
        items.addAll(data.getRows());
        tableView.setItems(items);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return  tableView;
    }

    private void relationPopUp(Relation data) {

        showNewPopUp(createTableView(data));
    }

    private void listPopUp(List<Object> data)
    {
        showNewPopUp(createListView(data));
    }


    private ListView<String> createListView(List<Object> list)
    {
        ListView<String> listView=new ListView<>();
        listView.setOrientation(Orientation.VERTICAL);
        int counter=1;
        for(Object object:list)
        {
            listView.getItems().add(counter+"."+object.toString());
            counter++;
        }
        return  listView;
    }






    private void addStepLogs(List<String> logs) {
        if (logs.size() == 0)
            addKeyValueLine( "The step had no logs","");
        else {
            for (String currLog : logs) {
                addKeyValueLine( currLog,"");
            }
        }
    }

    @FXML
    void continueToFlow(ActionEvent event) {
        System.out.println(choiceBoxView.getValue());
    }



    private void updateFlowInfoView()
    {
        if(flowExecutionDTO==null) {
            addTitleLine("The flow was not executed yet");
            return;
        }

        updateFlowNameIDAndState();
        if(flowExecutionDTO.getStateAfterRun()!=null)
            addKeyValueLine("Flow total run time: " , flowExecutionDTO.getRunTime() + " ms");
        else
            addKeyValueLine("Flow total run time: " ,  "flow is still running");
       addTitleLine("FREE INPUTS THAT RECEIVED DATA:");
       if(flowExecutionDTO.getFreeInputs().size()!=0) {
           updateFlowFreeInputs(flowExecutionDTO.getFreeInputs(), true);
           updateFlowFreeInputs(flowExecutionDTO.getFreeInputs(), false);
       }
       else
           addKeyValueLine("","NO FREE INPUTS HAVE RECEIVED DATA");
        addTitleLine("DATA PRODUCED (OUTPUTS):");
        if(flowExecutionDTO.getOutputs().size()!=0)
            updateOutputsHistoryData(flowExecutionDTO.getOutputs());
        else
            addKeyValueLine("","NO DATA WAS PRODUCED");
        addTitleLine("FLOW STEPS DATA:");
        updateStepsHistoryData(flowExecutionDTO.getSteps());

    }

    private void updateFlowNameIDAndState() {
        addTitleLine("FLOW EXECUTION DATA:");
        addKeyValueLine("Flows unique ID: ",flowExecutionDTO.getId());
        addKeyValueLine("Flow name: ",flowExecutionDTO.getName());
        if(flowExecutionDTO.getStateAfterRun()!=null)
            addKeyValueLine("Flow's final state : " , flowExecutionDTO.getStateAfterRun());
        else
            addKeyValueLine("Flow's final state : " , "flow is still running");


    }
    private void updateFlowFreeInputs(List<FreeInputExecutionDTO> flowFreeInputs,boolean mandatoryOrNot)
    {
        for (FreeInputExecutionDTO freeInput : flowFreeInputs) {
            if (freeInput.getData() != null) {
                addKeyValueLine("Name: " , freeInput.getName());
                addKeyValueLine("Type: " , freeInput.getType());
                if(freeInput.getType().equals("List") || freeInput.getType().equals("Relation") || freeInput.getType().equals("Mapping")) {
                    addKeyValueLine("Input data:","");
                    addKeyValueLine("",freeInput.getData());
                }
                else
                    addKeyValueLine("Input data: " , freeInput.getData());
                if (freeInput.isMandatory()&&mandatoryOrNot)
                    addKeyValueLine("This input is mandatory: ", "Yes");
                else
                    addKeyValueLine("This input is mandatory: ", "No");
            }
        }

    }

    private void updateOutputsHistoryData(List<OutputExecutionDTO> outputs) {
        for (OutputExecutionDTO output : outputs) {
            addKeyValueLine("Name: " , output.getName());
            addKeyValueLine("Type: " , output.getType());
            if (output.getData() != null) {
                if(output.getType().equals("List") || output.getType().equals("Relation") || output.getType().equals("Mapping")) {
                    addKeyValueLine("Data: " ,"");
                    addKeyValueLine("",output.getData());
                }
                else
                    addKeyValueLine("Data: " ,output.getData());

            }
            else
                addKeyValueLine("Data: " ,"Not created due to failure in flow");
        }
    }

    private void updateStepsHistoryData(List<StepExecutionDTO> steps) {
        for (StepExecutionDTO step: steps) {
            addKeyValueLine("Name: " , step.getName());
            addKeyValueLine("Run time: " , step.getRunTime() + " ms");
            addKeyValueLine("Finish state: " , step.getStateAfterRun());
            addKeyValueLine("Step summary:" , step.getSummaryLine());
            addTitleLine("STEP LOGS:");
            addStepLogs(step.getStepExtensionDTO().getLogs());
        }
    }

    private String getFlowHistoryData(FlowExecutionDTO flowExecutionDTO) {
        String res = getFlowNameIDAndState(flowExecutionDTO);
        String temp;

        if(flowExecutionDTO.getStateAfterRun()!=null) {
            res += "Flow total run time: " + flowExecutionDTO.getRunTime() + " ms\n\n";
            res += "------------------------------\n";
        }
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
        if(flowExecutionDTO.getStateAfterRun()!=null)
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
            res += getStrLogs(step.getStepExtensionDTO().getLogs());
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
