/*
Author: Charles T. Carter
Date: 3/10/2025
 */

package carter.stech.librarysystemv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a book in the library system.
 * Implements JavaFX properties for data binding and serialization.
 */
public class Book implements Serializable {
    private StringProperty title;
    private StringProperty author;
    private StringProperty isbn; //This field can double as a custom book ID
    private BooleanProperty isAvailable;
    private StringProperty borrowedBy;
    private ObjectProperty<LocalDate> dueDate;

    /**
     * Constructs a new Book object with the given parameters.
     *
     * @param title       The title of the book.
     * @param author      The author of the book.
     * @param isbn        The ISBN (or custom book ID) of the book.
     * @param isAvailable The availability status of the book.
     * @param borrowedBy  The user who borrowed the book (if applicable).
     * @param dueDate     The due date of the book (if applicable).
     */
    @JsonCreator
    public Book(@JsonProperty("title") String title,
                @JsonProperty("author") String author,
                @JsonProperty("isbn") String isbn,
                @JsonProperty("available") boolean isAvailable,
                @JsonProperty("borrowedBy") String borrowedBy,
                @JsonProperty("dueDate") LocalDate dueDate) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.isbn = new SimpleStringProperty(isbn);
        this.isAvailable = new SimpleBooleanProperty(isAvailable);
        this.borrowedBy = new SimpleStringProperty(isAvailable ? null : borrowedBy);
        this.dueDate = new SimpleObjectProperty<>(isAvailable ? null : dueDate);
    }

    /**
     * Gets the title of the book.
     * @return The title of the book.
     */
    public String getTitle() { return title.get(); }

    /**
     * Sets the title of the book.
     * @param title The new title.
     */
    public void setTitle(String title) { this.title.set(title); }

    /**
     * Gets the title property for JavaFX bindings.
     * @return The StringProperty of the title.
     */
    public StringProperty titleProperty() { return title; }

    /**
     * Gets the author of the book.
     * @return The author of the book.
     */
    public String getAuthor() { return author.get(); }

    /**
     * Sets the author of the book.
     * @param author The new author.
     */
    public void setAuthor(String author) { this.author.set(author); }

    /**
     * Gets the author property for JavaFX bindings.
     * @return The StringProperty of the author.
     */
    public StringProperty authorProperty() { return author; }

    /**
     * Gets the ISBN or Book ID.
     * @return The ISBN or Book ID.
     */
    public String getIsbn() { return isbn.get(); }

    /**
     * Sets the ISBN or Book ID.
     * @param isbn The new ISBN or Book ID.
     */
    public void setIsbn(String isbn) { this.isbn.set(isbn); }

    /**
     * Gets the ISBN property for JavaFX bindings.
     * @return The StringProperty of the ISBN.
     */
    public StringProperty isbnProperty() { return isbn; }

    /**
     * Checks if the book is available.
     * @return True if the book is available, false otherwise.
     */
    @JsonProperty("available")
    public boolean isAvailable() { return isAvailable.get(); }

    /**
     * Sets the availability status of the book.
     * If set to available, the borrower and due date will be reset.
     *
     * @param available The availability status.
     */
    @JsonProperty("available")
    public void setAvailable(boolean available) {
        this.isAvailable.set(available);
        if (available) {
            this.borrowedBy.set(null);
            this.dueDate.set(null);
        }
    }

    /**
     * Gets the availability property for JavaFX bindings.
     * @return The BooleanProperty of the availability.
     */
    public BooleanProperty availableProperty() { return isAvailable; }

    /**
     * Gets the due date of the book.
     * @return The due date, or null if not applicable.
     */
    @JsonProperty("dueDate")
    public LocalDate getDueDate() { return dueDate.get(); }

    /**
     * Sets the due date of the book.
     * @param dueDate The new due date.
     */
    @JsonProperty("dueDate")
    public void setDueDate(LocalDate dueDate) { this.dueDate.set(dueDate); }

    /**
     * Gets the due date property for JavaFX bindings.
     * @return The ObjectProperty of the due date.
     */
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    /**
     * Gets the ID of the borrower.
     * @return The borrower's ID or null if the book is available.
     */
    @JsonProperty("borrowedBy")
    public String getBorrowedBy() { return borrowedBy.get(); }

    /**
     * Sets the borrower's ID.
     * @param borrowedBy The new borrower.
     */
    @JsonProperty("borrowedBy")
    public void setBorrowedBy(String borrowedBy) { this.borrowedBy.set(borrowedBy); }

    /**
     * Gets the borrowedBy property for JavaFX bindings.
     * @return The StringProperty of the borrower.
     */
    public StringProperty borrowedByProperty() { return borrowedBy; }

    /**
     * Returns a string representation of the book.
     * @return A formatted string representing the book details.
     */
    @Override
    public String toString() {
        return String.format("Book[Title=%s, Author=%s, ISBN=%s, Available=%b, BorrowedBy=%s, DueDate=%s]",
                getTitle(), getAuthor(), getIsbn(), isAvailable.get(),
                (borrowedBy.get() == null ? "N/A" : borrowedBy.get()),
                (dueDate.get() == null ? "N/A" : dueDate.get().toString()));
    }
}
