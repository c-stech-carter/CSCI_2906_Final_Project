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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A JavaFX application that provides a graphical interface for browsing books
 * in the library system.
 */
public class BookBrowserApp extends Application {
    private static final String BOOKS_FILE = "books.json";
    private ObservableList<Book> booksList;
    private TableView<Book> tableView;
    private TextField searchField;

    /**
     * Starts the JavaFX application and initializes the UI.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        booksList = FXCollections.observableArrayList(loadBooks());
        tableView = new TableView<>();

        // Table Columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> data.getValue().authorProperty());

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN or BookID");
        isbnCol.setCellValueFactory(data -> data.getValue().isbnProperty());

        TableColumn<Book, String> availabilityCol = new TableColumn<>("Availability");
        availabilityCol.setCellValueFactory(data ->
                data.getValue().isAvailable() ?
                        new javafx.beans.property.SimpleStringProperty("Available") :
                        new javafx.beans.property.SimpleStringProperty("Checked Out")
        );

        tableView.getColumns().addAll(titleCol, authorCol, isbnCol, availabilityCol);
        tableView.setItems(booksList); // Load all books initially

        // Search Field and Button
        searchField = new TextField();
        searchField.setPromptText("Search by Title, Author, or BookID");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchBooks());

        Button clearButton = new Button("Clear Search");
        clearButton.setOnAction(e -> clearSearch());

        HBox searchBox = new HBox(10, searchField, searchButton, clearButton);
        searchBox.setPadding(new Insets(10));

        // Layout
        BorderPane root = new BorderPane();
        root.setTop(searchBox);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Library Book Browser");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> searchField.getParent().requestFocus());
    }

    /**
     * Loads books from a JSON file.
     *
     * @return A list of Book objects.
     */
    private List<Book> loadBooks() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            File file = new File(BOOKS_FILE);
            if (!file.exists()) return FXCollections.observableArrayList();

            return List.of(objectMapper.readValue(file, Book[].class));
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Searches for books by title, author, or book ID (can be ISBN).
     * Filters the displayed book list accordingly.
     */
    private void searchBooks() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            tableView.setItems(booksList);
            return;
        }

        ObservableList<Book> filteredBooks = FXCollections.observableArrayList(
                booksList.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(query) ||
                                book.getAuthor().toLowerCase().contains(query) ||
                                book.getIsbn().equalsIgnoreCase(query))
                        .collect(Collectors.toList())
        );

        tableView.setItems(filteredBooks);
    }

    /**
     * Clears the search field and resets the book list.
     */
    private void clearSearch() {
        searchField.clear();
        tableView.setItems(booksList);
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
