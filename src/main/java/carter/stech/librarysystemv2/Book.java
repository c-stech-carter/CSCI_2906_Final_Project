package carter.stech.librarysystemv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;
import java.io.Serializable;
import java.time.LocalDate;

public class Book implements Serializable {
    private StringProperty title;
    private StringProperty author;
    private StringProperty isbn;
    private BooleanProperty isAvailable;
    private StringProperty borrowedBy;
    private ObjectProperty<LocalDate> dueDate;

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

    // Title methods
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    // Author methods
    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }
    public StringProperty authorProperty() { return author; }

    // ISBN methods
    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String isbn) { this.isbn.set(isbn); }
    public StringProperty isbnProperty() { return isbn; }

    // Availability methods
    @JsonProperty("available")
    public boolean isAvailable() { return isAvailable.get(); }

    @JsonProperty("available")
    public void setAvailable(boolean available) {
        this.isAvailable.set(available);
        if (available) {
            this.borrowedBy.set(null);
            this.dueDate.set(null);
        }
    }

    public BooleanProperty availableProperty() { return isAvailable; }

    // Due Date methods
    @JsonProperty("dueDate")
    public LocalDate getDueDate() { return dueDate.get(); }

    @JsonProperty("dueDate")
    public void setDueDate(LocalDate dueDate) { this.dueDate.set(dueDate); }

    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    // Borrowed By methods
    @JsonProperty("borrowedBy")
    public String getBorrowedBy() { return borrowedBy.get(); }

    @JsonProperty("borrowedBy")
    public void setBorrowedBy(String borrowedBy) { this.borrowedBy.set(borrowedBy); }

    public StringProperty borrowedByProperty() { return borrowedBy; }

    @Override
    public String toString() {
        return String.format("Book[Title=%s, Author=%s, ISBN=%s, Available=%b, BorrowedBy=%s, DueDate=%s]",
                getTitle(), getAuthor(), getIsbn(), isAvailable.get(),
                (borrowedBy.get() == null ? "N/A" : borrowedBy.get()),
                (dueDate.get() == null ? "N/A" : dueDate.get().toString()));
    }
}
