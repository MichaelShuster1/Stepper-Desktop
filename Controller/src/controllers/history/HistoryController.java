package controllers.history;

import controllers.AppController;
import dto.FlowExecutionDTO;
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

    private TableColumn<FlowExecutionDTO,String> stateColumnView;




    @FXML
    public void initialize() {

        historyTableView=new TableView<>();
        flowNameColumnView=new TableColumn<>("flow name");
        activationTimeColumnView=new TableColumn<>("activation time");
        stateColumnView=new TableColumn<>("state after run");

        flowNameColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));
        activationTimeColumnView.setCellValueFactory(new PropertyValueFactory<>("activationTime"));

        historyTableView.getColumns().addAll(flowNameColumnView,activationTimeColumnView,stateColumnView);
        historyTableView.getColumns().forEach(column -> column.setMinWidth(200));
        historyTableView.setOnMouseClicked(e->rowClick(new ActionEvent()));
        stackTableView.getChildren().add(historyTableView);
        stateFilterView.getItems().addAll("NONE", "SUCCESS", "WARNING", "FAILURE");
        stateFilterView.setValue("NONE");
        stateFilterView.setOnAction(event -> filterTable());
        tableData = FXCollections.observableArrayList();
        historyTableView.setItems(tableData);
    }



    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    @FXML
    void reRunFlow(ActionEvent event) {

    }

    @FXML
    private void rowClick(ActionEvent event) {
        FlowExecutionDTO flowExecutionDTO= historyTableView.getSelectionModel().getSelectedItem();
        System.out.println(flowExecutionDTO.getName());

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
}
