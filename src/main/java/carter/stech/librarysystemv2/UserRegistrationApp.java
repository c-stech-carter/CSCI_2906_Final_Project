package carter.stech.librarysystemv2;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A JavaFX application for managing user registration in the library system.
 * Users can be registered and removed, with validation for unique user IDs.
 */
public class UserRegistrationApp extends Application {
    private static final String USERS_FILE = "users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private ObservableList<User> userList;
    private TableView<User> tableView;

    /**
     * Starts the JavaFX application and initializes the user registration UI.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        userList = FXCollections.observableArrayList(loadUsers()); // Ensure ObservableList is updated
        tableView = new TableView<>();

        /*
        // DEBUG: Print loaded users
        System.out.println("Loaded users: " + userList);
        */

        // Table Columns
        TableColumn<User, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(data -> data.getValue().userIdProperty());
        idCol.setPrefWidth(100);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        nameCol.setPrefWidth(150);

        tableView.getColumns().addAll(idCol, nameCol);
        tableView.setItems(userList);

        // Input Fields
        TextField idField = new TextField();
        idField.setPromptText("User ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        Button addButton = new Button("Register User");
        addButton.setOnAction(e -> addUser(idField, nameField));

        Button deleteButton = new Button("Delete User");
        deleteButton.setOnAction(e -> deleteUser());

        HBox inputBox = new HBox(10, idField, nameField, addButton, deleteButton);
        inputBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(inputBox);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Adds a new user to the system, ensuring the user ID is unique.
     *
     * @param idField  The text field containing the user ID.
     * @param nameField The text field containing the user's name.
     */
    private void addUser(TextField idField, TextField nameField) {
        String userId = idField.getText().trim();
        String name = nameField.getText().trim();

        if (userId.isEmpty() || name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter both User ID and Name.");
            return;
        }

        //Preventing users with duplicate IDs
        Optional<User> existingUser = userList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
        if (existingUser.isPresent()) {
            showAlert(Alert.AlertType.ERROR, "Duplicate User ID", "User ID already exists!  Please enter an unused ID.");
            return;
        }

        User newUser = new User(userId, name, new ArrayList<>());
        userList.add(newUser);
        saveUsers(userList);

        idField.clear();
        nameField.clear();
    }

    /**
     * Deletes the selected user if they do not have any active checkouts.
     */
    private void deleteUser() {
        Optional<User> selectedUser = Optional.ofNullable(tableView.getSelectionModel().getSelectedItem());

        //Will not allow user removal if they have active checkouts
        selectedUser.ifPresentOrElse(user -> {
            if (!user.getCheckedOutBooks().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Cannot Delete",
                        "User has active checkouts and cannot be deleted.");
                return;
            }

            userList.remove(user);
            saveUsers(userList);
        }, () -> showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a user to delete."));
    }

    /**
     * Loads users from the stored JSON file.
     *
     * @return A list of users.
     */
    private List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                System.out.println("users.json not found. Creating an empty file...");
                objectMapper.writeValue(file, new User[0]);
                return new ArrayList<>();
            }
            return List.of(objectMapper.readValue(file, User[].class));
        } catch (IOException e) {
            System.err.println("Error loading users.json: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves the current list of users to a JSON file.
     *
     * @param users The list of users to save.
     */
    private void saveUsers(List<User> users) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert message to the user.
     *
     * @param type    The type of alert.
     * @param title   The title of the alert.
     * @param message The message content.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * The main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
