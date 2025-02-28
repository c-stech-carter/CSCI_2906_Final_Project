package carter.stech.librarysystemv2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CirculationApp extends Application {
    private static final String BOOKS_FILE = "books.json";
    private static final String USERS_FILE = "users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private ObservableList<Book> bookList;
    private ObservableList<User> userList;
    private TableView<Book> userBooksTable;
    private TextField userIdField;
    private TextField isbnField;
    private Label userNameLabel;
    private User currentUser;

    @Override
    public void start(Stage primaryStage) {
        bookList = FXCollections.observableArrayList(loadBooks());
        userList = FXCollections.observableArrayList(loadUsers());

        userIdField = new TextField();
        userIdField.setPromptText("Enter User ID");
        Button findUserButton = new Button("Find User");
        findUserButton.setOnAction(e -> findUser());

        HBox userInputBox = new HBox(10, userIdField, findUserButton);
        userInputBox.setPadding(new Insets(10));

        userNameLabel = new Label("User: Not Selected");

        userBooksTable = new TableView<>();
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> data.getValue().authorProperty());

        TableColumn<Book, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDueDate() != null ? data.getValue().getDueDate().toString() : "N/A"));

        userBooksTable.getColumns().addAll(titleCol, authorCol, dueDateCol);

        isbnField = new TextField();
        isbnField.setPromptText("Enter ISBN to Check Out");
        Button checkoutButton = new Button("Check Out Book");
        checkoutButton.setOnAction(e -> checkoutBook());

        HBox checkoutBox = new HBox(10, isbnField, checkoutButton);
        checkoutBox.setPadding(new Insets(10));

        VBox layout = new VBox(10, userInputBox, userNameLabel, userBooksTable, checkoutBox);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setTitle("Library Circulation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void findUser() {
        String userId = userIdField.getText().trim();
        Optional<User> userOptional = userList.stream().filter(user -> user.getUserId().equals(userId)).findFirst();

        if (userOptional.isPresent()) {
            currentUser = userOptional.get();
            userNameLabel.setText("User: " + currentUser.getName());
            updateUserBooksTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with ID " + userId);
        }
    }

    private void checkoutBook() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Find a user before checking out a book.");
            return;
        }

        String isbn = isbnField.getText().trim();
        Optional<Book> bookOptional = bookList.stream().filter(book -> book.getIsbn().equals(isbn) && book.isAvailable()).findFirst();

        if (bookOptional.isPresent()) {
            Book selectedBook = bookOptional.get();
            selectedBook.setAvailable(false);
            selectedBook.setBorrowedBy(currentUser.getUserId());
            selectedBook.setDueDate(LocalDate.now().plusWeeks(2));
            currentUser.addCheckedOutBook(isbn);

            saveBooks(bookList);
            saveUsers(userList);
            updateUserBooksTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book checked out successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Book Unavailable", "This book is not available for checkout.");
        }
    }

    private void updateUserBooksTable() {
        if (currentUser != null) {
            List<Book> checkedOutBooks = bookList.stream()
                    .filter(book -> book.getBorrowedBy() != null && book.getBorrowedBy().equals(currentUser.getUserId()))
                    .toList();
            userBooksTable.setItems(FXCollections.observableArrayList(checkedOutBooks));
        }
    }

    private List<Book> loadBooks() {
        try {
            File file = new File(BOOKS_FILE);
            if (!file.exists()) return new ArrayList<>();
            return List.of(objectMapper.readValue(file, Book[].class));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) return new ArrayList<>();
            return List.of(objectMapper.readValue(file, User[].class));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveBooks(List<Book> books) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BOOKS_FILE), books);
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[]args){
        launch(args);
    }
}
