package manager;

import model.*;
import util.Logger;
import util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class UserManager {
    private Map<String, user> users;
    private Map<String, user> usersByEmail;
    private static final String USERS_FILE = "users.dat";

    public UserManager() {
        this.users = new HashMap<>();
        this.usersByEmail = new HashMap<>();
        loadUsersFromFile();
    }

    public void addUser(user user) {
        if (user != null && !users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            usersByEmail.put(user.getEmail(), user);
            saveUsersToFile();
            Logger.log("User added: " + user.getEmail());
        }
    }

    public void removeUser(String userId) {
        user user = users.get(userId);
        if (user != null) {
            users.remove(userId);
            usersByEmail.remove(user.getEmail());
            saveUsersToFile();
            Logger.log("User removed: " + user.getEmail());
        }
    }

    public user getUserById(String userId) {
        return users.get(userId);
    }

    public user getUserByEmail(String email) {
        return usersByEmail.get(email);
    }

    public void updateUser(user user) {
        if (user != null && users.containsKey(user.getId())) {
            // Remove old email mapping if email changed
            user oldUser = users.get(user.getId());
            if (!oldUser.getEmail().equals(user.getEmail())) {
                usersByEmail.remove(oldUser.getEmail());
            }
            
            users.put(user.getId(), user);
            usersByEmail.put(user.getEmail(), user);
            saveUsersToFile();
            Logger.log("User updated: " + user.getEmail());
        }
    }

    public List<user> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<user> getUsersByRole(String role) {
        return users.values().stream()
                .filter(user -> user.getRole().equals(role))
                .collect(Collectors.toList());
    }

    public List<user> getActiveUsers() {
        return users.values().stream()
                .filter(user::isActive)
                .collect(Collectors.toList());
    }

    public List<user> getInactiveUsers() {
        return users.values().stream()
                .filter(user -> !user.isActive())
                .collect(Collectors.toList());
    }

    public boolean emailExists(String email) {
        return usersByEmail.containsKey(email);
    }

    public int getTotalUsers() {
        return users.size();
    }

    public int getActiveUsersCount() {
        return (int) users.values().stream().filter(user::isActive).count();
    }

    public void blockUser(String userId) {
        user user = users.get(userId);
        if (user != null) {
            user.setActive(false);
            updateUser(user);
        }
    }

    public void unblockUser(String userId) {
        user user = users.get(userId);
        if (user != null) {
            user.setActive(true);
            updateUser(user);
        }
    }

    public List<user> searchUsers(String keyword) {
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                               user.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void loadUsersFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(USERS_FILE);
            for (String line : lines) {
                user user = parseUserFromString(line);
                if (user != null) {
                    users.put(user.getId(), user);
                    usersByEmail.put(user.getEmail(), user);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load users from file: " + e.getMessage());
        }
    }

    private void saveUsersToFile() {
        try {
            FileHandler.clearFile(USERS_FILE);
            for (user user : users.values()) {
                String userString = convertUserToString(user);
                FileHandler.writeToFile(USERS_FILE, userString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save users to file: " + e.getMessage());
        }
    }

    private user parseUserFromString(String userString) {
        // Simple parsing - in real implementation would use proper serialization
        try {
            String[] parts = userString.split("\\|");
            if (parts.length >= 6) {
                String role = parts[5];
                switch (role) {
                    case "ADMIN":
                        return new Admin(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    case "TOURIST":
                        return new Tourist(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    case "AGENT":
                        return new TravelAgent(parts[0], parts[1], parts[2], parts[3], parts[4]);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to parse user: " + e.getMessage());
        }
        return null;
    }

    private String convertUserToString(user user) {
        return String.join("|", 
            user.getId(), user.getName(), user.getEmail(), 
            user.getPassword(), user.getPhone(), user.getRole(),
            String.valueOf(user.isActive()));
    }
}
