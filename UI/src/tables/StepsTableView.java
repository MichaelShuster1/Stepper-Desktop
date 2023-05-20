package tables;

import dto.StepExecutionDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class StepsTableView {

    private TableView<StepExecutionDTO> stepsTableView;

    private TableColumn<StepExecutionDTO,String> stepColumnView;

    private TableColumn<StepExecutionDTO,String> stateColumnView;


    public StepsTableView(EventHandler<MouseEvent> eventHandler) {
        stepsTableView=new TableView<>();
        stepColumnView=new TableColumn<>("step");
        stateColumnView=new TableColumn<>("state");

        stepColumnView.setCellValueFactory(new PropertyValueFactory<>("name"));
        stateColumnView.setCellValueFactory(new PropertyValueFactory<>("stateAfterRun"));

        stepsTableView.setOnMouseClicked(eventHandler);
        stepsTableView.getColumns().addAll(stepColumnView,stateColumnView);
    }

    public void setTable(ObservableList<StepExecutionDTO> list)
    {
        stepsTableView.setItems(list);
    }
}
