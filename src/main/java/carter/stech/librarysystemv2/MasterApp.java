package carter.stech.librarysystemv2;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The Master Application that serves as the main menu for launching different
 * components of the Library System.
 */
public class MasterApp extends Application {

    /**
     * Starts the JavaFX application and initializes the main control UI.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);

        Button circulationButton = new Button("Open Circulation System");
        circulationButton.setOnAction(e -> launchApp("CirculationApp"));

        Button bookBrowserButton = new Button("Open Book Browser");
        bookBrowserButton.setOnAction(e -> launchApp("BookBrowserApp"));

        Button userRegistrationButton = new Button("Open User Registration");
        userRegistrationButton.setOnAction(e -> launchApp("UserRegistrationApp"));

        Button catalogingButton = new Button("Open Cataloging System");
        catalogingButton.setOnAction(e -> launchApp("CatalogingApp"));

        root.getChildren().addAll(circulationButton, bookBrowserButton,  userRegistrationButton, catalogingButton);
        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Library System - Master Control");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the specified JavaFX application dynamically.
     *
     * @param className The name of the application class to launch.
     */
    private void launchApp(String className) {
        try {
            // Load the JavaFX app class dynamically
            Class<?> appClass = Class.forName("carter.stech.librarysystemv2." + className);
            Application appInstance = (Application) appClass.getDeclaredConstructor().newInstance();

            // Start a new Stage for the application
            Stage newStage = new Stage();
            appInstance.start(newStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
