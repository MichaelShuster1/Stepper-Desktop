package controllers.flowdefinition;

import controllers.AppController;
import dto.*;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefinitionController {

    private EngineApi engine;
    private AppController appController;
    @FXML
    private StackPane selectedFlowDetails;
    @FXML
    private StackPane tableStack;
    private TableView<AvailableFlowDTO> flowTable;
    private final ObservableList<AvailableFlowDTO> tvObservableList = FXCollections.observableArrayList();

    private Popup popup;

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    public void initialize() {
        addTable();
        selectedFlowDetails.getChildren().add(new Label("No data"));
    }

    public void fillTableData()
    {
        fillTableObservableListWithData();
        flowTable.setItems(tvObservableList);
    }


    public void addTable() {
        flowTable = new TableView<>();
        setTableappearance();

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
            return cell;
        });

        TableColumn<AvailableFlowDTO, Integer> colsteps = new TableColumn<>("Number of steps");
        colsteps.setCellValueFactory(new PropertyValueFactory<>("numberOfSteps"));

        TableColumn<AvailableFlowDTO, String> colinputs = new TableColumn<>("Number of inputs");
        colinputs.setCellValueFactory(new PropertyValueFactory<>("numberOfInputs"));

        TableColumn<AvailableFlowDTO, Integer> colcontinuations = new TableColumn<>("Number of Continuations");
        colcontinuations.setCellValueFactory(new PropertyValueFactory<>("numberOfContinuations"));




        flowTable.getColumns().addAll(colname, coldesc, colinputs, colsteps, colcontinuations);

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
        if (availableFlows != null) {
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
        showFlowDefinition1(flowName);
    }

//
//    private void showFlowDefinition(String name) {
//        FlowDefinitionDTO flowDefinition = engine.getFlowDefinition(name);
//        String data;
//        data = "SELECTED FLOW DATA:\n\n";
//        data += "Flow name: " + flowDefinition.getName() + "\n";
//        data += "Flow description: " + flowDefinition.getDescription() + "\n\n";
//        data += getStrFormalOutputs(flowDefinition.getFormal_outputs()) + "\n";
//        data += getStrReadOnlyStatus(flowDefinition.isReadOnly());
//        data += "------------------------------\n";
//        data += getStrStepsData(flowDefinition.getSteps());
//        data += "------------------------------\n";
//        data += getStrFreeInputs(flowDefinition.getFreeInputs());
//        data += "------------------------------\n";
//        data += getStrOutPuts(flowDefinition.getOutputs());
//        data += "------------------------------\n";
//        data += getStrConnections(flowDefinition.getSteps());
//
//        Text flowDefinitionText = new Text(data);
//        //flowDefinitionText.setWrapText(true);
//        //flowDefinitionText.setPrefWidth(definition.getPrefWidth());
//        flowDefinitionText.setFont(Font.font(16));
//        TextFlow textFlow = new TextFlow(flowDefinitionText);
//        textFlow.prefWidthProperty().bind(selectedFlowDetails.widthProperty());
//        Button executeBtn = new Button("Execute Flow !");
//        executeBtn.setOnAction(e -> streamFlowToTab2(engine.getFlowIndexByName(flowDefinition.getName())));
//        ScrollPane scrollPane = new ScrollPane(textFlow);
//        scrollPane.setPrefWidth(selectedFlowDetails.getPrefWidth());
//        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        selectedFlowDetails.getChildren().clear();
//        selectedFlowDetails.getChildren().addAll(scrollPane, executeBtn);
//        selectedFlowDetails.setAlignment(executeBtn, Pos.BOTTOM_RIGHT);
//        StackPane.setMargin(executeBtn, new Insets(0, 20, 10, 0));
//    }

    private void showFlowDefinition1(String name) {
        FlowDefinitionDTO flowDefinition = engine.getFlowDefinition(name);
        VBox vbox = new VBox();
        List<HBox> addToVBox = new ArrayList<>();
        addToVBox.add(createHBoxForData("Flow name: ",flowDefinition.getName() + "\n"));
        addToVBox.add(createHBoxForData("Flow description: ",flowDefinition.getDescription() + "\n\n"));
        addToVBox.addAll(getStrFormalOutputs1(flowDefinition.getFormal_outputs()));
        addToVBox.add(getStrReadOnlyStatus1(flowDefinition.isReadOnly()));
        addToVBox.addAll(getStrStepsData1(flowDefinition.getSteps()));
        addToVBox.addAll(getStrFreeInputs1(flowDefinition.getFreeInputs()));
        addToVBox.addAll(getStrOutPuts1(flowDefinition.getOutputs()));

//        Text flowDefinitionText = new Text(data);
//        //flowDefinitionText.setWrapText(true);
//        //flowDefinitionText.setPrefWidth(definition.getPrefWidth());
//        flowDefinitionText.setFont(Font.font(16));
//        TextFlow textFlow = new TextFlow(flowDefinitionText);
//        textFlow.prefWidthProperty().bind(selectedFlowDetails.widthProperty());
        vbox.getChildren().addAll(addToVBox);
        Button executeBtn = new Button("Execute Flow !");
        executeBtn.setOnAction(e -> streamFlowToTab2(flowDefinition.getName()));
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setPrefWidth(selectedFlowDetails.getPrefWidth());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        selectedFlowDetails.getChildren().clear();
        selectedFlowDetails.getChildren().addAll(scrollPane, executeBtn);
        selectedFlowDetails.setAlignment(executeBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(executeBtn, new Insets(0, 20, 10, 0));
    }

    public HBox createHBoxForData(String title, String data)
    {
       HBox hbox = new HBox();
       Label titleLabel = new Label(title);
       titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
       Label dataText = new Label(data);
       dataText.setAlignment(Pos.TOP_LEFT);
       dataText.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
       dataText.setStyle("-fx-font-size: 14px");
       hbox.getChildren().add(titleLabel);
       hbox.getChildren().add(dataText);
       return hbox;
    }

    public HBox createHBoxForTitle(String title)
    {
        HBox hbox = new HBox();
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        hbox.getChildren().add(titleLabel);
        return hbox;
    }

    public HBox createHBoxForNoSep(String data)
    {
        HBox hbox = new HBox();
        Label dataText = new Label(data);
        dataText.setStyle("-fx-font-size: 14px");
        hbox.getChildren().add(dataText);
        return hbox;
    }



    private void streamFlowToTab2(String flowName) {
        appController.streamFlow(flowName);
    }

//    private String getStrFormalOutputs(Set<String> formal_outputs) {
//        String res;
//        if (formal_outputs.size() > 0) {
//            res = "The formal outputs of the flow are:\n";
//            for (String currOutput : formal_outputs) {
//                res = res + currOutput + "\n";
//            }
//        } else
//            res = "The flow doesn't have formal outputs\n";
//
//        return res;
//    }
//
//    private String getStrReadOnlyStatus(boolean read_only) {
//        if (read_only)
//            return "The flow is Read-Only: YES\n";
//        else
//            return "The flow is Read-Only: NO\n";
//    }
//
//    private String getStrStepsData(List<StepDefinitionDTO> steps) {
//        String res = "THE FLOW'S STEPS:\n";
//        String currStep;
//        for (StepDefinitionDTO step : steps) {
//            if (step.getName().equals(step.getDefaultName()))
//                currStep = "Step name: " + step.getName() + "\n";
//            else {
//                currStep = "Step name: " + step.getDefaultName() + "\n";
//                currStep += "Step alias: " + step.getName() + "\n";
//            }
//            if (step.isReadOnly())
//                currStep = currStep + "The step is Read-Only: YES\n";
//            else
//                currStep = currStep + "This step is Read-Only: No\n";
//            currStep = currStep + "\n";
//            res += currStep;
//        }
//
//        return res;
//    }
//
//    private String getStrFreeInputs(List<FreeInputDefinitionDTO> flowFreeInputs) {
//        String res;
//        if (flowFreeInputs.isEmpty())
//            res = "The flow have no free inputs\n";
//        else {
//            res = "Flow's free input's are:\n\n";
//            String currInput;
//            for (FreeInputDefinitionDTO freeInput : flowFreeInputs) {
//                currInput = "Name: " + freeInput.getName() + "\n";
//                currInput += "Type: " + freeInput.getType() + "\n";
//                currInput += "Steps that are related to that input: ";
//                for (String stepName : freeInput.getRelatedSteps())
//                    currInput += stepName + ", ";
//                currInput = currInput.substring(0, currInput.length() - 2);
//                currInput += "\n";
//                if (freeInput.isMandatory())
//                    currInput += "This input is mandatory: Yes\n\n";
//                else
//                    currInput += "This input is mandatory: No\n\n";
//                res += currInput;
//            }
//        }
//
//        return res;
//    }
//
//    public String getStrOutPuts(List<OutputDefintionDTO> outputs) {
//        String res = "THE FLOW'S OUTPUTS:\n";
//
//        for (OutputDefintionDTO output : outputs) {
//            res += "Output name: " + output.getName() + "\n";
//            res += "Type: " + output.getType() + "\n";
//            res += "Belongs to step: " + output.getStepName() + "\n\n";
//        }
//
//        if (outputs.size() > 0)
//            return res;
//        else
//            return "THIS FLOW HAVE NO OUTPUTS";
//    }
//
//
//    public List<String> getStrConnections(List<StepDefinitionDTO> steps) {
//        List<String> test = new ArrayList<>();
//        for (StepDefinitionDTO step : steps) {
//            String res = "";
//            StepConnectionsDTO connections = step.getConnections();
//            Map<String, Map<String, String>> outputsConnections = connections.getOutputsConnections();
//            Map<String, Pair<String, String>> inputsConnections = connections.getInputsConnections();
//            Map<String, Boolean> isMandatory = connections.getIsMandatory();
//            res = step.getName() + " Connections: \n";
//            if (outputsConnections != null) {
//                for (String name : outputsConnections.keySet()) {
//                    res += "Output name: " + name + "\n";
//                    if (!outputsConnections.get(name).isEmpty()) {
//                        res += "connected to:\n";
//                        for (String stepName : outputsConnections.get(name).keySet()) {
//                            res += outputsConnections.get(name).get(stepName) + "from step: " + stepName + "\n";
//                        }
//                    } else
//                        res += name + " have no connections" + "\n";
//
//                }
//            }
//            for (String input: isMandatory.keySet()) {
//                res += "input name: " + input + "\n";
//                res += "the input is mandatory: ";
//                if(isMandatory.get(input))
//                    res+= "yes\n";
//                else
//                    res+= "no\n";
//                if(inputsConnections != null && inputsConnections.containsKey(input)) {
//                        Pair<String,String> connection = inputsConnections.get(input);
//                        res += "This input is connected to the output" + connection.getValue() + " from the step: " + connection.getKey();
//                    }
//                else {
//                    res+= "no connections\n";
//                }
//                }
//            test.add(res);
//            }
//        return test;
//        }








        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private List<HBox> getStrFormalOutputs1(Set<String> formal_outputs) {
        List<HBox> res = new ArrayList<>();
        String title = "";
        String data = "";
        if (formal_outputs.size() > 0) {
            title = "THE FORMAL OUTPUTS OF THE FLOW\n\n";
            for (String currOutput : formal_outputs) {
                data = data + currOutput + "\n";
            }
            data += "\n";
            res.add(createHBoxForTitle(title));
            res.add(createHBoxForNoSep(data));
        } else
            res.add(createHBoxForTitle("The flow doesn't have formal outputs\n"));

        return res;
    }

    private HBox getStrReadOnlyStatus1(boolean read_only) {
        if (read_only)
            return createHBoxForData("The flow is Read-Only: ", "YES\n");
        else
            return createHBoxForData("The flow is Read-Only :" ,"NO\n");
    }

    private List<HBox> getStrStepsData1(List<StepDefinitionDTO> steps) {
        List<HBox> res= new ArrayList<>();
        res.add(createHBoxForTitle("THE FLOW'S STEPS\n\n"));
        for (StepDefinitionDTO step : steps) {
            if (step.getName().equals(step.getDefaultName()))
                res.add(createHBoxForData("Step name: " , step.getName()));
            else {
                res.add(createHBoxForData("Step name: " ,step.getDefaultName()));
                res.add(createHBoxForData("Step alias: " , step.getName()));
            }
            if (step.isReadOnly())
                res.add(createHBoxForData( "The step is Read-Only: ", "YES"));
            else
                res.add(createHBoxForData("This step is Read-Only: ", "No"));
            Hyperlink link = new Hyperlink("Full " + step.getName() +" Inputs\\Outputs details\n\n");
            link.setStyle("-fx-font-size: 14px");
            link.setOnAction(e -> setStepConnectionPopUp(step.getName(),step.getConnections(),link));
//            link.setOnMouseExited(event -> {
//                hidePopup();
//            });

            res.add(new HBox(link));
        }

        return res;
    }

    private void hidePopup() {
        if (popup != null) {
            popup.hide();
        }
    }


    private List<HBox> getStrFreeInputs1(List<FreeInputDefinitionDTO> flowFreeInputs) {
        List<HBox> res = new ArrayList<>();
        if (flowFreeInputs.isEmpty())
            res.add(createHBoxForTitle("THE FLOW HAVE NO FREE INPUTS\n"));
        else {
            res.add(createHBoxForTitle("THE FLOW'S FREE INPUTS\n\n"));
            for (FreeInputDefinitionDTO freeInput : flowFreeInputs) {
                res.add(createHBoxForData("Name: " , freeInput.getName()));
                res.add(createHBoxForData("Type: " , freeInput.getType()));
                String related = "";
                for (String stepName : freeInput.getRelatedSteps())
                    related += stepName + ", ";
                related = related.substring(0, related.length() - 2);
                res.add(createHBoxForData("Steps that are related to that input: ",related));
                if (freeInput.isMandatory())
                    res.add(createHBoxForData("This input is mandatory: ", "Yes\n\n"));
                else
                    res.add(createHBoxForData("This input is mandatory: ", "No\n\n"));
            }
        }

        return res;
    }

    public List<HBox> getStrOutPuts1(List<OutputDefintionDTO> outputs) {
        List<HBox> res= new ArrayList<>();
        res.add(createHBoxForTitle("THE FLOW'S OUTPUTS\n"));

        for (OutputDefintionDTO output : outputs) {
            res.add(createHBoxForData("Output name: " , output.getName()));
            res.add(createHBoxForData("Type: " , output.getType()));
            res.add(createHBoxForData("Belongs to step: " , output.getStepName() + "\n\n"));
        }

        if (!(outputs.size() > 0))
            res.add(createHBoxForTitle("THIS FLOW HAVE NO OUTPUTS\n\n"));

        return res;
    }


    public List<HBox> getStrConnections1(List<StepDefinitionDTO> steps) {
        List<HBox> test = new ArrayList<>();
        for (StepDefinitionDTO step : steps) {
            String res = "";
            StepConnectionsDTO connections = step.getConnections();
            Map<String, Map<String, String>> outputsConnections = connections.getOutputsConnections();
            Map<String, Pair<String, String>> inputsConnections = connections.getInputsConnections();
            Map<String, Boolean> isMandatory = connections.getIsMandatory();
            res = step.getName() + " Connections: \n";
            if (outputsConnections != null) {
                for (String name : outputsConnections.keySet()) {
                    res += "Output name: " + name + "\n";
                    if (!outputsConnections.get(name).isEmpty()) {
                        res += "connected to:\n";
                        for (String stepName : outputsConnections.get(name).keySet()) {
                            res += outputsConnections.get(name).get(stepName) + "from step: " + stepName + "\n";
                        }
                    } else
                        res += name + " have no connections" + "\n";

                }
            }
            for (String input: isMandatory.keySet()) {
                res += "input name: " + input + "\n";
                res += "the input is mandatory: ";
                if(isMandatory.get(input))
                    res+= "yes\n";
                else
                    res+= "no\n";
                if(inputsConnections != null && inputsConnections.containsKey(input)) {
                    Pair<String,String> connection = inputsConnections.get(input);
                    res += "This input is connected to the output" + connection.getValue() + " from the step: " + connection.getKey();
                }
                else {
                    res+= "no connections\n";
                }
            }
            //test.add(res);
        }
        return test;
    }

    private void setStepConnectionPopUp(String stepName, StepConnectionsDTO connections,Hyperlink link) {
        Map<String, Map<String, String>> outputsConnections = connections.getOutputsConnections();
        Map<String, Pair<String, String>> inputsConnections = connections.getInputsConnections();
        Map<String, Boolean> isMandatory = connections.getIsMandatory();
        VBox vbox = new VBox();
        List<HBox> addToVBox = new ArrayList<>();
        addToVBox.add(createHBoxForTitle(stepName + " Connections\n\n"));
        addToVBox.add(createHBoxForData("Inputs","\n"));
        for (String input: isMandatory.keySet()) {
            addToVBox.add(createHBoxForData("Input name: ", input));
            if (isMandatory.get(input))
                addToVBox.add(createHBoxForData("The input is mandatory: ", "Yes"));
            else
                addToVBox.add(createHBoxForData("The input is mandatory: ", "No"));
            if (inputsConnections != null && inputsConnections.containsKey(input)) {
                Pair<String, String> connection = inputsConnections.get(input);
                addToVBox.add(createHBoxForNoSep("This input is connected to the output " + "\"" + connection.getValue() + "\"" + " from the step " + "\"" + connection.getKey() + "\"" + "\n"));
            } else {
                addToVBox.add(createHBoxForData("No connections\n", "\n"));
            }
        }
        addToVBox.add(createHBoxForNoSep("\n\n"));
        if (outputsConnections != null) {
            if(outputsConnections.keySet().size() == 0)
                addToVBox.add(createHBoxForData("No Outputs", "\n"));
            else
                addToVBox.add(createHBoxForData("Outputs","\n"));
            for (String name : outputsConnections.keySet()) {
                addToVBox.add(createHBoxForData("Output name: ", name));
                if (outputsConnections.get(name).size() != 0) {
                    addToVBox.add(createHBoxForData("Connected to:", ""));
                    String currConn = "";
                    for (String currStepName : outputsConnections.get(name).keySet()) {
                        currConn += "The input " + "\"" + outputsConnections.get(name).get(currStepName) + "\"" + " from the step " + "\"" + currStepName + "\"" + "\n";
                    }
                    addToVBox.add(createHBoxForNoSep(currConn));
                }
                else
                    addToVBox.add(createHBoxForData("No connections", "\n"));
            }
        }
        for(HBox box: addToVBox)
            box.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(addToVBox);
        showPopup(appController.getPrimaryStage(),link,vbox);
    }

    private void showPopup(Stage ownerStage,Hyperlink openLink, VBox vbox) {
        Stage popupStage = new Stage();
        popupStage.initOwner(ownerStage);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Step details");
        popupStage.setResizable(true);
        popupStage.initStyle(StageStyle.UTILITY);

        //vbox.setStyle("-fx-background-color: white; -fx-padding: 10px;");
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);


        ScrollPane scroll = new ScrollPane(vbox);
        scroll.setMinWidth(400);
        scroll.setMinHeight(300);
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        //popup = new Popup();
        //popup.getContent().add(scroll);
        //popup.show(openLink, openLink.localToScreen(0, 0).getX(), openLink.localToScreen(0, 0).getY() + openLink.getHeight());
        Scene popupScene = new Scene(scroll);
        if(ownerStage.getScene().getStylesheets().size()!=0)
            popupScene.getStylesheets().add(ownerStage.getScene().getStylesheets().get(0));
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    public void clearTab() {
        selectedFlowDetails.getChildren().clear();
        flowTable.getItems().clear();
        tvObservableList.clear();
    }



}







