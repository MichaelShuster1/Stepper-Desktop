package main;

import controllers.AppController;
import enginemanager.Manager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource =getClass().getResource("MainScreen.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(resource);
        Parent root = loader.load(resource.openStream());
        AppController controller = loader.getController();
        controller.setModel(new Manager());
        controller.setPrimaryStage(primaryStage);

        Scene scene = new Scene(root,1200,600);
        //scene.getStylesheets().add(getClass().getResource("Midnight.css").toExternalForm());
        primaryStage.setTitle("Stepper");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
