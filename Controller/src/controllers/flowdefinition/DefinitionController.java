package controllers.flowdefinition;

import controllers.AppController;
import dto.*;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefinitionController {

    private EngineApi engine;
    private AppController appController;
    @FXML
    private StackPane selectedFlowDetails;
    @FXML
    private StackPane tableStack;
    private final TableView<AvailableFlowDTO> flowTable = new TableView<>();
    private final ObservableList<AvailableFlowDTO> tvObservableList = FXCollections.observableArrayList();

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void addTable() {
        setTableappearance();

        fillTableObservableListWithData();
        flowTable.setItems(tvObservableList);

        TableColumn<AvailableFlowDTO, String> colname = new TableColumn<>("Name");
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<AvailableFlowDTO, String> coldesc = new TableColumn<>("Description");
        coldesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        coldesc.setCellFactory(tc -> {
            TableCell<AvailableFlowDTO, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(coldesc.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        TableColumn<AvailableFlowDTO, Integer> colsteps = new TableColumn<>("Number of steps");
        colsteps.setCellValueFactory(new PropertyValueFactory<>("numberOfSteps"));

        TableColumn<AvailableFlowDTO, String> colinputs = new TableColumn<>("Number of inputs");
        colinputs.setCellValueFactory(new PropertyValueFactory<>("numberOfInputs"));

        TableColumn<AvailableFlowDTO, Integer> colcontinuations = new TableColumn<>("Number of Continuations");
        colcontinuations.setCellValueFactory(new PropertyValueFactory<>("numberOfContinuations"));

        flowTable.getColumns().addAll(colname,coldesc,colinputs,colsteps,colcontinuations);

        //addButtonToTable();
        flowTable.getColumns().forEach(column -> column.setMinWidth(100));
        setTableClickFunction();
        tableStack.getChildren().add(flowTable);
    }



    private void setTableappearance() {
        flowTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        flowTable.setPrefWidth(700);
        flowTable.setPrefHeight(600);
    }

    private void fillTableObservableListWithData() {

        List<AvailableFlowDTO> availableFlows = engine.getAvailableFlows();
        if(availableFlows!=null) {
            tvObservableList.addAll(availableFlows);
            flowTable.setItems(tvObservableList);
        }
    }

    private void addButtonToTable() {
        TableColumn<AvailableFlowDTO, Void> colBtn = new TableColumn("Select Flow");

        Callback<TableColumn<AvailableFlowDTO, Void>, TableCell<AvailableFlowDTO, Void>> cellFactory = new Callback<TableColumn<AvailableFlowDTO, Void>, TableCell<AvailableFlowDTO, Void>>() {
            @Override
            public TableCell<AvailableFlowDTO, Void> call(final TableColumn<AvailableFlowDTO, Void> param) {
                final TableCell<AvailableFlowDTO, Void> cell = new TableCell<AvailableFlowDTO, Void>() {

                    private final Button btn = new Button("Get Full Data");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            AvailableFlowDTO data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        flowTable.getColumns().add(colBtn);

    }

    public void setTableClickFunction() {
       flowTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && !flowTable.getSelectionModel().isEmpty()) {
                AvailableFlowDTO selectedRow = flowTable.getSelectionModel().getSelectedItem();
                showFlowData(selectedRow);
            }
        });
    }

    private void showFlowData(AvailableFlowDTO selectedRow) {
        String flowName = selectedRow.getName();
        showFlowDefinition(flowName);
    }



    private void showFlowDefinition(String name)
    {
        FlowDefinitionDTO flowDefinition = engine.getFlowDefinition(name);
        String data;
        data = "SELECTED FLOW DATA:\n\n";
        data += "Flow name: " + flowDefinition.getName() + "\n";
        data += "Flow description: " + flowDefinition.getDescription() + "\n\n";
        data += getStrFormalOutputs(flowDefinition.getFormal_outputs()) + "\n";
        data += getStrReadOnlyStatus(flowDefinition.isReadOnly());
        data += "------------------------------\n";
        data += getStrStepsData(flowDefinition.getSteps());
        data += "------------------------------\n";
        data += getStrFreeInputs(flowDefinition.getFreeInputs());
        data += "------------------------------\n";
        data += getStrOutPuts(flowDefinition.getOutputs());
        data += "------------------------------\n";

        Text flowDefinitionText = new Text(data);
        //flowDefinitionText.setWrapText(true);
        //flowDefinitionText.setPrefWidth(definition.getPrefWidth());
        flowDefinitionText.setFont(Font.font(16));
        TextFlow textFlow = new TextFlow(flowDefinitionText);
        textFlow.prefWidthProperty().bind(selectedFlowDetails.widthProperty());
        Button executeBtn = new Button("Execute Flow !");
        executeBtn.setOnAction(e -> streamFlowToTab2(engine.getFlowIndexByName(flowDefinition.getName())));
        ScrollPane scrollPane = new ScrollPane(textFlow);
        scrollPane.setPrefWidth(selectedFlowDetails.getPrefWidth());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        selectedFlowDetails.getChildren().clear();
        selectedFlowDetails.getChildren().addAll(scrollPane,executeBtn);
        selectedFlowDetails.setAlignment(executeBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(executeBtn,new Insets(0,20,10,0));
    }

    private void streamFlowToTab2(int index) {
        appController.streamFlowFrom1To2(index);
    }

    private String getStrFormalOutputs(Set<String> formal_outputs) {
        String res;
        if (formal_outputs.size() > 0) {
            res = "The formal outputs of the flow are:\n";
            for (String currOutput : formal_outputs) {
                res = res + currOutput + "\n";
            }
        } else
            res = "The flow doesn't have formal outputs\n";

        return res;
    }

    private String getStrReadOnlyStatus(boolean read_only) {
        if (read_only)
            return "The flow is Read-Only: YES\n";
        else
            return "The flow is Read-Only: NO\n";
    }

    private String getStrStepsData(List<StepDefinitionDTO> steps) {
        String res = "THE FLOW'S STEPS:\n";
        String currStep;
        for (StepDefinitionDTO step : steps) {
            if (step.getName().equals(step.getDefaultName()))
                currStep = "Step name: " + step.getName() + "\n";
            else {
                currStep = "Step name: " + step.getDefaultName() + "\n";
                currStep += "Step alias: " + step.getName() + "\n";
            }
            if (step.isReadOnly())
                currStep = currStep + "The step is Read-Only: YES\n";
            else
                currStep = currStep + "This step is Read-Only: No\n";
            currStep = currStep + "\n";
            res += currStep;
        }

        return res;
    }

    private String getStrFreeInputs(List<FreeInputDefinitionDTO> flowFreeInputs) {
        String res;
        if (flowFreeInputs.isEmpty())
            res = "The flow have no free inputs\n";
        else {
            res = "Flow's free input's are:\n\n";
            String currInput;
            for (FreeInputDefinitionDTO freeInput : flowFreeInputs) {
                currInput = "Name: " + freeInput.getName() + "\n";
                currInput += "Type: " + freeInput.getType() + "\n";
                currInput += "Steps that are related to that input: ";
                for (String stepName : freeInput.getRelatedSteps())
                    currInput += stepName + ", ";
                currInput = currInput.substring(0, currInput.length() - 2);
                currInput += "\n";
                if (freeInput.isMandatory())
                    currInput += "This input is mandatory: Yes\n\n";
                else
                    currInput += "This input is mandatory: No\n\n";
                res += currInput;
            }
        }

        return res;
    }

    public String getStrOutPuts(List<OutputDefintionDTO> outputs) {
        String res = "THE FLOW'S OUTPUTS:\n";

        for (OutputDefintionDTO output : outputs) {
            res += "Output name: " + output.getName() + "\n";
            res += "Type: " + output.getType() + "\n";
            res += "Belongs to step: " + output.getStepName() + "\n\n";
        }

        if (outputs.size() > 0)
            return res;
        else
            return "THIS FLOW HAVE NO OUTPUTS";
    }


}

