package carter.stech.librarysystemv2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A JavaFX application for managing the catalog of books in a library system.
 * Allows users to add and remove books from the library catalog.
 */
public class CatalogingApp extends Application {
    private static final String BOOKS_FILE = "books.json";
    private ObservableList<Book> booksList;
    private TableView<Book> tableView;

    /**
     * Starts the JavaFX application and initializes the UI.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        booksList = FXCollections.observableArrayList(loadBooks());
        tableView = new TableView<>();

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> data.getValue().authorProperty());

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN or BookID");
        isbnCol.setCellValueFactory(data -> data.getValue().isbnProperty());

        tableView.getColumns().addAll(titleCol, authorCol, isbnCol);
        tableView.setItems(booksList);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");

        Button addButton = new Button("Add Book");
        addButton.setOnAction(e -> addBook(titleField, authorField, isbnField));

        Button removeButton = new Button("Remove Book");
        removeButton.setOnAction(e -> removeBook());

        HBox inputBox = new HBox(10, titleField, authorField, isbnField, addButton, removeButton);
        inputBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(inputBox);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Library Cataloging System");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Remove focus from titleField so that the text prompt is visible on startup
        Platform.runLater(() -> titleField.getParent().requestFocus());
    }

    /**
     * Adds a book to the catalog if all fields are filled.
     *
     * @param title  The text field for the book title.
     * @param author The text field for the book author.
     * @param isbn  The text field for the book ISBN.
     */
    private void addBook(TextField title, TextField author, TextField isbn) {
        if (!title.getText().isEmpty() && !author.getText().isEmpty() && !isbn.getText().isEmpty()) {
            Book newBook = new Book(title.getText(), author.getText(), isbn.getText(), true, null, null);
            booksList.add(newBook);
            saveBooks(booksList);
            title.clear();
            author.clear();
            isbn.clear();
        }
    }

    /**
     * Removes a book from the catalog if it is not checked out.
     */
    private void removeBook() {
        Book selectedBook = tableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            if (selectedBook.isAvailable()) {  // Prevent removal if book is checked out
                booksList.remove(selectedBook);
                saveBooks(booksList);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cannot Remove Book");
                alert.setHeaderText("Book is currently checked out");
                alert.setContentText("Please check the book back in before removing.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Loads books from a JSON file.
     *
     * @return A list of books in the catalog.
     */
    private List<Book> loadBooks() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // Enable LocalDate support

            File file = new File(BOOKS_FILE);
            if (!file.exists()) return FXCollections.observableArrayList();

            return List.of(objectMapper.readValue(file, Book[].class));
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Saves the current book catalog to a JSON file.
     *
     * @param books The list of books to save.
     */
    private void saveBooks(List<Book> books) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()); // Enable LocalDate support

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(BOOKS_FILE), books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

