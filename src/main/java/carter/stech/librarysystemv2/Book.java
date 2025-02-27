package carter.stech.librarysystemv2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.io.Serializable;
import java.time.LocalDate;

public class Book implements Serializable {
    private StringProperty title;
    private StringProperty author;
    private StringProperty isbn;
    private boolean isAvailable;
    private String borrowedBy;
    private LocalDate dueDate;

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
        this.isAvailable = isAvailable;
        this.borrowedBy = isAvailable ? null : borrowedBy;  // Set to null if book is available
        this.dueDate = isAvailable ? null : dueDate;  // Set to null if book is available
    }



    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getAuthor() { return author.get(); }
    public void setAuthor(String author) { this.author.set(author); }
    public StringProperty authorProperty() { return author; }

    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String isbn) { this.isbn.set(isbn); }
    public StringProperty isbnProperty() { return isbn; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    @Override
    public String toString() {
        return String.format("Book[Title=%s, Author=%s, ISBN=%s, Available=%b, BorrowedBy=%s, DueDate=%s]",
                getTitle(), getAuthor(), getIsbn(), isAvailable, borrowedBy,
                (dueDate == null ? "N/A" : dueDate.toString()));
    }
}

