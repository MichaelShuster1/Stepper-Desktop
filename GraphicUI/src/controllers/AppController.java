package controllers;

import controllers.flowdefinition.DefinitionController;
import controllers.statistics.StatisticsController;
import controllers.history.HistoryController;
import dto.FlowExecutionDTO;
import dto.InputsDTO;
import enginemanager.EngineApi;
import enginemanager.Manager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import controllers.flowexecution.ExecutionController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import progress.ProgressTracker;
import styles.Styles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppController {

    @FXML
    private StackPane definitionComponent;

    @FXML
    private DefinitionController definitionComponentController;

    @FXML
    private StackPane executionComponent;

    @FXML
    private ExecutionController executionComponentController;

    @FXML
    private StackPane historyComponent;

    @FXML
    private HistoryController historyComponentController;

    @FXML
    private StackPane statisticsComponent;
    @FXML
    private StatisticsController statisticsComponentController;

    @FXML
    private Button loadXML;

    @FXML
    private Label loadedXML;

    @FXML
    private TabPane tabPaneView;

    @FXML
    private Tab executionTabView;
    @FXML
    private ChoiceBox<String> styleChoiceView;

    @FXML
    private ImageView stepperLogo;

    private EngineApi engine;

    private ProgressTracker progressTracker;

    private Stage primaryStage;

    private boolean tabClicked;




    @FXML
    public void initialize() {
        executionComponentController.setAppController(this);
        definitionComponentController.setAppController(this);
        historyComponentController.setAppController(this);
        statisticsComponentController.setAppController(this);
        styleChoiceView.getItems().addAll(Styles.getStyles());
        styleChoiceView.setValue(Styles.DEFAULT.toString());
        styleChoiceView.setOnAction(e->setStyle());
        setTab(3);
        tabClicked=true;


        tabPaneView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if (newTab != null) {
                    String tabTitle = newTab.getText();
                    if(tabTitle.equals("Flows Execution")&&tabClicked) {
                        if(progressTracker.finishedFollowingLastActivatedFlow())
                            executionComponentController.clearTab();
                    }
                }
                tabClicked =true;
            }
        });


    }




    private void setStyle() {
        String choice= styleChoiceView.getValue();
        primaryStage.getScene().getStylesheets().clear();
        switch (Styles.valueOf(choice))
        {
            case DARK:
                primaryStage.getScene().getStylesheets().add(
                        getClass().getResource("../main/Dark.css").toExternalForm());
                stepperLogo.setImage(new Image(getClass().getResource("../main/blackLogo.png").toExternalForm()));
                break;
            case MIDNIGHT:
                primaryStage.getScene().getStylesheets().add(
                        getClass().getResource("../main/Midnight.css").toExternalForm());
                stepperLogo.setImage(new Image(getClass().getResource("../main/purpleLogo.png").toExternalForm()));
                break;
            case DEFAULT:
                stepperLogo.setImage(new Image(getClass().getResource("../main/blackLogo.png").toExternalForm()));
                break;
        }

    }


    public void setModel(Manager engine) {
        this.engine = engine;
        executionComponentController.setEngine(engine);
        definitionComponentController.setEngine(engine);
        statisticsComponentController.setEngine(engine);
        historyComponentController.setEngine(engine);
        progressTracker=new ProgressTracker(this,engine);
        Thread thread=new Thread(progressTracker);
        thread.setDaemon(true);
        thread.start();
    }

    public void addFlowId(String id)
    {
        progressTracker.addFlowId(id);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        executionComponentController.setStage(primaryStage);
        historyComponentController.setStage(primaryStage);

        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Consume the event to prevent default close behavior

            // Show a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            if(primaryStage.getScene().getStylesheets().size()!=0)
                alert.getDialogPane().getStylesheets().add(primaryStage.getScene().getStylesheets().get(0));

            if(progressTracker.areFlowsRunning())
                alert.setHeaderText("The are still flows running in the background!\nAre you sure you want to exit?");
            else
                alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("Press OK to exit the application.\n");

            // Handle the user's choice
            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    primaryStage.close();
                    engine.endProcess();
                }
            });
        });
    }


    public InputsDTO getFlowInputs(int index)
    {
        return engine.getFlowInputs(index);
    }


    @FXML
    private void loadXMLFile(ActionEvent event) {
       File selectedFile = openFileChooserAndGetFile();
       if(selectedFile == null)
           return;

        try {
            engine.loadXmlFile(selectedFile.getAbsolutePath());
            clearTabs();
            loadedXML.setText("Currently loaded file: " + selectedFile.getAbsolutePath());
            definitionComponentController.fillTableData();
            statisticsComponentController.createStatisticsTables();
        }
        catch (Exception ex)
        {
            /*
            Duration TEXT_CHANGE_DURATION = Duration.seconds(6);
            Timeline timeline = new Timeline();
            String INITIAL_TEXT = loadedXML.getText();

            // Define the initial keyframe
            KeyFrame initialKeyFrame = new KeyFrame(Duration.ZERO, e -> {
                loadedXML.setText("Error: " + ex.getMessage());
                loadedXML.setTextFill(Color.RED);
            });

            // Define the final keyframe
            KeyFrame finalKeyFrame = new KeyFrame(TEXT_CHANGE_DURATION, e -> {
                loadedXML.setText(INITIAL_TEXT);
                loadedXML.setTextFill(Color.BLACK);
            });

            // Add keyframes to the timeline
            timeline.getKeyFrames().addAll(initialKeyFrame, finalKeyFrame);

            // Play the timeline
            timeline.play();
            */
            Alert alert =new Alert(Alert.AlertType.ERROR);

            ObservableList<String> stylesheets = primaryStage.getScene().getStylesheets();
            if(stylesheets.size()!=0)
                alert.getDialogPane().getStylesheets().add(stylesheets.get(0));

            alert.setTitle("Error");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }


    private File openFileChooserAndGetFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set initial directory (optional)
        //fileChooser.setInitialDirectory(new File("C:\\Users\\Igal\\Desktop\\New folder (2)"));

        // Add file extension filters (optional)
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        return  selectedFile;
    }


    public void streamFlow(int index) {
        progressTracker.resetCurrentFlowId();
        executionComponentController.setTabView(getFlowInputs(index));
        tabClicked=false;
        setTab(2);
    }



    public void updateProgressFlow(FlowExecutionDTO flowExecutionDTO)
    {
        executionComponentController.updateProgressFlow(flowExecutionDTO);
    }



    public void setTab(int index)
    {
        tabPaneView.getSelectionModel().select(index);
    }


    public void updateStatistics() {
        statisticsComponentController.fillTablesData();
    }


    public void addRowInHistoryTable(FlowExecutionDTO flowExecutionDTO)
    {
        historyComponentController.addRow(flowExecutionDTO);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void clearTabs() {
        definitionComponentController.clearTab();
        executionComponentController.clearTab();
        historyComponentController.clearTab();
        statisticsComponentController.clearTab();
    }

}



