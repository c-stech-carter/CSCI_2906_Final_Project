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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class CirculationApp extends Application {
    private static final String BOOKS_FILE = "books.json";
    private static final String USERS_FILE = "users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private ObservableList<Book> bookList;
    private ObservableList<User> userList;
    private TableView<Book> userBooksTable, recentCheckInsTable;
    private TextField userIdField, isbnField, checkInField;
    private Label userNameLabel;
    private User currentUser;
    private Queue<Book> recentCheckInsQueue = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) {
        bookList = FXCollections.observableArrayList(loadBooks());
        userList = FXCollections.observableArrayList(loadUsers());

        //--User UI for Check-Outs
        userIdField = new TextField();
        userIdField.setPromptText("Enter User ID or Name");
        Button findUserButton = new Button("Find User");
        findUserButton.setOnAction(e -> findUser());

        HBox userInputBox = new HBox(10, userIdField, findUserButton);
        userInputBox.setPadding(new Insets(10));

        userNameLabel = new Label("User: Not Selected");

        userBooksTable = new TableView<>();
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        titleCol.setPrefWidth(150);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> data.getValue().authorProperty());
        authorCol.setPrefWidth(150);

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

        //--Check in books UI
        checkInField = new TextField();
        checkInField.setPromptText("Enter ISBN or Title to Check In");
        Button checkInButton = new Button("Check In Book");
        checkInButton.setOnAction(e -> checkInBook());

        HBox checkInBox = new HBox(10, checkInField, checkInButton);
        checkInBox.setPadding(new Insets(10));

        recentCheckInsTable = new TableView<>();
        TableColumn<Book, String> checkInTitleCol = new TableColumn<>("Title");
        checkInTitleCol.setCellValueFactory(data -> data.getValue().titleProperty());
        checkInTitleCol.setPrefWidth(150);

        TableColumn<Book, String> checkInUserCol = new TableColumn<>("Last Borrowed By");
        checkInUserCol.setCellValueFactory(data ->
                new SimpleStringProperty(getUserNameById(data.getValue().getBorrowedBy())));
        checkInUserCol.setPrefWidth(150);

        TableColumn<Book, String> checkInDueDateCol = new TableColumn<>("Due Date");
        checkInDueDateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDueDate() != null ? data.getValue().getDueDate().toString() : "N/A"));

        recentCheckInsTable.getColumns().addAll(checkInTitleCol, checkInUserCol, checkInDueDateCol);

        VBox checkInLayout = new VBox(10, checkInBox, new Label("Recently Checked In Books"), recentCheckInsTable);
        checkInLayout.setPadding(new Insets(10));

        //Tabs setup
        TabPane tabPane = new TabPane();
        Tab checkoutTab = new Tab("Check Out", new VBox(10, userInputBox, userNameLabel, userBooksTable, checkoutBox));
        Tab checkInTab = new Tab("Check In", checkInLayout);
        checkoutTab.setClosable(false);
        checkInTab.setClosable(false);
        tabPane.getTabs().addAll(checkoutTab, checkInTab);




        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("Library Circulation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void findUser() {
        String input = userIdField.getText().trim();
        //Try searching by ID first
        Optional<User> userOptional = userList.stream().filter(user -> user.getUserId()
                .equals(input)).findFirst();

        //Alternatively, search by Name
        if(userOptional.isEmpty()) {
            userOptional = userList.stream()
                    .filter(user -> user.getName().equalsIgnoreCase(input))
                    .findFirst();
        }

        if (userOptional.isPresent()) {
            currentUser = userOptional.get();
            userNameLabel.setText("User: " + currentUser.getName());
            updateUserBooksTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with ID or Name: " + input);
        }
    }

    private void checkoutBook() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Find a user before checking out a book.");
            return;
        }

        //Try searching by ISBN first:
        String input = isbnField.getText().trim();
        Optional<Book> bookOptional = bookList.stream().filter(book -> book.getIsbn()
                .equalsIgnoreCase(input) && book.isAvailable()).findFirst();

        //Alternatively, search by Title
        if(bookOptional.isEmpty()) {
            bookOptional = bookList.stream()
                    .filter(book -> book.getTitle().equalsIgnoreCase(input)
                            && book.isAvailable()).findFirst();
        }

        if (bookOptional.isPresent()) {
            Book selectedBook = bookOptional.get();
            selectedBook.setAvailable(false);
            selectedBook.setBorrowedBy(currentUser.getUserId());
            //Default Checkout period can be changed here:
            selectedBook.setDueDate(LocalDate.now().plusWeeks(2));

            //Store only ISBN in user's record
            currentUser.addCheckedOutBook(selectedBook.getIsbn());

            saveBooks(bookList);
            saveUsers(userList);
            updateUserBooksTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book checked out successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Book Unavailable", "This book is not available for checkout.");
        }
    }

    private void checkInBook() {
        String input = checkInField.getText().trim();
        Optional<Book> bookOptional = bookList.stream()
                .filter(book -> (book.getIsbn().equalsIgnoreCase(input) || book.getTitle().equalsIgnoreCase(input))
                        && book.getBorrowedBy() != null)
                .findFirst();

        if (bookOptional.isPresent()) {
            Book selectedBook = bookOptional.get();
            String lastBorrowedBy = selectedBook.getBorrowedBy();
            LocalDate lastDueDate = selectedBook.getDueDate();

            //Find the user who checked out this book
            Optional<User> userOptional = userList.stream()
                    .filter(user -> user.getUserId().equals(lastBorrowedBy))
                    .findFirst();

            if (userOptional.isPresent()) {
                User borrower = userOptional.get();
                borrower.returnBook(selectedBook.getIsbn()); // Remove the book from the user's list
                saveUsers(userList); // Save updated user data
            }

            //Update book status
            selectedBook.setAvailable(true);
            selectedBook.setBorrowedBy(null);
            selectedBook.setDueDate(null);

            if (recentCheckInsQueue.size() == 5) recentCheckInsQueue.poll();
            recentCheckInsQueue.add(new Book(selectedBook.getTitle(), selectedBook.getAuthor(),
                    selectedBook.getIsbn(), false, lastBorrowedBy, lastDueDate)); //Displays original borrower/due date values in a temporary new Book
            recentCheckInsTable.setItems(FXCollections.observableArrayList(recentCheckInsQueue));



            saveBooks(bookList);
            updateUserBooksTable();  //Makes sure the checkout tab displays proper information
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book checked in successfully.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Check-In Error", "No checked-out book found with that ISBN or Title.");
        }
    }

    private String getUserNameById(String userId) {
        return userList.stream()
                .filter(user -> user.getUserId().equals(userId))
                .map(User::getName)
                .findFirst()
                .orElse("Unknown");
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
