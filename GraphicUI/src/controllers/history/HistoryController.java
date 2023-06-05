package controllers.history;

import controllers.AppController;
import dto.FlowExecutionDTO;
import dto.FreeInputExecutionDTO;
import dto.OutputExecutionDTO;
import dto.StepExecutionDTO;
import elementlogic.ElementLogic;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class HistoryController {
    @FXML
    private StackPane stackTableView;

    @FXML
    private ChoiceBox<String> stateFilterView;

    @FXML
    private VBox elementDetailsView;

    @FXML
    private VBox elementChoiceView;

    @FXML
    private Button reRunButton;

    @FXML
    private Button continuationButton;

    private AppController appController;

    private EngineApi engine;


    private TableView<FlowExecutionDTO> historyTableView;

    private ObservableList<FlowExecutionDTO> tableData;

    private TableColumn<FlowExecutionDTO,String> flowNameColumnView;

    private TableColumn<FlowExecutionDTO,String> activationTimeColumnView;

    private TableColumn<FlowExecutionDTO,String> flowStateColumnView;

    private ElementLogic elementLogic;




    @FXML
    public void initialize() {

        historyTableView=new TableView<>();
        historyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        flowNameColumnView=new TableColumn<>("flow name");
        activationTimeColumnView=new TableColumn<>("activation time");
        flowStateColumnView =new TableColumn<>("state after run");

        flowNameColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        flowStateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));
        activationTimeColumnView.setCellValueFactory(new PropertyValueFactory<>("activationTime"));

        historyTableView.getColumns().addAll(flowNameColumnView,activationTimeColumnView, flowStateColumnView);
        historyTableView.getColumns().forEach(column -> column.setMinWidth(200));
        historyTableView.setOnMouseClicked(e-> HistoryTableRowClick(new ActionEvent()));
        historyTableView.setEditable(false);
        stackTableView.getChildren().add(historyTableView);

        stateFilterView.getItems().addAll("ALL", "SUCCESS", "WARNING", "FAILURE");
        stateFilterView.setValue("ALL");
        stateFilterView.setOnAction(event -> filterTable());

        tableData = FXCollections.observableArrayList();
        historyTableView.setItems(tableData);

        reRunButton.setDisable(true);
        continuationButton.setDisable(true);


        historyTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                reRunButton.setDisable(false);
                checkIfContinuationsAvailable(historyTableView.getSelectionModel().getSelectedItem());
            } else {
                reRunButton.setDisable(true);
                continuationButton.setDisable(true);
            }
        });


    }

    private void checkIfContinuationsAvailable(FlowExecutionDTO selectedItem) {
        if (engine.getContinuationMenuDTOByName(selectedItem.getName()) != null)
            continuationButton.setDisable(false);
        else
            continuationButton.setDisable(true);
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setStage(Stage stage)
    {
        elementLogic=new ElementLogic(elementChoiceView,elementDetailsView,stage);
    }

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    @FXML
    void reRunFlow(ActionEvent event) {
        if(!historyTableView.getSelectionModel().isEmpty()) {
            FlowExecutionDTO flowExecutionDTO = historyTableView.getSelectionModel().getSelectedItem();
            engine.reUseInputsData(flowExecutionDTO);
            appController.streamFlow(flowExecutionDTO.getName());
        }

    }

    @FXML
    private void HistoryTableRowClick(ActionEvent event) {

        if(!historyTableView.getSelectionModel().isEmpty()) {
            FlowExecutionDTO flowExecutionDTO = historyTableView.getSelectionModel().getSelectedItem();
            elementLogic.setElementDetailsView(flowExecutionDTO);
        }
    }


    public void addRow(FlowExecutionDTO flowExecutionDTO)
    {
        tableData.add(0,flowExecutionDTO);
    }

    public void filterTable()
    {
        String choice = stateFilterView.getValue();
        FilteredList<FlowExecutionDTO> filteredData = new FilteredList<>(tableData);
        switch(choice) {
            case "ALL":
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

    public void clearTab()
    {
        elementLogic.clear();
        tableData.clear();
        historyTableView.getItems().clear();
        stateFilterView.setValue("ALL");
    }


    @FXML
    void showFlowInfo(MouseEvent event) {
        elementLogic.updateFlowInfoView();
    }

    @FXML
    void openContinuationPopUp(ActionEvent event) {
        FlowExecutionDTO flowExecutionDTO = historyTableView.getSelectionModel().getSelectedItem();
        TextInputDialog inputDialog =getNewTextInputDialog();
        Optional<String> result = Optional.empty();
        ChoiceBox<String> continuationChoice = new ChoiceBox<>();
        continuationChoice.getItems().addAll(engine.getContinuationMenuDTOByName(flowExecutionDTO.getName()).getTargetFlows());
        continuationChoice.setStyle("-fx-pref-width: 200px;");

        HBox hbox = new HBox(10, new Label("Available Continuations:"), continuationChoice);
        hbox.setAlignment(Pos.CENTER);
        inputDialog.getDialogPane().setContent(hbox);

        inputDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String selectedOption = continuationChoice.getValue();
                return selectedOption;
            }
            return null;
        });

        Button submitButton=(Button) inputDialog.getDialogPane().lookupButton(ButtonType.OK);

        continuationChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue == null);
        });

        result = inputDialog.showAndWait();
        if(result.isPresent())
        {
            String targetName= result.get();
            continueToFlow(targetName, flowExecutionDTO.getId());
        }
    }

    private TextInputDialog getNewTextInputDialog()
    {
        TextInputDialog inputDialog = new TextInputDialog();

        inputDialog.setTitle("Choose continuation");
        inputDialog.setHeaderText(null);
        inputDialog.setGraphic(null);
        inputDialog.getDialogPane().setPrefWidth(400);

        Button submitButton=(Button) inputDialog.getDialogPane().lookupButton(ButtonType.OK);
        submitButton.setText("Continue to flow");

        submitButton.setDisable(true);

        TextField textField = inputDialog.getEditor();

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(newValue.trim().isEmpty());
        });

        if(appController.getPrimaryStage().getScene().getStylesheets().size()!=0)
            inputDialog.getDialogPane().getStylesheets().add(appController.getPrimaryStage().getScene().getStylesheets().get(0));
        return  inputDialog;
    }

    void continueToFlow(String targetName, String id) {
        engine.doContinuation(engine.getFlowExecution(id),targetName);
        appController.streamFlow(targetName);
    }

}
