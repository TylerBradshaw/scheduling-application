package Main;

import controllers.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    /**
     * The path of the resource that defines the main user interface.
     */
    private static final String FXML_RESOURCE = "login.fxml";
    /**
     * Title of the main window of the application.
     */
    private static final String TITLE = "U-Schedule";
    /**
     * Launches the application.
     * @param stage The primary stage of the application
     * @throws IOException If the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL resource = getClass().getResource(FXML_RESOURCE);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + FXML_RESOURCE);
        }

        stage.setTitle(TITLE);
        stage.setScene(new Scene(FXMLLoader.load(resource)));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            JDBC.openConnection();
            launch();
        } finally {
            JDBC.closeConnection();
        }
    }
}