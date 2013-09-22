package io.github.samwright.workingdays;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * User: Sam Wright Date: 20/09/2013 Time: 23:06
 */
public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new MainWindow();
        Scene scene = new Scene(root, 300, 300);

        stage.setTitle("Working Days Calculator");
        stage.setScene(scene);
        stage.show();
    }
}
