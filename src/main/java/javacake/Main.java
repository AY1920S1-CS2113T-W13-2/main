package javacake;

import java.io.IOException;

import javacake.ui.MainWindow;
import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private static String savedDataPath = "data/";
    private Duke duke = new Duke();


    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setDuke(duke);
            fxmlLoader.<MainWindow>getController().setStage(stage);
            stage.show();
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/app_icon.jpg")));
            stage.setTitle("JavaCake");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}