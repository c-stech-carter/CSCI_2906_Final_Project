package carter.stech.librarysystemv2;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BookHandler {
    private static final String FILE_NAME = "books.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static synchronized void saveBooks(List<Book> books) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Book> loadBooks() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return new java.util.ArrayList<>();
            return Arrays.asList(objectMapper.readValue(file, Book[].class));
        } catch (IOException e) {
            return new java.util.ArrayList<>();
        }
    }
}