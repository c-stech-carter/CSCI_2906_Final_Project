package carter.stech.librarysystemv2;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserHandler {
    private static final String USERS_FILE = "users.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static synchronized void saveUsers(List<User> users) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(USERS_FILE), users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                objectMapper.writeValue(file, new User[0]); // Create an empty JSON array
                return new ArrayList<>();
            }
            return Arrays.asList(objectMapper.readValue(file, User[].class));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static User findUserById(String userId) {
        return loadUsers().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst().orElse(null);
    }
}

