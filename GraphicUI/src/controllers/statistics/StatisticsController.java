package controllers.statistics;

import controllers.AppController;
import dto.AvailableFlowDTO;
import dto.StatisticsDTO;
import dto.StatisticsUnitDTO;
import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class StatisticsController {

    private EngineApi engine;
    private AppController appController;
    @FXML
    private StackPane flowsStatisticsPane;

    @FXML
    private StackPane stepsStatisticsPane;

    private final TableView<StatisticsUnitDTO> stepsTable = new TableView<>();
    private final TableView<StatisticsUnitDTO> flowsTable = new TableView<>();

    private final ObservableList<StatisticsUnitDTO> flowsObservableList = FXCollections.observableArrayList();
    private final ObservableList<StatisticsUnitDTO> stepsObservableList = FXCollections.observableArrayList();

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    private void setTableAppearance(TableView<StatisticsUnitDTO> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach(column -> column.setMinWidth(100));
        table.setPrefWidth(700);
        table.setPrefHeight(600);
        table.setEditable(false);
    }

    private List<TableColumn<StatisticsUnitDTO, String>> createTableColumns() {
        TableColumn<StatisticsUnitDTO, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StatisticsUnitDTO, String> colCount = new TableColumn<>("Amount of times activated");
        colCount.setCellValueFactory(new PropertyValueFactory<>("amountTimesActivated"));

        TableColumn<StatisticsUnitDTO, String> colRunTime = new TableColumn<>("Average run time");
        colRunTime.setCellValueFactory(new PropertyValueFactory<>("averageRunTime"));

        List<TableColumn<StatisticsUnitDTO, String>> columns = new ArrayList<>();
        columns.add(colName);
        columns.add(colCount);
        columns.add(colRunTime);

        return columns;
    }

    private void createFlowsTable() {
        flowsTable.getColumns().addAll(createTableColumns());
        setTableAppearance(flowsTable);
        flowsStatisticsPane.getChildren().add(flowsTable);
    }

    private void createStepsTable() {
        stepsTable.getColumns().addAll(createTableColumns());
        setTableAppearance(stepsTable);
        stepsStatisticsPane.getChildren().add(stepsTable);
    }

    public void fillTablesData() {
        StatisticsDTO statistics = engine.getStatistics();
        List<StatisticsUnitDTO> flowsStatistics = statistics.getFlowsStatistics();
        List<StatisticsUnitDTO> stepsStatistics = statistics.getStepsStatistics();
        if(!flowsObservableList.isEmpty())
            flowsObservableList.clear();
        flowsObservableList.addAll(flowsStatistics);
        flowsTable.setItems(flowsObservableList);
        if(!stepsObservableList.isEmpty())
            stepsObservableList.clear();
        stepsObservableList.addAll(stepsStatistics);
        stepsTable.setItems(stepsObservableList);
    }

    public void createStatisticsTables()
    {
        createFlowsTable();
        createStepsTable();
        fillTablesData();
    }


    public void clearTab() {
        flowsStatisticsPane.getChildren().clear();
        stepsStatisticsPane.getChildren().clear();
        flowsObservableList.clear();
        stepsObservableList.clear();
    }





}
