package elementlogic;

import datadefinition.Relation;
import dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ElementLogic {
    private VBox elementChoiceView;
    private VBox elementDetailsView;
    private Stage primaryStage;
    private FlowExecutionDTO flowExecutionDTO;

    private TableView<StepExecutionDTO> stepsTableView;

    private TableColumn<StepExecutionDTO,String> stepColumnView;

    private TableColumn<StepExecutionDTO,String> stateColumnView;

    public ElementLogic(VBox elementChoiceView,VBox elementDetailsView,Stage primaryStage) {
        this.elementChoiceView = elementChoiceView;
        this.elementDetailsView = elementDetailsView;
        this.primaryStage=primaryStage;

        stepsTableView=new TableView<>();
        stepColumnView=new TableColumn<>("step");
        stateColumnView=new TableColumn<>("state");

        stepColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));

        stepsTableView.getColumns().addAll(stepColumnView,stateColumnView);
        stepsTableView.setOnMouseClicked(e->rowClick(new ActionEvent()));
        stepsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        elementChoiceView.getChildren().add(stepsTableView);
        VBox.setVgrow(stepsTableView, Priority.ALWAYS);
    }


    public void setElementDetailsView(FlowExecutionDTO flowExecutionDTO)
    {
        this.flowExecutionDTO=flowExecutionDTO;
        ObservableList<StepExecutionDTO> items= FXCollections.observableArrayList();
        items.addAll(flowExecutionDTO.getSteps());
        stepsTableView.setItems(items);

        if(flowExecutionDTO.getStateAfterRun()!=null)
            updateFlowInfoView();
    }

    public void clear()
    {
        elementDetailsView.getChildren().clear();
        stepsTableView.getItems().clear();
        flowExecutionDTO=null;
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

        Label key =new Label(name);
        key.setFont(Font.font("System", FontWeight.BOLD,12));

        Label data =new Label(value);
        data.setAlignment(Pos.TOP_LEFT);

        hBox.getChildren().add(key);
        hBox.getChildren().add(data);

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
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, 400, 300);
        if(primaryStage.getScene().getStylesheets().size()!=0)
            scene.getStylesheets().add(primaryStage.getScene().getStylesheets().get(0));
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

        ObservableList<Map<String,String>> items= FXCollections.observableArrayList();
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
        if(data.size()==0) {
            Label label =new Label("empty list");
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font("System", FontWeight.BOLD,15));
            BorderPane borderPane =new BorderPane();
            borderPane.setCenter(label);
            showNewPopUp(borderPane);
        }
        else
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
            addKeyValueLine( "","The step had no logs\n\n");
        else {
            for (String currLog : logs) {
                addKeyValueLine( "",currLog+"\n\n");
            }
        }
    }


    public void updateFlowInfoView()
    {
        elementDetailsView.getChildren().clear();
        if(flowExecutionDTO==null)
            return;

        updateFlowNameIDAndState();
        if(flowExecutionDTO.getStateAfterRun()!=null)
            addKeyValueLine("Flow total run time: " , flowExecutionDTO.getRunTime() + " ms");
        else
            addKeyValueLine("Flow total run time: " ,  "flow is still running");
        addTitleLine("\n\nFREE INPUTS THAT RECEIVED DATA:\n");
        if(flowExecutionDTO.getFreeInputs().size()!=0) {
            updateFlowFreeInputs(flowExecutionDTO.getFreeInputs(), true);
            updateFlowFreeInputs(flowExecutionDTO.getFreeInputs(), false);
        }
        else
            addKeyValueLine("","NO FREE INPUTS HAVE RECEIVED DATA");
        addTitleLine("\nDATA PRODUCED (OUTPUTS):\n");
        if(flowExecutionDTO.getOutputs().size()!=0)
            updateOutputsHistoryData(flowExecutionDTO.getOutputs());
        else
            addKeyValueLine("","NO DATA WAS PRODUCED");
        addTitleLine("\nFLOW STEPS DATA:\n");
        updateStepsHistoryData(flowExecutionDTO.getSteps());

    }

    private void updateFlowNameIDAndState() {
        addTitleLine("FLOW EXECUTION DATA:\n");
        addKeyValueLine("Flows unique ID: ",flowExecutionDTO.getId());
        addKeyValueLine("Flow name: ",flowExecutionDTO.getName());
        if(flowExecutionDTO.getStateAfterRun()!=null)
            addKeyValueLine("Flow's final state : " , flowExecutionDTO.getStateAfterRun());
        else
            addKeyValueLine("Flow's final state : " , "flow is still running");


    }
    private void updateFlowFreeInputs(List<FreeInputExecutionDTO> flowFreeInputs, boolean mandatoryOrNot)
    {
        for (FreeInputExecutionDTO freeInput : flowFreeInputs) {
            if (freeInput.getData() != null) {
                if(freeInput.isMandatory()==mandatoryOrNot) {

                    addKeyValueLine("Name: ", freeInput.getName());
                    addKeyValueLine("Type: ", freeInput.getType());
                    addKeyValueLine("Input data: ", freeInput.getData());

                    if (mandatoryOrNot)
                        addKeyValueLine("This input is mandatory: ", "Yes\n\n");
                    else
                        addKeyValueLine("This input is mandatory: ", "No\n\n");
                }
            }
        }

    }

    private void updateOutputsHistoryData(List<OutputExecutionDTO> outputs) {
        for (OutputExecutionDTO output : outputs) {
            addKeyValueLine("Name: " , output.getName());
            addKeyValueLine("Type: " , output.getType());
            if (output.getData() != null) {
                if(output.getType().equals("List") || output.getType().equals("Relation")) {
                    addKeyHyperLinkValueLine("Data",output.getType(),output.getData());
                    addKeyValueLine("" ,"\n");
                }
                else
                    addKeyValueLine("Data: " ,output.getData().toString()+"\n\n");

            }
            else
                addKeyValueLine("Data: " ,"Not created due to failure in flow\n\n");
        }
    }

    private void updateStepsHistoryData(List<StepExecutionDTO> steps) {
        for (StepExecutionDTO step: steps) {
            addKeyValueLine("Step Name: " , step.getName());
            addKeyValueLine("Run time: " , step.getRunTime() + " ms");
            addKeyValueLine("Finish state: " , step.getStateAfterRun());
            addKeyValueLine("Step summary:" , step.getSummaryLine());
            addKeyValueLine("STEP LOGS:","");
            addStepLogs(step.getStepExtensionDTO().getLogs());
        }
    }
}
