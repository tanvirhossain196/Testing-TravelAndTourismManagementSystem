package ui;

import util.InputHandler;
import util.Logger;
import util.BackupManager;
import manager.*;
import model.user;
import model.Admin;
import report.ReportGenerator;
import java.util.List;

public class AdminMenu {
    private Admin admin;
    private UserManager userManager;
    private PackageManager packageManager;
    private BookingManager bookingManager;
    private PaymentManager paymentManager;
    private ReportGenerator reportGenerator;
    private boolean isRunning;

    public AdminMenu(user user, UserManager userManager) {
        this.admin = (Admin) user;
        this.userManager = userManager;
        this.packageManager = new PackageManager();
        this.bookingManager = new BookingManager();
        this.paymentManager = new PaymentManager();
        this.reportGenerator = new ReportGenerator(userManager, packageManager, bookingManager, paymentManager);
        this.isRunning = true;
    }

    public void displayAdminMenu() {
        while (isRunning) {
            clearScreen();
            printAdminHeader();
            printAdminMenuOptions();
            
            int choice = InputHandler.getInt("Enter your choice: ");
            handleAdminMenuChoice(choice);
        }
    }

    private void printAdminHeader() {
        System.out.println("=======================================================");
        System.out.println("                 ADMIN DASHBOARD");
        System.out.println("              Welcome, " + admin.getName());
        System.out.println("=======================================================");
        System.out.println();
    }

    private void printAdminMenuOptions() {
        System.out.println("-------------------------------------------------------");
        System.out.println("                ADMIN FUNCTIONS");
        System.out.println("-------------------------------------------------------");
        System.out.println(" 1.  User Management");
        System.out.println(" 2.  Package Management");
        System.out.println(" 3.  Booking Management");
        System.out.println(" 4.  Payment Management");
        System.out.println(" 5.  Reports & Analytics");
        System.out.println(" 6.  System Backup");
        System.out.println(" 7.  System Settings");
        System.out.println(" 8.  Logout");
        System.out.println("-------------------------------------------------------");
        System.out.println();
    }

    private void handleAdminMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleUserManagement();
                break;
            case 2:
                handlePackageManagement();
                break;
            case 3:
                handleBookingManagement();
                break;
            case 4:
                handlePaymentManagement();
                break;
            case 5:
                handleReportsAndAnalytics();
                break;
            case 6:
                handleSystemBackup();
                break;
            case 7:
                handleSystemSettings();
                break;
            case 8:
                logout();
                break;
            default:
                System.out.println("Invalid choice! Please select 1-8.");
                InputHandler.pressEnterToContinue();
        }
    }

    private void handleUserManagement() {
        while (true) {
            clearScreen();
            System.out.println("=======================================================");
            System.out.println("                USER MANAGEMENT");
            System.out.println("=======================================================");
            System.out.println("1. View All Users");
            System.out.println("2. Search User");
            System.out.println("3. Block/Unblock User");
            System.out.println("4. View User Statistics");
            System.out.println("5. Add New Admin");
            System.out.println("6. Back to Main Menu");

            int choice = InputHandler.getInt("Enter choice: ");
            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    searchUser();
                    break;
                case 3:
                    blockUnblockUser();
                    break;
                case 4:
                    viewUserStatistics();
                    break;
                case 5:
                    addNewAdmin();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice!");
                    InputHandler.pressEnterToContinue();
            }
        }
    }

    private void viewAllUsers() {
        clearScreen();
        System.out.println("ALL SYSTEM USERS");
        System.out.println("=======================================================");
        
        List<user> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found in the system.");
        } else {
            System.out.printf("%-12s %-25s %-30s %-10s %-8s%n", 
                "USER ID", "NAME", "EMAIL", "ROLE", "STATUS");
            System.out.println("-------------------------------------------------------");
            
            for (user u : users) {
                System.out.printf("%-12s %-25s %-30s %-10s %-8s%n",
                    u.getId(),
                    u.getName().length() > 24 ? u.getName().substring(0, 24) : u.getName(),
                    u.getEmail().length() > 29 ? u.getEmail().substring(0, 29) : u.getEmail(),
                    u.getRole(),
                    u.isActive() ? "Active" : "Blocked");
            }
        }
        
        System.out.println("\nTotal Users: " + users.size());
        InputHandler.pressEnterToContinue();
    }

    private void searchUser() {
        String searchTerm = InputHandler.getString("Enter email or name to search: ");
        List<user> users = userManager.searchUsers(searchTerm);
        
        clearScreen();
        System.out.println("SEARCH RESULTS");
        System.out.println("=======================================================");
        
        if (!users.isEmpty()) {
            for (user u : users) {
                System.out.println("User: " + u.getName());
                System.out.println("Email: " + u.getEmail());
                System.out.println("Role: " + u.getRole());
                System.out.println("Status: " + (u.isActive() ? "Active" : "Blocked"));
                System.out.println("-------------------------------------------------------");
            }
        } else {
            System.out.println("No users found matching: " + searchTerm);
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void blockUnblockUser() {
        String userId = InputHandler.getString("Enter User ID: ");
        user user = userManager.getUserById(userId);
        
        if (user != null) {
            if (user.getRole().equals("ADMIN")) {
                System.out.println("Cannot block/unblock admin users!");
            } else {
                boolean newStatus = !user.isActive();
                user.setActive(newStatus);
                userManager.updateUser(user);
                
                System.out.println("User " + user.getName() + " has been " + 
                    (newStatus ? "unblocked" : "blocked"));
                Logger.log("Admin " + admin.getName() + " changed user status: " + userId);
            }
        } else {
            System.out.println("User not found!");
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void viewUserStatistics() {
        List<user> users = userManager.getAllUsers();
        long totalUsers = users.size();
        long activeUsers = 0;
        long tourists = 0;
        long agents = 0;
        long admins = 0;
        
        for (user u : users) {
            if (u.isActive()) activeUsers++;
            if ("TOURIST".equals(u.getRole())) tourists++;
            else if ("AGENT".equals(u.getRole())) agents++;
            else if ("ADMIN".equals(u.getRole())) admins++;
        }

        clearScreen();
        System.out.println("USER STATISTICS");
        System.out.println("=======================================================");
        System.out.println("Total Users: " + totalUsers);
        System.out.println("Active Users: " + activeUsers);
        System.out.println("Blocked Users: " + (totalUsers - activeUsers));
        System.out.println();
        System.out.println("By Role:");
        System.out.println("  Tourists: " + tourists);
        System.out.println("  Agents: " + agents);
        System.out.println("  Admins: " + admins);
        
        InputHandler.pressEnterToContinue();
    }

    private void addNewAdmin() {
        System.out.println("ADD NEW ADMIN");
        System.out.println("=======================================================");
        
        try {
            String name = InputHandler.getString("Enter admin name: ");
            String email = InputHandler.getString("Enter admin email: ");
            String password = InputHandler.getString("Enter admin password: ");
            String phone = InputHandler.getString("Enter admin phone: ");
            
            if (userManager.emailExists(email)) {
                System.out.println("Email already exists!");
            } else {
                Admin newAdmin = new Admin(util.IDGenerator.generateUserId(), name, email, password, phone);
                userManager.addUser(newAdmin);
                System.out.println("New admin created successfully!");
                Logger.log("New admin created by " + admin.getName() + ": " + email);
            }
        } catch (Exception e) {
            System.out.println("Error creating admin: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void handlePackageManagement() {
        clearScreen();
        System.out.println("PACKAGE MANAGEMENT");
        System.out.println("=======================================================");
        System.out.println("Total Packages: " + packageManager.getTotalPackages());
        System.out.println("Active Packages: " + packageManager.getActivePackagesCount());
        System.out.println();
        System.out.println("Package management features:");
        System.out.println("- View all packages and their details");
        System.out.println("- Manage package availability and pricing");
        System.out.println("- Monitor package performance");
        System.out.println("- Generate package reports");
        System.out.println();
        System.out.println("Feature accessible through Package UI...");
        InputHandler.pressEnterToContinue();
    }

    private void handleBookingManagement() {
        clearScreen();
        System.out.println("BOOKING MANAGEMENT");
        System.out.println("=======================================================");
        System.out.println("Total Bookings: " + bookingManager.getTotalBookings());
        System.out.println("Pending Bookings: " + bookingManager.getPendingBookings().size());
        System.out.println("Confirmed Bookings: " + bookingManager.getConfirmedBookings().size());
        System.out.println("Cancelled Bookings: " + bookingManager.getCancelledBookings().size());
        System.out.println("Total Revenue: $" + String.format("%.2f", bookingManager.getTotalRevenue()));
        System.out.println();
        System.out.println("Booking management features under development...");
        InputHandler.pressEnterToContinue();
    }

    private void handlePaymentManagement() {
        clearScreen();
        System.out.println("PAYMENT MANAGEMENT");
        System.out.println("=======================================================");
        System.out.println("Total Payments: " + paymentManager.getTotalPayments());
        System.out.println("Total Revenue: $" + String.format("%.2f", paymentManager.getTotalRevenue()));
        System.out.println("Success Rate: " + String.format("%.1f%%", paymentManager.getSuccessRate()));
        System.out.println("Today's Revenue: $" + String.format("%.2f", paymentManager.getTodaysRevenue()));
        System.out.println();
        System.out.println("Payment management features under development...");
        InputHandler.pressEnterToContinue();
    }

    private void handleReportsAndAnalytics() {
        while (true) {
            clearScreen();
            System.out.println("REPORTS & ANALYTICS");
            System.out.println("=======================================================");
            System.out.println("1. Daily Report");
            System.out.println("2. Monthly Report");
            System.out.println("3. Annual Report");
            System.out.println("4. System Report");
            System.out.println("5. Custom Report");
            System.out.println("6. Back to Main Menu");

            int choice = InputHandler.getInt("Enter choice: ");
            switch (choice) {
                case 1:
                    generateDailyReport();
                    break;
                case 2:
                    generateMonthlyReport();
                    break;
                case 3:
                    generateAnnualReport();
                    break;
                case 4:
                    generateSystemReport();
                    break;
                case 5:
                    generateCustomReport();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice!");
                    InputHandler.pressEnterToContinue();
            }
        }
    }

    private void generateDailyReport() {
        clearScreen();
        System.out.println("Generating daily report...");
        String report = reportGenerator.generateDaily();
        System.out.println(report);
        InputHandler.pressEnterToContinue();
    }

    private void generateMonthlyReport() {
        clearScreen();
        System.out.println("Generating monthly report...");
        String report = reportGenerator.generateMonthly();
        System.out.println(report);
        InputHandler.pressEnterToContinue();
    }

    private void generateAnnualReport() {
        clearScreen();
        System.out.println("Generating annual report...");
        String report = reportGenerator.generateAnnual();
        System.out.println(report);
        InputHandler.pressEnterToContinue();
    }

    private void generateSystemReport() {
        clearScreen();
        System.out.println("Generating system report...");
        String report = reportGenerator.generateSystemReport();
        System.out.println(report);
        InputHandler.pressEnterToContinue();
    }

    private void generateCustomReport() {
        String reportType = InputHandler.getString("Enter report type (REVENUE/BOOKINGS/CUSTOMERS/PACKAGES): ");
        String startDate = InputHandler.getString("Enter start date (YYYY-MM-DD): ");
        String endDate = InputHandler.getString("Enter end date (YYYY-MM-DD): ");
        
        clearScreen();
        System.out.println("Generating custom report...");
        String report = reportGenerator.generateCustomReport(reportType, startDate, endDate);
        System.out.println(report);
        InputHandler.pressEnterToContinue();
    }

    private void handleSystemBackup() {
        clearScreen();
        System.out.println("SYSTEM BACKUP");
        System.out.println("=======================================================");
        System.out.println("Creating system backup...");
        
        try {
            BackupManager.createBackup();
            System.out.println("Backup completed successfully!");
            System.out.println("Backup location: backups/");
            System.out.println("Timestamp: " + util.DateUtil.getCurrentDateTime());
            Logger.log("System backup created by admin: " + admin.getName());
        } catch (Exception e) {
            System.out.println("Backup failed: " + e.getMessage());
            Logger.error("Backup failed: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void handleSystemSettings() {
        clearScreen();
        System.out.println("SYSTEM SETTINGS");
        System.out.println("=======================================================");
        System.out.println("Current system configuration:");
        System.out.println("- System name: TourBD v1.0");
        System.out.println("- Default currency: BDT");
        System.out.println("- Time zone: Asia/Dhaka");
        System.out.println("- Date format: YYYY-MM-DD");
        System.out.println("- Session timeout: 2 hours");
        System.out.println("- Max login attempts: 3");
        System.out.println();
        System.out.println("System settings management under development...");
        InputHandler.pressEnterToContinue();
    }

    private void logout() {
        clearScreen();
        System.out.println("=======================================================");
        System.out.println("                ADMIN LOGOUT");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Thank you for using the admin panel, " + admin.getName() + "!");
        System.out.println("Session ended at: " + util.DateUtil.getCurrentDateTime());
        
        Logger.log("Admin logged out: " + admin.getEmail());
        isRunning = false;
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}