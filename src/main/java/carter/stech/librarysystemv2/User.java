package carter.stech.librarysystemv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the library system.
 * Users have a unique ID, a name, and a list of checked-out books.
 */
public class User implements Serializable {
    private final StringProperty userId;
    private final StringProperty name;
    private List<String> checkedOutBooks; // List of ISBNs

    /**
     * Default constructor for creating an empty user.
     */
    public User() {
        this.userId = new SimpleStringProperty("");
        this.name = new SimpleStringProperty("");
        this.checkedOutBooks = new ArrayList<>();
    }

    /**
     * Constructs a new User with the given parameters.
     *
     * @param userId          The unique ID of the user.
     * @param name            The name of the user.
     * @param checkedOutBooks A list of ISBNs of books checked out by the user.
     */
    @JsonCreator
    public User(@JsonProperty("userId") String userId,
                @JsonProperty("name") String name,
                @JsonProperty("checkedOutBooks") List<String> checkedOutBooks) {
        this.userId = new SimpleStringProperty(userId);
        this.name = new SimpleStringProperty(name);
        this.checkedOutBooks = (checkedOutBooks != null) ? checkedOutBooks : new ArrayList<>();
    }

    /**
     * Gets the user ID.
     * @return The user's unique ID.
     */
    @JsonProperty("userId")
    public String getUserId() {
        return userId.get();
    }

    /**
     * Sets the user ID.
     * @param userId The new user ID.
     */
    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    /**
     * Gets the user ID property for JavaFX bindings.
     * @return The StringProperty of the user ID.
     */
    public StringProperty userIdProperty() {
        return userId;
    }

    /**
     * Gets the user's name.
     * @return The name of the user.
     */
    @JsonProperty("name")
    public String getName() {
        return name.get();
    }

    /**
     * Sets the user's name.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Gets the name property for JavaFX bindings.
     * @return The StringProperty of the name.
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Gets the list of checked-out books.
     * @return A list of ISBNs or Book IDs of books the user has checked out.
     */
    @JsonProperty("checkedOutBooks")
    public List<String> getCheckedOutBooks() {
        return checkedOutBooks;
    }

    /**
     * Adds a book to the user's list of checked-out books.
     * @param isbn The ISBN of the book to add.
     */
    @JsonProperty("checkedOutBooks")
    public void addCheckedOutBook(String isbn) {
        checkedOutBooks.add(isbn);
    }

    /**
     * Removes a book from the user's list of checked-out books.
     * @param isbn The ISBN of the book to remove.
     */
    public void returnBook(String isbn) {
        checkedOutBooks.remove(isbn);
    }

    /**
     * Returns a string representation of the user.
     * @return A formatted string representing the user details.
     */
    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, CheckedOutBooks=%s]", getUserId(), getName(), checkedOutBooks);
    }
}
