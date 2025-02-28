package carter.stech.librarysystemv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private final StringProperty userId;
    private final StringProperty name;
    private List<String> checkedOutBooks; // List of ISBNs

    public User() {
        this.userId = new SimpleStringProperty("");
        this.name = new SimpleStringProperty("");
        this.checkedOutBooks = new ArrayList<>();
    }

    @JsonCreator
    public User(@JsonProperty("userId") String userId,
                @JsonProperty("name") String name,
                @JsonProperty("checkedOutBooks") List<String> checkedOutBooks) {
        this.userId = new SimpleStringProperty(userId);
        this.name = new SimpleStringProperty(name);
        this.checkedOutBooks = (checkedOutBooks != null) ? checkedOutBooks : new ArrayList<>();
    }

    // User ID methods
    @JsonProperty("userId")
    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    // Name methods
    @JsonProperty("name")
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // CheckedOutBooks methods
    @JsonProperty("checkedOutBooks")
    public List<String> getCheckedOutBooks() {
        return checkedOutBooks;
    }

    @JsonProperty("checkedOutBooks")
    public void addCheckedOutBook(String isbn) {
        checkedOutBooks.add(isbn);
    }

    public void returnBook(String isbn) {
        checkedOutBooks.remove(isbn);
    }

    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, CheckedOutBooks=%s]", getUserId(), getName(), checkedOutBooks);
    }
}
