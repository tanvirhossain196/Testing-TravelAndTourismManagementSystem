package ui;

import util.InputHandler;
import util.Logger;
import manager.*;
import model.user;
import model.TravelAgent;
import java.util.List;

public class AgentMenu {
    private TravelAgent agent;
    private UserManager userManager;
    private PackageManager packageManager;
    private BookingManager bookingManager;
    private boolean isRunning;

    public AgentMenu(user user, UserManager userManager) {
        this.agent = (TravelAgent) user;
        this.userManager = userManager;
        this.packageManager = new PackageManager();
        this.bookingManager = new BookingManager();
        this.isRunning = true;
    }

    public void displayAgentMenu() {
        while (isRunning) {
            clearScreen();
            printAgentHeader();
            printAgentMenuOptions();
            
            int choice = InputHandler.getInt("Enter your choice: ");
            handleAgentMenuChoice(choice);
        }
    }

    private void printAgentHeader() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                  AGENT PANEL                     ║");
        System.out.println("║              Welcome, " + String.format("%-20s", agent.getName()) + "     ║");
        System.out.println("║              Agency: " + String.format("%-21s", 
            agent.getAgencyName() != null ? agent.getAgencyName() : "Not Set") + "     ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printAgentMenuOptions() {
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│                AGENT FUNCTIONS                  │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│ 1. 📦 Create New Package                       │");
        System.out.println("│ 2. ✏️  Edit My Packages                        │");
        System.out.println("│ 3. 📋 View All Packages                        │");
        System.out.println("│ 4. 🗺️  Manage Itineraries                      │");
        System.out.println("│ 5. 📊 View Package Analytics                   │");
        System.out.println("│ 6. 💰 Commission Reports                       │");
        System.out.println("│ 7. 👤 My Profile                               │");
        System.out.println("│ 8. 🚪 Logout                                   │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();
    }

    private void handleAgentMenuChoice(int choice) {
        switch (choice) {
            case 1:
                createNewPackage();
                break;
            case 2:
                editMyPackages();
                break;
            case 3:
                viewAllPackages();
                break;
            case 4:
                manageItineraries();
                break;
            case 5:
                viewPackageAnalytics();
                break;
            case 6:
                viewCommissionReports();
                break;
            case 7:
                viewProfile();
                break;
            case 8:
                logout();
                break;
            default:
                System.out.println("❌ Invalid choice! Please select 1-8.");
                InputHandler.pressEnterToContinue();
        }
    }

    private void createNewPackage() {
        clearScreen();
        System.out.println("CREATE NEW PACKAGE");
        System.out.println("=======================================================");
        
        try {
            String packageId = util.IDGenerator.generatePackageId();
            String name = InputHandler.getString("Package Name: ");
            String location = InputHandler.getString("Location: ");
            double price = InputHandler.getDouble("Base Price ($): ");
            int duration = InputHandler.getInt("Duration (days): ");
            String description = InputHandler.getString("Description: ");
            
            model.TourPackage newPackage = new model.TourPackage(packageId, name, location, price, duration, description);
            
            // Set category
            enumtype.PackageCategory category = selectPackageCategory();
            newPackage.setCategory(category);
            
            // Set tour type
            enumtype.TourType tourType = selectTourType();
            newPackage.setTourType(tourType);
            
            // Set capacity
            int maxCapacity = InputHandler.getInt("Maximum capacity: ");
            newPackage.setMaxCapacity(maxCapacity);
            
            newPackage.setCreatedBy(agent.getId());
            
            packageManager.addPackage(newPackage);
            
            // Add to agent's managed packages
            agent.getManagedPackages().add(packageId);
            userManager.updateUser(agent);
            
            System.out.println("Package created successfully!");
            System.out.println("Package ID: " + packageId);
            
            Logger.log("Package created by agent " + agent.getEmail() + ": " + packageId);
            
        } catch (Exception e) {
            System.out.println("Error creating package: " + e.getMessage());
            Logger.error("Package creation error: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private enumtype.PackageCategory selectPackageCategory() {
        System.out.println("\nSelect Package Category:");
        System.out.println("1. LOCAL");
        System.out.println("2. INTERNATIONAL");
        System.out.println("3. SEASONAL");
        
        while (true) {
            int choice = InputHandler.getInt("Enter choice (1-3): ");
            switch (choice) {
                case 1: return enumtype.PackageCategory.LOCAL;
                case 2: return enumtype.PackageCategory.INTERNATIONAL;
                case 3: return enumtype.PackageCategory.SEASONAL;
                default:
                    System.out.println("Invalid choice! Please select 1-3.");
            }
        }
    }

    private enumtype.TourType selectTourType() {
        System.out.println("\nSelect Tour Type:");
        System.out.println("1. FAMILY");
        System.out.println("2. ADVENTURE");
        System.out.println("3. HISTORICAL");
        System.out.println("4. COUPLE");
        
        while (true) {
            int choice = InputHandler.getInt("Enter choice (1-4): ");
            switch (choice) {
                case 1: return enumtype.TourType.FAMILY;
                case 2: return enumtype.TourType.ADVENTURE;
                case 3: return enumtype.TourType.HISTORICAL;
                case 4: return enumtype.TourType.COUPLE;
                default:
                    System.out.println("Invalid choice! Please select 1-4.");
            }
        }
    }

    private void editMyPackages() {
        clearScreen();
        System.out.println("✏️ EDIT MY PACKAGES");
        System.out.println("═══════════════════════════════════════════════════");
        
        List<String> myPackages = agent.getManagedPackages();
        if (myPackages.isEmpty()) {
            System.out.println("You haven't created any packages yet.");
            System.out.println("Would you like to create a new package?");
            if (InputHandler.getBoolean("Create new package?")) {
                createNewPackage();
                return;
            }
        } else {
            System.out.println("Your packages:");
            for (int i = 0; i < myPackages.size(); i++) {
                System.out.println((i + 1) + ". " + myPackages.get(i));
            }
            
            String packageId = InputHandler.getString("Enter Package ID to edit: ");
            if (myPackages.contains(packageId)) {
                editPackage(packageId);
            } else {
                System.out.println("❌ You don't have permission to edit this package.");
            }
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void editPackage(String packageId) {
        model.TourPackage tourPackage = packageManager.getPackageById(packageId);
        
        if (tourPackage == null) {
            System.out.println("Package not found!");
            return;
        }
        
        clearScreen();
        System.out.println("EDIT PACKAGE: " + packageId);
        System.out.println("=======================================================");
        
        System.out.println("Current Details:");
        System.out.println("Name: " + tourPackage.getName());
        System.out.println("Location: " + tourPackage.getLocation());
        System.out.println("Price: $" + tourPackage.getBasePrice());
        System.out.println("Duration: " + tourPackage.getDuration() + " days");
        System.out.println("Description: " + tourPackage.getDescription());
        
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Name");
        System.out.println("2. Price");
        System.out.println("3. Description");
        System.out.println("4. Duration");
        System.out.println("5. Maximum Capacity");
        System.out.println("6. Cancel");
        
        int choice = InputHandler.getInt("Enter choice: ");
        
        switch (choice) {
            case 1:
                String newName = InputHandler.getString("New name: ");
                tourPackage.setName(newName);
                break;
            case 2:
                double newPrice = InputHandler.getDouble("New price: ");
                tourPackage.setBasePrice(newPrice);
                break;
            case 3:
                String newDescription = InputHandler.getString("New description: ");
                tourPackage.setDescription(newDescription);
                break;
            case 4:
                int newDuration = InputHandler.getInt("New duration (days): ");
                tourPackage.setDuration(newDuration);
                break;
            case 5:
                int newCapacity = InputHandler.getInt("New maximum capacity: ");
                tourPackage.setMaxCapacity(newCapacity);
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid choice!");
                return;
        }
        
        packageManager.updatePackage(tourPackage);
        System.out.println("Package updated successfully!");
        
        Logger.log("Package updated by " + agent.getEmail() + ": " + packageId);
    }

    private void viewAllPackages() {
        clearScreen();
        System.out.println("ALL TOUR PACKAGES");
        System.out.println("=======================================================");
        
        List<model.TourPackage> packages = packageManager.listActivePackages();
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
        
        InputHandler.pressEnterToContinue();
    }

    private void manageItineraries() {
        clearScreen();
        System.out.println("🗺️ MANAGE ITINERARIES");
        System.out.println("═══════════════════════════════════════════════════");
        
        List<String> myPackages = agent.getManagedPackages();
        if (myPackages.isEmpty()) {
            System.out.println("You need to create packages first.");
        } else {
            System.out.println("Select package to manage itinerary:");
            for (int i = 0; i < myPackages.size(); i++) {
                System.out.println((i + 1) + ". " + myPackages.get(i));
            }
            
            String packageId = InputHandler.getString("Enter Package ID: ");
            if (myPackages.contains(packageId)) {
                managePackageItinerary(packageId);
            } else {
                System.out.println("❌ Invalid package ID.");
            }
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void managePackageItinerary(String packageId) {
        clearScreen();
        System.out.println("🗺️ ITINERARY FOR PACKAGE: " + packageId);
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.println("1. Add Day Activity");
        System.out.println("2. View Complete Itinerary");
        System.out.println("3. Back");
        
        int choice = InputHandler.getInt("Enter choice: ");
        switch (choice) {
            case 1:
                addDayActivity(packageId);
                break;
            case 2:
                viewCompleteItinerary(packageId);
                break;
            case 3:
                return;
        }
    }

    private void addDayActivity(String packageId) {
        int day = InputHandler.getInt("Enter day number: ");
        String activity = InputHandler.getString("Enter activity description: ");
        String location = InputHandler.getString("Enter location: ");
        
        System.out.println("✅ Activity added for Day " + day);
        System.out.println("Activity: " + activity);
        System.out.println("Location: " + location);
        
        Logger.log("Agent " + agent.getEmail() + " added itinerary for package " + packageId);
    }

    private void viewCompleteItinerary(String packageId) {
        System.out.println("📋 COMPLETE ITINERARY - " + packageId);
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Day 1: Arrival and hotel check-in");
        System.out.println("Day 2: City tour and cultural sites");
        System.out.println("Day 3: Adventure activities");
        System.out.println("Day 4: Departure");
        System.out.println("\nFeature under development for dynamic itineraries...");
    }

    private void viewPackageAnalytics() {
        clearScreen();
        System.out.println("📊 PACKAGE ANALYTICS");
        System.out.println("═══════════════════════════════════════════════════");
        
        List<String> myPackages = agent.getManagedPackages();
        System.out.println("Total Packages Created: " + myPackages.size());
        System.out.println("Active Packages: " + myPackages.size());
        System.out.println("Total Bookings: 0"); // Placeholder
        System.out.println("Average Rating: 4.5/5"); // Placeholder
        
        System.out.println("\nMost Popular Package: PKG001");
        System.out.println("Highest Revenue Package: PKG002");
        System.out.println("Recent Bookings: 5 this month");
        
        InputHandler.pressEnterToContinue();
    }

    private void viewCommissionReports() {
        clearScreen();
        System.out.println("💰 COMMISSION REPORTS");
        System.out.println("═══════════════════════════════════════════════════");
        
        double commissionRate = agent.getCommissionRate() * 100;
        System.out.println("Commission Rate: " + commissionRate + "%");
        System.out.println("This Month Earnings: $500.00"); // Placeholder
        System.out.println("Last Month Earnings: $750.00"); // Placeholder
        System.out.println("Total Earnings: $2,500.00"); // Placeholder
        
        System.out.println("\nTop Earning Packages:");
        System.out.println("1. Thailand Paradise - $200.00");
        System.out.println("2. Cox's Bazar Tour - $150.00");
        System.out.println("3. Sundarbans Adventure - $100.00");
        
        InputHandler.pressEnterToContinue();
    }

    private void viewProfile() {
        clearScreen();
        System.out.println("👤 MY PROFILE");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Name: " + agent.getName());
        System.out.println("Email: " + agent.getEmail());
        System.out.println("Phone: " + agent.getPhone());
        System.out.println("Agency Name: " + (agent.getAgencyName() != null ? agent.getAgencyName() : "Not Set"));
        System.out.println("License Number: " + (agent.getLicenseNumber() != null ? agent.getLicenseNumber() : "Not Set"));
        System.out.println("Commission Rate: " + (agent.getCommissionRate() * 100) + "%");
        System.out.println("Packages Created: " + agent.getManagedPackages().size());
        System.out.println("Member Since: " + agent.getCreatedDate());
        
        if (InputHandler.getBoolean("Do you want to update your profile?")) {
            updateProfile();
        } else {
            InputHandler.pressEnterToContinue();
        }
    }

    private void updateProfile() {
        System.out.println("\n📝 UPDATE PROFILE");
        
        String newPhone = InputHandler.getString("New Phone (current: " + agent.getPhone() + "): ");
        if (!newPhone.trim().isEmpty()) {
            agent.setPhone(newPhone);
        }
        
        String newAgencyName = InputHandler.getString("Agency Name (current: " + 
            (agent.getAgencyName() != null ? agent.getAgencyName() : "Not Set") + "): ");
        if (!newAgencyName.trim().isEmpty()) {
            agent.setAgencyName(newAgencyName);
        }
        
        String newLicenseNumber = InputHandler.getString("License Number (current: " + 
            (agent.getLicenseNumber() != null ? agent.getLicenseNumber() : "Not Set") + "): ");
        if (!newLicenseNumber.trim().isEmpty()) {
            agent.setLicenseNumber(newLicenseNumber);
        }
        
        userManager.updateUser(agent);
        System.out.println("✅ Profile updated successfully!");
        
        InputHandler.pressEnterToContinue();
    }

    private void logout() {
        Logger.log("Agent logged out: " + agent.getEmail());
        isRunning = false;
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}