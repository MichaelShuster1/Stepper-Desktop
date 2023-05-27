package controllers.statistics;

import controllers.AppController;
import dto.StatisticsDTO;
import dto.StatisticsUnitDTO;
import enginemanager.EngineApi;
import hardcodeddata.HCSteps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsController {

    private EngineApi engine;
    private AppController appController;
    @FXML
    private StackPane flowsStatisticsPane;

    @FXML
    private StackPane stepsStatisticsPane;

    private  TableView<StatisticsUnitDTO> stepsTable;
    private  TableView<StatisticsUnitDTO> flowsTable;

    private  BarChart<String,Number> stepsActivatedTimesGraph;
    private BarChart<String,Number> stepsAvgRunTimeGraph;

    private  BarChart<String,Number> flowsActivatedTimesGraph;
    private BarChart<String,Number> flowsAvgRunTimeGraph;

    private final ObservableList<StatisticsUnitDTO> flowsObservableList = FXCollections.observableArrayList();
    private final ObservableList<StatisticsUnitDTO> stepsObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        createFlowsTable();
        createStepsTable();
        createGraphs();
    }

    private void createGraphs() {
        createStepsActivatedTimesGraph();
        createStepsAvgRunTimeGraph();
        createFlowsActivatedTimesGraph();
        createFlowsAvgRunTimeGraph();
    }

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
        flowsTable = new TableView<>();
        flowsTable.getColumns().addAll(createTableColumns());
        setTableAppearance(flowsTable);
        flowsStatisticsPane.getChildren().add(flowsTable);
    }

    private void createStepsTable() {
        stepsTable = new TableView<>();
        stepsTable.getColumns().addAll(createTableColumns());
        setTableAppearance(stepsTable);
        stepsStatisticsPane.getChildren().add(stepsTable);
    }

    public void fillTablesData() {

        StatisticsDTO statistics = engine.getStatistics();
        List<StatisticsUnitDTO> flowsStatistics = statistics.getFlowsStatistics();
        List<StatisticsUnitDTO> stepsStatistics= statistics.getStepsStatistics();

        if(!flowsObservableList.isEmpty())
            flowsObservableList.clear();
        flowsObservableList.addAll(flowsStatistics);
        flowsTable.setItems(flowsObservableList);

        if(!stepsObservableList.isEmpty())
            stepsObservableList.clear();
        stepsObservableList.addAll(stepsStatistics);
        stepsTable.setItems(stepsObservableList);

        updateActivatedTimesGraph(stepsStatistics,stepsActivatedTimesGraph);
        updateAvgRunTimeGraph(stepsStatistics,stepsAvgRunTimeGraph);
        updateActivatedTimesGraph(flowsStatistics,flowsActivatedTimesGraph);
        updateAvgRunTimeGraph(flowsStatistics,flowsAvgRunTimeGraph);
    }

    public void createStatisticsTables()
    {
        createFlowsTable();
        createStepsTable();
        createGraphs();
        fillTablesData();
    }

    public void clearTab() {
        flowsStatisticsPane.getChildren().clear();
        stepsStatisticsPane.getChildren().clear();
        flowsObservableList.clear();
        stepsObservableList.clear();
        clearGraphs();
    }

    private void clearGraphs() {
        stepsActivatedTimesGraph.getData().clear();
        stepsAvgRunTimeGraph.getData().clear();
        flowsActivatedTimesGraph.getData().clear();
        flowsAvgRunTimeGraph.getData().clear();
    }


    @FXML
    void showFlowsActivatedTimesGraph(ActionEvent event) {
        StackPane stackPane=new StackPane(flowsActivatedTimesGraph);
        showNewPopUp(stackPane);
    }

    @FXML
    void showFlowsAvgRunTimeGraph(ActionEvent event) {
        StackPane stackPane=new StackPane(flowsAvgRunTimeGraph);
        showNewPopUp(stackPane);
    }

    @FXML
    void showStepsActivatedTimesGraph(ActionEvent event) {
        StackPane stackPane=new StackPane(stepsActivatedTimesGraph);
        showNewPopUp(stackPane);
    }

    @FXML
    void showStepsAvgRunTimeGraph(ActionEvent event) {
        StackPane stackPane=new StackPane(stepsAvgRunTimeGraph);
        showNewPopUp(stackPane);
    }


    private void createStepsActivatedTimesGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(HCSteps.getAllStepsName()));
        xAxis.setLabel("step name");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(1);
        yAxis.setLowerBound(0);
        yAxis.setAutoRanging(false);

        yAxis.setLabel("number of times activated");

        stepsActivatedTimesGraph = new BarChart<>(xAxis, yAxis);
        stepsActivatedTimesGraph.setTitle("steps number of times activated statistics");
    }

    private void createStepsAvgRunTimeGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(HCSteps.getAllStepsName()));
        xAxis.setLabel("step name");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("average run time (ms)");

        stepsAvgRunTimeGraph= new BarChart<>(xAxis, yAxis);
        stepsAvgRunTimeGraph.setTitle("steps average run time statistics");
    }

    private void createFlowsActivatedTimesGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("flow name");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(1);
        yAxis.setLowerBound(0);
        yAxis.setAutoRanging(false);

        yAxis.setLabel("number of times activated");

        flowsActivatedTimesGraph = new BarChart<>(xAxis, yAxis);
        flowsActivatedTimesGraph.setTitle("flows number of times activated statistics");
    }

    private void createFlowsAvgRunTimeGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("flow name");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("average run time (ms)");

        flowsAvgRunTimeGraph= new BarChart<>(xAxis, yAxis);
        flowsAvgRunTimeGraph.setTitle("flows average run time statistics");
    }

    private void updateActivatedTimesGraph(List<StatisticsUnitDTO> statistics, BarChart<String,Number> graph) {
        double max=0;

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for(StatisticsUnitDTO statisticsUnitDTO: statistics)
        {
            int times=statisticsUnitDTO.getAmountTimesActivated();
            series.getData().add(new XYChart.Data<>(statisticsUnitDTO.getName(),times));
            if(times>max)
                max=times;
        }

        NumberAxis yAxis=(NumberAxis) graph.getYAxis();
        yAxis.setUpperBound(max+2);

        graph.getData().clear();
        graph.getData().add(series);
    }

    private void updateAvgRunTimeGraph(List<StatisticsUnitDTO> statistics, BarChart<String,Number> graph) {
        double max=0;
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for(StatisticsUnitDTO statisticsUnitDTO: statistics)
        {
            double avgRunTIme=statisticsUnitDTO.getAverageRunTime();
            series.getData().add(new XYChart.Data<>(statisticsUnitDTO.getName(),avgRunTIme));
            if(avgRunTIme>max)
                max=avgRunTIme;
        }

        NumberAxis yAxis=(NumberAxis) graph.getYAxis();
        yAxis.setUpperBound(max+2);

        graph.getData().clear();
        graph.getData().add(series);
    }


    private void showNewPopUp(Parent root)
    {
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root, 1200, 300);
        if(appController.getPrimaryStage().getScene().getStylesheets().size()!=0)
            scene.getStylesheets().add(appController.getPrimaryStage().getScene().getStylesheets().get(0));
        stage.setScene(scene);
        stage.show();
    }



}
