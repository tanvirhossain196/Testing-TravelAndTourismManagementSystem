package ui;

import util.InputHandler;
import util.Validator;
import util.IDGenerator;
import util.Logger;
import auth.AuthService;
import manager.UserManager;
import model.*;
import enumtype.RoleType;

public class LoginUI {
    private AuthService authService;
    private UserManager userManager;

    public LoginUI(AuthService authService, UserManager userManager) {
        this.authService = authService;
        this.userManager = userManager;
    }

    public user displayLoginScreen() {
        clearScreen();
        printLoginHeader();
        
        String email = InputHandler.getString("Enter Email: ");
        String password = InputHandler.getString("Enter Password: ");
        
        user user = authService.authenticate(email, password);
        
        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.getName());
            Logger.log("User logged in: " + email);
            InputHandler.pressEnterToContinue();
            return user;
        } else {
            System.out.println("Invalid credentials! Please try again.");
            
            // Check if account is locked
            if (authService.isAccountLocked(email)) {
                long remainingTime = authService.getRemainingLockoutTime(email);
                System.out.println("Account is locked. Time remaining: " + remainingTime + " seconds");
            }
            
            InputHandler.pressEnterToContinue();
            return null;
        }
    }

    public void displayRegistrationScreen() {
        clearScreen();
        printRegistrationHeader();
        
        try {
            String name = getValidName();
            String email = getValidEmail();
            String password = getValidPassword();
            String phone = getValidPhone();
            RoleType role = getValidRole();
            
            user newUser = createUserByRole(role, name, email, password, phone);
            
            if (authService.registerUser(newUser)) {
                System.out.println("Registration successful!");
                System.out.println("Welcome to TourBD, " + name + "!");
                Logger.log("New user registered: " + email);
            } else {
                System.out.println("Registration failed! Email might already exist.");
            }
            
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            Logger.error("Registration error: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private String getValidName() {
        while (true) {
            String name = InputHandler.getString("Enter Full Name: ");
            if (Validator.isValidName(name)) {
                return name;
            }
            System.out.println("Invalid name. Please enter a valid name (2+ characters, letters only).");
        }
    }

    private String getValidEmail() {
        while (true) {
            String email = InputHandler.getString("Enter Email: ");
            if (Validator.validateEmail(email)) {
                if (!userManager.emailExists(email)) {
                    return email;
                } else {
                    System.out.println("Email already exists! Please use a different email.");
                }
            } else {
                System.out.println("Invalid email format! Please enter a valid email.");
            }
        }
    }

    private String getValidPassword() {
        while (true) {
            String password = InputHandler.getString("Enter Password (min 6 characters): ");
            if (Validator.validatePassword(password)) {
                String confirmPassword = InputHandler.getString("Confirm Password: ");
                if (password.equals(confirmPassword)) {
                    return password;
                } else {
                    System.out.println("Passwords don't match! Please try again.");
                }
            } else {
                System.out.println("Password must be at least 6 characters long.");
            }
        }
    }

    private String getValidPhone() {
        while (true) {
            String phone = InputHandler.getString("Enter Phone Number: ");
            if (Validator.validatePhone(phone)) {
                return phone;
            }
            System.out.println("Invalid phone number! Please enter a valid Bangladeshi number.");
        }
    }

    private RoleType getValidRole() {
        System.out.println("\nSelect Account Type:");
        System.out.println("1. Tourist (Browse and book packages)");
        System.out.println("2. Travel Agent (Manage packages)");
        
        while (true) {
            int choice = InputHandler.getInt("Enter choice (1-2): ");
            switch (choice) {
                case 1:
                    return RoleType.TOURIST;
                case 2:
                    return RoleType.AGENT;
                default:
                    System.out.println("Invalid choice! Please select 1 or 2.");
            }
        }
    }

    private user createUserByRole(RoleType role, String name, String email, String password, String phone) {
        String userId = IDGenerator.generateUserId();
        
        switch (role) {
            case TOURIST:
                return new Tourist(userId, name, email, password, phone);
            case AGENT:
                return new TravelAgent(userId, name, email, password, phone);
            default:
                throw new IllegalArgumentException("Invalid role for registration: " + role);
        }
    }

    private void printLoginHeader() {
        System.out.println("=======================================================");
        System.out.println("                    USER LOGIN");
        System.out.println("=======================================================");
        System.out.println();
    }

    private void printRegistrationHeader() {
        System.out.println("=======================================================");
        System.out.println("                 CREATE ACCOUNT");
        System.out.println("=======================================================");
        System.out.println();
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // Additional utility methods
    public void displayForgotPasswordScreen() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("                 FORGOT PASSWORD");
        System.out.println("=======================================================");
        System.out.println();
        
        String email = InputHandler.getString("Enter your email address: ");
        
        if (userManager.emailExists(email)) {
            System.out.println("Password reset instructions have been sent to your email.");
            System.out.println("Please check your email and follow the instructions.");
            Logger.log("Password reset requested for: " + email);
        } else {
            System.out.println("Email address not found in our records.");
        }
        
        InputHandler.pressEnterToContinue();
    }

    public void displayLoginHelp() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("                  LOGIN HELP");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Login Instructions:");
        System.out.println("1. Enter your registered email address");
        System.out.println("2. Enter your password");
        System.out.println("3. Press Enter to login");
        System.out.println();
        System.out.println("Troubleshooting:");
        System.out.println("- Make sure your email is correct");
        System.out.println("- Check if Caps Lock is on");
        System.out.println("- Account gets locked after 3 failed attempts");
        System.out.println("- Contact admin if you can't access your account");
        System.out.println();
        System.out.println("Account Types:");
        System.out.println("- Tourist: Can browse and book packages");
        System.out.println("- Agent: Can manage packages and bookings");
        System.out.println("- Admin: Full system access");
        System.out.println();
        System.out.println("For support: support@tourbd.com");
        System.out.println();
        
        InputHandler.pressEnterToContinue();
    }

    public void displayRegistrationHelp() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("               REGISTRATION HELP");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Registration Requirements:");
        System.out.println("- Full name (minimum 2 characters)");
        System.out.println("- Valid email address");
        System.out.println("- Password (minimum 6 characters)");
        System.out.println("- Valid phone number");
        System.out.println("- Account type selection");
        System.out.println();
        System.out.println("Password Guidelines:");
        System.out.println("- Use at least 6 characters");
        System.out.println("- Include letters and numbers");
        System.out.println("- Avoid using personal information");
        System.out.println("- Keep your password secure");
        System.out.println();
        System.out.println("Phone Number Format:");
        System.out.println("- Bangladesh: +8801xxxxxxxxx");
        System.out.println("- Example: +8801712345678");
        System.out.println();
        System.out.println("After registration:");
        System.out.println("- You can immediately login");
        System.out.println("- Complete your profile");
        System.out.println("- Start booking packages");
        System.out.println();
        
        InputHandler.pressEnterToContinue();
    }
}
