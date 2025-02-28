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

public class UserRegistrationApp extends Application {
    private static final String USERS_FILE = "users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private ObservableList<User> userList;
    private TableView<User> tableView;

    @Override
    public void start(Stage primaryStage) {
        userList = FXCollections.observableArrayList(loadUsers()); // Ensure ObservableList is updated
        tableView = new TableView<>();

        // DEBUG: Print loaded users
        System.out.println("Loaded users: " + userList);

        // Table Columns
        TableColumn<User, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(data -> data.getValue().userIdProperty());

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());

        tableView.getColumns().addAll(idCol, nameCol);
        tableView.setItems(userList);

        // Input Fields
        TextField idField = new TextField();
        idField.setPromptText("User ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        Button addButton = new Button("Register User");
        addButton.setOnAction(e -> addUser(idField, nameField));

        HBox inputBox = new HBox(10, idField, nameField, addButton);
        inputBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(inputBox);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addUser(TextField idField, TextField nameField) {
        String userId = idField.getText().trim();
        String name = nameField.getText().trim();

        if (userId.isEmpty() || name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter both User ID and Name.");
            return;
        }

        for (User user : userList) {
            if (user.getUserId().equals(userId)) {
                showAlert(Alert.AlertType.ERROR, "Duplicate User", "User ID already exists!");
                return;
            }
        }

        User newUser = new User(userId, name, new ArrayList<>());
        userList.add(newUser);
        saveUsers(userList);

        idField.clear();
        nameField.clear();
    }

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

    private void saveUsers(List<User> users) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
