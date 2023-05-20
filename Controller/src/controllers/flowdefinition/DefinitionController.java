package controllers.flowdefinition;

import enginemanager.EngineApi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class DefinitionController {

    EngineApi engine;
    @FXML
    private StackPane selectedFlowDetails;
    @FXML
    private StackPane tableStack;
    private final TableView<FlowData> flowTable = new TableView<>();
    private final ObservableList<FlowData> tvObservableList = FXCollections.observableArrayList();

    public void setEngine(EngineApi engine) {
        this.engine = engine;
    }

    @FXML
    void changeButtonName(ActionEvent event) {
        System.out.println("click123");
    }

    public void addTable() {
        setTableappearance();

        fillTableObservableListWithData();
        flowTable.setItems(tvObservableList);

        TableColumn<FlowData, String> colname = new TableColumn<>("Name");
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<FlowData, String> coldesc = new TableColumn<>("Description");
        coldesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        coldesc.setCellFactory(tc -> {
            TableCell<FlowData, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(coldesc.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        TableColumn<FlowData, Integer> colsteps = new TableColumn<>("Number of steps");
        colsteps.setCellValueFactory(new PropertyValueFactory<>("numberOfSteps"));

        TableColumn<FlowData, String> colinputs = new TableColumn<>("Number of inputs");
        colinputs.setCellValueFactory(new PropertyValueFactory<>("numberOfInputs"));

        TableColumn<FlowData, Integer> colcontinuations = new TableColumn<>("Number of Continuations");
        colcontinuations.setCellValueFactory(new PropertyValueFactory<>("numberOfContinuations"));

        flowTable.getColumns().addAll(colname,coldesc,colinputs,colsteps,colcontinuations);

        addButtonToTable();
        flowTable.getColumns().forEach(column -> column.setMinWidth(100));

        tableStack.getChildren().add(flowTable);
    }



    private void setTableappearance() {
        flowTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        flowTable.setPrefWidth(700);
        flowTable.setPrefHeight(600);
    }

    private void fillTableObservableListWithData() {

        tvObservableList.addAll(new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","defffffffffffffffffffffffffffffff\nfdsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss\nkddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddsc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8),
                new FlowData("name","desc",5,7,8));


    }

    private void addButtonToTable() {
        TableColumn<FlowData, Void> colBtn = new TableColumn("Select Flow");

        Callback<TableColumn<FlowData, Void>, TableCell<FlowData, Void>> cellFactory = new Callback<TableColumn<FlowData, Void>, TableCell<FlowData, Void>>() {
            @Override
            public TableCell<FlowData, Void> call(final TableColumn<FlowData, Void> param) {
                final TableCell<FlowData, Void> cell = new TableCell<FlowData, Void>() {

                    private final Button btn = new Button("Get Full Data");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            FlowData data = getTableView().getItems().get(getIndex());
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
                FlowData selectedRow = flowTable.getSelectionModel().getSelectedItem();
                showFlowData(selectedRow);
            }
        });
    }

    private void showFlowData(FlowData selectedRow) {
        String flowName = selectedRow.getName();

    }


}

