package controllers.history;

import controllers.AppController;
import enginemanager.EngineApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Text;

public class HistoryController {

    @FXML
    private Label stepDetalisView;

    @FXML
    private Text flowInfoView;

    @FXML
    private TitledPane stepsPaneView;

    private AppController appController;

    private EngineApi engine;



    @FXML
    public void initialize() {
        //
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
}
