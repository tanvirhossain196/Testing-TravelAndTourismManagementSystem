package ui;

import util.InputHandler;
import util.Logger;
import manager.UserManager;
import auth.AuthService;
import model.user;

public class MainMenu {
    private UserManager userManager;
    private AuthService authService;
    private boolean isRunning;

    public MainMenu(UserManager userManager) {
        this.userManager = userManager;
        this.authService = new AuthService(userManager);
        this.isRunning = true;
    }

    public void displayMainMenu() {
        while (isRunning) {
            clearScreen();
            printWelcomeBanner();
            printMainMenuOptions();
            
            int choice = InputHandler.getInt("Enter your choice: ");
            handleMainMenuChoice(choice);
        }
    }

    private void printWelcomeBanner() {
        System.out.println("=======================================================");
        System.out.println("        TRAVEL & TOURISM MANAGEMENT SYSTEM");
        System.out.println("                 Welcome to TourBD");
        System.out.println("=======================================================");
        System.out.println();
    }

    private void printMainMenuOptions() {
        System.out.println("-------------------------------------------------------");
        System.out.println("                 MAIN MENU");
        System.out.println("-------------------------------------------------------");
        System.out.println(" 1. Login to System");
        System.out.println(" 2. Register New Account");
        System.out.println(" 3. Browse Packages (Guest)");
        System.out.println(" 4. About System");
        System.out.println(" 5. Contact Information");
        System.out.println(" 6. Exit System");
        System.out.println("-------------------------------------------------------");
        System.out.println();
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                browsePackagesAsGuest();
                break;
            case 4:
                showAboutSystem();
                break;
            case 5:
                showContactInfo();
                break;
            case 6:
                exitSystem();
                break;
            default:
                System.out.println("Invalid choice! Please select 1-6.");
                InputHandler.pressEnterToContinue();
        }
    }

    private void handleLogin() {
        LoginUI loginUI = new LoginUI(authService, userManager);
        user loggedInUser = loginUI.displayLoginScreen();
        
        if (loggedInUser != null) {
            redirectToUserMenu(loggedInUser);
        }
    }

    private void handleRegistration() {
        LoginUI loginUI = new LoginUI(authService, userManager);
        loginUI.displayRegistrationScreen();
    }

    private void redirectToUserMenu(user user) {
        try {
            String role = user.getRole();
            if ("ADMIN".equals(role)) {
                AdminMenu adminMenu = new AdminMenu(user, userManager);
                adminMenu.displayAdminMenu();
            } else if ("TOURIST".equals(role)) {
                TouristMenu touristMenu = new TouristMenu(user, userManager);
                touristMenu.displayTouristMenu();
            } else if ("AGENT".equals(role)) {
                AgentMenu agentMenu = new AgentMenu(user, userManager);
                agentMenu.displayAgentMenu();
            } else {
                System.out.println("Unknown user role: " + role);
                Logger.error("Unknown user role encountered: " + role);
                InputHandler.pressEnterToContinue();
            }
        } catch (Exception e) {
            System.out.println("Error accessing user menu: " + e.getMessage());
            Logger.error("Menu redirection error: " + e.getMessage());
            InputHandler.pressEnterToContinue();
        }
    }

    private void browsePackagesAsGuest() {
        try {
            clearScreen();
            System.out.println("TOUR PACKAGES (GUEST VIEW)");
            System.out.println("=======================================================");
            System.out.println("Register or login to book packages!");
            System.out.println();
            
            manager.PackageManager packageManager = new manager.PackageManager();
            java.util.List<model.TourPackage> packages = packageManager.listActivePackages();
            
            if (packages.isEmpty()) {
                System.out.println("No packages available at the moment.");
            } else {
                System.out.printf("%-12s %-25s %-15s %-10s %-5s%n", 
                    "PACKAGE ID", "NAME", "LOCATION", "PRICE", "DAYS");
                System.out.println("-------------------------------------------------------");
                
                for (model.TourPackage pkg : packages) {
                    System.out.printf("%-12s %-25s %-15s $%-9.2f %-5d%n",
                        pkg.getPackageId(),
                        pkg.getName().length() > 24 ? pkg.getName().substring(0, 24) : pkg.getName(),
                        pkg.getLocation().length() > 14 ? pkg.getLocation().substring(0, 14) : pkg.getLocation(),
                        pkg.getBasePrice(),
                        pkg.getDuration());
                }
                
                System.out.println("\nTotal: " + packages.size() + " packages");
            }
            
        } catch (Exception e) {
            System.out.println("Error browsing packages: " + e.getMessage());
            Logger.error("Guest browsing error: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void showAboutSystem() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("                  ABOUT SYSTEM");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Travel & Tourism Management System (TourBD)");
        System.out.println("Version: 1.0.0");
        System.out.println("Developed by: TourBD Development Team");
        System.out.println("Email: info@tourbd.com");
        System.out.println("Website: www.tourbd.com");
        System.out.println();
        System.out.println("System Features:");
        System.out.println("  - Complete tour package management");
        System.out.println("  - Hotel and room booking system");
        System.out.println("  - Transport booking and management");
        System.out.println("  - Tour guide assignment and scheduling");
        System.out.println("  - Secure payment processing");
        System.out.println("  - Review and rating system");
        System.out.println("  - Comprehensive reporting and analytics");
        System.out.println("  - Multi-user role support (Admin, Tourist, Agent)");
        System.out.println("  - Advanced search and filtering");
        System.out.println("  - Booking management and cancellations");
        System.out.println();
        System.out.println("Mission: To provide the best travel booking experience");
        System.out.println("    in Bangladesh with cutting-edge technology and");
        System.out.println("    exceptional customer service.");
        System.out.println();
        InputHandler.pressEnterToContinue();
    }

    private void showContactInfo() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("                CONTACT INFORMATION");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("TourBD - Travel & Tourism Management System");
        System.out.println();
        System.out.println("Head Office:");
        System.out.println("Address: 123 Tourism Street, Gulshan-2");
        System.out.println("          Dhaka-1212, Bangladesh");
        System.out.println();
        System.out.println("Contact Numbers:");
        System.out.println("   Hotline: +880-2-123456789");
        System.out.println("   Mobile: +880-1712345678");
        System.out.println("   Emergency: +880-1812345678");
        System.out.println();
        System.out.println("Email Addresses:");
        System.out.println("   General: info@tourbd.com");
        System.out.println("   Support: support@tourbd.com");
        System.out.println("   Bookings: booking@tourbd.com");
        System.out.println("   Corporate: corporate@tourbd.com");
        System.out.println();
        System.out.println("Online Presence:");
        System.out.println("   Website: www.tourbd.com");
        System.out.println("   Facebook: /TourBDBangladesh");
        System.out.println("   Instagram: @tourbd_official");
        System.out.println("   Twitter: @TourBD_BD");
        System.out.println();
        System.out.println("Business Hours:");
        System.out.println("   Saturday - Thursday: 9:00 AM - 6:00 PM");
        System.out.println("   Friday: 2:00 PM - 6:00 PM");
        System.out.println("   Emergency Support: 24/7 Available");
        System.out.println();
        System.out.println("Booking Support:");
        System.out.println("   Online booking available 24/7");
        System.out.println("   Phone booking: Business hours only");
        System.out.println("   Walk-in service: Business hours only");
        System.out.println();
        InputHandler.pressEnterToContinue();
    }

    private void exitSystem() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("            Thank you for using TourBD!");
        System.out.println("              Have a great journey!");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("We hope you enjoyed exploring our system.");
        System.out.println("For any assistance, call us at +880-2-123456789");
        System.out.println("Visit www.tourbd.com for online bookings");
        System.out.println();
        System.out.println("TourBD - Making Travel Dreams Come True!");
        System.out.println();
        
        Logger.log("System shutdown initiated by user from main menu");
        isRunning = false;
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // Utility methods for system information
    private void displaySystemStats() {
        System.out.println("SYSTEM STATISTICS");
        System.out.println("=======================================================");
        try {
            int totalUsers = userManager.getTotalUsers();
            int activeUsers = userManager.getActiveUsersCount();
            
            System.out.println("Total Registered Users: " + totalUsers);
            System.out.println("Active Users: " + activeUsers);
            System.out.println("System Status: Online");
            System.out.println("Last Updated: Today");
        } catch (Exception e) {
            System.out.println("Unable to load system statistics");
            Logger.error("System stats error: " + e.getMessage());
        }
        System.out.println("=======================================================");
    }

    // Method to handle emergency shutdown
    public void emergencyShutdown() {
        System.out.println("\nEMERGENCY SHUTDOWN INITIATED");
        Logger.log("Emergency shutdown triggered from MainMenu");
        isRunning = false;
    }

    // Method to check if system is running
    public boolean isSystemRunning() {
        return isRunning;
    }

    // Method to restart the system
    public void restartSystem() {
        Logger.log("System restart initiated from MainMenu");
        isRunning = true;
        displayMainMenu();
    }

    // Getters for dependencies (useful for testing)
    public UserManager getUserManager() {
        return userManager;
    }

    public AuthService getAuthService() {
        return authService;
    }
}