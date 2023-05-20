package controllers.history;

import controllers.AppController;
import dto.FlowExecutionDTO;
import enginemanager.EngineApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
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

    private AppController appController;

    private EngineApi engine;


    private TableView<FlowExecutionDTO> historyTableView;

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
        activationTimeColumnView.setCellValueFactory(new PropertyValueFactory<>("activationTime"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));

        historyTableView.getColumns().addAll(flowNameColumnView,activationTimeColumnView,stateColumnView);
        historyTableView.getColumns().forEach(column -> column.setMinWidth(100));
        historyTableView.setOnMouseClicked(e->rowClick(new ActionEvent()));
        stackTableView.getChildren().add(historyTableView);
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

    }

    public void addRow(FlowExecutionDTO flowExecutionDTO)
    {
        historyTableView.getItems().add(flowExecutionDTO);
    }
}
