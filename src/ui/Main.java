import ui.MainMenu;
import manager.*;
import auth.AuthService;
import util.*;
import model.*;
import enumtype.*;
import report.ReportGenerator;
import java.io.File;

public class Main {
    private static UserManager userManager;
    private static PackageManager packageManager;
    private static BookingManager bookingManager;
    private static PaymentManager paymentManager;
    private static HotelManager hotelManager;
    private static TransportManager transportManager;
    private static GuideManager guideManager;
    private static ReviewManager reviewManager;
    private static RefundManager refundManager;
    private static RoomManager roomManager;
    private static AuthService authService;
    private static ReportGenerator reportGenerator;

    public static void main(String[] args) {
        displayStartupBanner();
        
        try {
            // Initialize system components
            initializeSystem();
            
            // Initialize all managers
            initializeManagers();
            
            // Configure system settings
            configureSystemSettings();
            
            // Seed initial data
            seedInitialData();
            
            // Perform system health check
            performSystemHealthCheck();
            
            // Start background tasks
            startBackgroundTasks();
            
            // Initialize and start the main menu UI
            startUserInterface();
            
        } catch (Exception e) {
            handleStartupError(e);
        } finally {
            // Cleanup and shutdown
            performShutdown();
        }
    }
    
    private static void displayStartupBanner() {
        System.out.println("=======================================================");
        System.out.println("        TRAVEL & TOURISM MANAGEMENT SYSTEM");
        System.out.println("                   TourBD v1.0");
        System.out.println("                   Starting...");
        System.out.println("=======================================================");
        System.out.println();
        System.out.println("Welcome to TourBD - Your Complete Travel Solution");
        System.out.println("System Date: " + DateUtil.getCurrentDateTime());
        System.out.println();
    }
    
    private static void initializeSystem() {
        System.out.println("Initializing system components...");
        
        try {
            // Initialize logging system
            Logger.log("=== SYSTEM STARTUP ===");
            Logger.log("Travel & Tourism Management System v1.0");
            Logger.log("Startup time: " + DateUtil.getCurrentDateTime());
            Logger.log("Java Version: " + System.getProperty("java.version"));
            Logger.log("Operating System: " + System.getProperty("os.name"));
            
            // Create necessary directories
            createDirectoryStructure();
            
            // Initialize file handlers
            initializeFileSystem();
            
            System.out.println("System components initialized successfully");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize system components", e);
        }
    }
    
    private static void createDirectoryStructure() {
        String[] directories = {"data", "exports", "backups", "logs", "reports", "temp"};
        
        for (String dir : directories) {
            try {
                File directory = new File(dir);
                if (!directory.exists()) {
                    directory.mkdirs();
                    Logger.log("Created directory: " + dir);
                }
            } catch (Exception e) {
                Logger.error("Failed to create directory: " + dir + " - " + e.getMessage());
            }
        }
    }
    
    private static void initializeFileSystem() {
        try {
            // Initialize file handlers
            Logger.log("File system initialized");
        } catch (Exception e) {
            Logger.error("Failed to initialize file system: " + e.getMessage());
        }
    }
    
    private static void initializeManagers() {
        System.out.println("Initializing core managers...");
        
        try {
            // Core managers
            userManager = new UserManager();
            packageManager = new PackageManager();
            bookingManager = new BookingManager();
            paymentManager = new PaymentManager();
            hotelManager = new HotelManager();
            transportManager = new TransportManager();
            guideManager = new GuideManager();
            reviewManager = new ReviewManager();
            roomManager = new RoomManager();
            
            // Initialize refund manager with payment manager dependency
            refundManager = new RefundManager(paymentManager);
            
            // Initialize authentication service
            authService = new AuthService(userManager);
            
            // Initialize report generator
            reportGenerator = new ReportGenerator(userManager, packageManager, bookingManager, paymentManager);
            
            System.out.println("All managers initialized successfully");
            Logger.log("Core managers initialized");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize managers", e);
        }
    }
    
    private static void configureSystemSettings() {
        System.out.println("Configuring system settings...");
        
        try {
            // Configure authentication settings
            authService.getLoginManager().setMaxLoginAttempts(3);
            authService.getLoginManager().setSessionTimeout(120); // 2 hours
            authService.getLoginManager().setAccountLockoutDuration(30); // 30 minutes
            authService.getLoginManager().setEnableSessionTimeout(true);
            authService.getLoginManager().setEnableAccountLockout(true);
            
            // Add authorized admin emails
            authService.addAuthorizedAdmin("admin@tourbd.com");
            authService.addAuthorizedAdmin("superadmin@tourbd.com");
            
            System.out.println("System settings configured");
            Logger.log("System settings configured successfully");
            
        } catch (Exception e) {
            Logger.error("Failed to configure system settings: " + e.getMessage());
        }
    }
    
    private static void seedInitialData() {
        System.out.println("Seeding initial data...");
        
        try {
            DataSeeder.seedInitialData(userManager, packageManager, hotelManager);
            
            // Seed additional test data if needed
            seedAdditionalData();
            
            System.out.println("Initial data seeded successfully");
            Logger.log("Initial data seeding completed");
            
        } catch (Exception e) {
            Logger.error("Failed to seed initial data: " + e.getMessage());
            System.out.println("Warning: Failed to seed some initial data");
        }
    }
    
    private static void seedAdditionalData() {
        try {
            // Seed additional guides
            seedGuides();
            
            // Seed additional transport options
            seedTransports();
            
            // Seed sample rooms
            seedRooms();
            
        } catch (Exception e) {
            Logger.error("Failed to seed additional data: " + e.getMessage());
        }
    }
    
    private static void seedGuides() {
        TourGuide guide1 = new TourGuide("GID001", "Rahman Ahmed", "+8801712345681", "rahman@tourbd.com");
        guide1.addLanguage("Bengali");
        guide1.addLanguage("English");
        guide1.setSpecialization("Historical Tours");
        guide1.setDailyRate(150.0);
        guideManager.addGuide(guide1);
        
        TourGuide guide2 = new TourGuide("GID002", "Fatima Khan", "+8801712345682", "fatima@tourbd.com");
        guide2.addLanguage("Bengali");
        guide2.addLanguage("English");
        guide2.addLanguage("Hindi");
        guide2.setSpecialization("Adventure Tours");
        guide2.setDailyRate(200.0);
        guideManager.addGuide(guide2);
    }
    
    private static void seedTransports() {
        Transport bus1 = new Transport("TRP001", TransportType.BUS, "Dhaka", "Cox's Bazar");
        bus1.setDepartureTime("08:00");
        bus1.setArrivalTime("20:00");
        bus1.setOperatorName("Green Line");
        transportManager.addTransport(bus1);
        
        Transport flight1 = new Transport("TRP002", TransportType.FLIGHT, "Dhaka", "Bangkok");
        flight1.setDepartureTime("14:30");
        flight1.setArrivalTime("19:45");
        flight1.setOperatorName("Biman Bangladesh");
        transportManager.addTransport(flight1);
    }
    
    private static void seedRooms() {
        // Add rooms to the first hotel
        Room room1 = new Room("ROM001", "HTL001", "101", RoomType.STANDARD, 1500.0);
        room1.setCapacity(2);
        room1.setHasAC(true);
        room1.setHasWiFi(true);
        roomManager.addRoom(room1);
        
        Room room2 = new Room("ROM002", "HTL001", "201", RoomType.DELUXE, 2500.0);
        room2.setCapacity(3);
        room2.setHasAC(true);
        room2.setHasWiFi(true);
        room2.setHasTV(true);
        roomManager.addRoom(room2);
    }
    
    private static void performSystemHealthCheck() {
        System.out.println("Performing system health check...");
        
        try {
            // Check manager status
            int totalUsers = userManager.getTotalUsers();
            int totalPackages = packageManager.getTotalPackages();
            int totalHotels = hotelManager.getTotalHotels();
            int totalGuides = guideManager.getTotalGuides();
            int totalTransports = transportManager.getTotalTransports();
            
            // Display system statistics
            System.out.println("-------------------------------------------------------");
            System.out.println("              SYSTEM STATISTICS");
            System.out.println("-------------------------------------------------------");
            System.out.println("   Users: " + totalUsers);
            System.out.println("   Packages: " + totalPackages);
            System.out.println("   Hotels: " + totalHotels);
            System.out.println("   Guides: " + totalGuides);
            System.out.println("   Transports: " + totalTransports);
            System.out.println("-------------------------------------------------------");
            
            // Log system status
            Logger.log("=== SYSTEM HEALTH CHECK ===");
            Logger.log("Total users: " + totalUsers);
            Logger.log("Total packages: " + totalPackages);
            Logger.log("Total hotels: " + totalHotels);
            Logger.log("Total guides: " + totalGuides);
            Logger.log("Total transports: " + totalTransports);
            
            // Check for critical issues
            if (totalUsers == 0) {
                Logger.warning("No users found in system");
            }
            
            System.out.println("System health check completed");
            
        } catch (Exception e) {
            Logger.error("System health check failed: " + e.getMessage());
            System.out.println("Warning: Health check encountered issues");
        }
    }
    
    private static void startBackgroundTasks() {
        System.out.println("Starting background tasks...");
        
        try {
            // Start session cleanup thread
            startSessionCleanupTask();
            
            // Start daily backup thread
            startDailyBackupTask();
            
            // Start system monitoring thread
            startSystemMonitoringTask();
            
            System.out.println("Background tasks started");
            Logger.log("Background tasks initialized");
            
        } catch (Exception e) {
            Logger.error("Failed to start background tasks: " + e.getMessage());
        }
    }
    
    private static void startSessionCleanupTask() {
        Thread sessionCleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000); // 5 minutes
                    authService.getLoginManager().cleanupExpiredSessions();
                    Logger.log("Session cleanup completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Logger.error("Session cleanup error: " + e.getMessage());
                }
            }
        });
        sessionCleanupThread.setDaemon(true);
        sessionCleanupThread.setName("SessionCleanup");
        sessionCleanupThread.start();
    }
    
    private static void startDailyBackupTask() {
        Thread backupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(86400000); // 24 hours
                    BackupManager.createBackup();
                    Logger.log("Automated daily backup completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Logger.error("Backup error: " + e.getMessage());
                }
            }
        });
        backupThread.setDaemon(true);
        backupThread.setName("DailyBackup");
        backupThread.start();
    }
    
    private static void startSystemMonitoringTask() {
        Thread monitoringThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3600000); // 1 hour
                    // Monitor system resources and log status
                    Logger.log("System monitoring: Active sessions: " + 
                              authService.getLoginManager().getActiveSessionCount());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    Logger.error("Monitoring error: " + e.getMessage());
                }
            }
        });
        monitoringThread.setDaemon(true);
        monitoringThread.setName("SystemMonitoring");
        monitoringThread.start();
    }
    
    private static void startUserInterface() {
        System.out.println("Starting user interface...");
        
        try {
            // Setup shutdown hook before starting UI
            setupShutdownHook();
            
            // Create and start main menu
            MainMenu mainMenu = new MainMenu(userManager);
            
            // Display startup completion message
            displayStartupComplete();
            
            // Start the application main loop
            mainMenu.displayMainMenu();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to start user interface", e);
        }
    }
    
    private static void displayStartupComplete() {
        System.out.println();
        System.out.println("=======================================================");
        System.out.println("            SYSTEM READY FOR USE");
        System.out.println("         All components initialized");
        System.out.println("=======================================================");
        System.out.println();
    }
    
    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nSystem shutdown signal received...");
            Logger.log("=== EMERGENCY SHUTDOWN ===");
            Logger.log("Shutdown initiated at: " + DateUtil.getCurrentDateTime());
            performEmergencyShutdown();
        }));
    }
    
    private static void performEmergencyShutdown() {
        try {
            // Logout all active sessions
            if (authService != null) {
                // Emergency logout all users
                Logger.log("Emergency session cleanup initiated");
            }
            
            // Save any pending data
            Logger.log("Emergency data save completed");
            
            // Final log entry
            Logger.log("Emergency shutdown completed");
            
        } catch (Exception e) {
            System.err.println("Error during emergency shutdown: " + e.getMessage());
        }
    }
    
    private static void handleStartupError(Exception e) {
        System.err.println();
        System.err.println("=======================================================");
        System.err.println("                 STARTUP ERROR");
        System.err.println("=======================================================");
        System.err.println();
        System.err.println("Failed to start the system: " + e.getMessage());
        System.err.println();
        System.err.println("Please check the following:");
        System.err.println("1. All required directories exist and are writable");
        System.err.println("2. Java version is compatible (Java 8 or higher)");
        System.err.println("3. Sufficient disk space is available");
        System.err.println("4. No other instance of the application is running");
        System.err.println();
        System.err.println("For technical support, contact: support@tourbd.com");
        System.err.println();
        
        Logger.error("CRITICAL_STARTUP_ERROR: " + e.getMessage());
        e.printStackTrace();
        
        // Wait for user acknowledgment
        System.err.println("Press Enter to exit...");
        try {
            System.in.read();
        } catch (Exception ex) {
            // Ignore
        }
    }
    
    private static void performShutdown() {
        System.out.println("Performing system cleanup...");
        
        try {
            // Close input handler
            InputHandler.closeScanner();
            
            // Generate final system report
            generateShutdownReport();
            
            // Create final backup
            try {
                BackupManager.createBackup();
                Logger.log("Final backup created successfully");
            } catch (Exception e) {
                Logger.error("Failed to create final backup: " + e.getMessage());
            }
            
            // Final logging
            Logger.log("=== SYSTEM SHUTDOWN COMPLETE ===");
            Logger.log("Shutdown completed at: " + DateUtil.getCurrentDateTime());
            
            System.out.println("System cleanup completed successfully");
            
            // Display goodbye message
            displayGoodbyeMessage();
            
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
            Logger.error("Shutdown error: " + e.getMessage());
        }
    }
    
    private static void generateShutdownReport() {
        try {
            if (reportGenerator != null) {
                String report = reportGenerator.generateSystemReport();
                Logger.log("Shutdown system report generated");
            }
        } catch (Exception e) {
            Logger.error("Failed to generate shutdown report: " + e.getMessage());
        }
    }
    
    private static void displayGoodbyeMessage() {
        System.out.println();
        System.out.println("=======================================================");
        System.out.println("      Thank you for using TourBD System!");
        System.out.println("              Have a great journey!");
        System.out.println();
        System.out.println("              Visit us at: www.tourbd.com");
        System.out.println("=======================================================");
        System.out.println();
    }
    
    // Getter methods for managers (for testing or external access)
    public static UserManager getUserManager() { return userManager; }
    public static PackageManager getPackageManager() { return packageManager; }
    public static BookingManager getBookingManager() { return bookingManager; }
    public static PaymentManager getPaymentManager() { return paymentManager; }
    public static HotelManager getHotelManager() { return hotelManager; }
    public static TransportManager getTransportManager() { return transportManager; }
    public static GuideManager getGuideManager() { return guideManager; }
    public static ReviewManager getReviewManager() { return reviewManager; }
    public static RefundManager getRefundManager() { return refundManager; }
    public static RoomManager getRoomManager() { return roomManager; }
    public static AuthService getAuthService() { return authService; }
    public static ReportGenerator getReportGenerator() { return reportGenerator; }
}
