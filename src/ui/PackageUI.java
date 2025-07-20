package ui;

import util.InputHandler;
import util.IDGenerator;
import util.Logger;
import manager.PackageManager;
import manager.UserManager;
import model.*;
import enumtype.*;
import java.util.List;

public class PackageUI {
    private user currentUser;
    private UserManager userManager;
    private PackageManager packageManager;

    public PackageUI(user currentUser, UserManager userManager) {
        this.currentUser = currentUser;
        this.userManager = userManager;
        this.packageManager = new PackageManager();
    }

    public void browseTourPackages() {
        clearScreen();
        System.out.println("BROWSE TOUR PACKAGES");
        System.out.println("=======================================================");
        
        List<TourPackage> packages = packageManager.listActivePackages();
        if (packages.isEmpty()) {
            System.out.println("No packages available at the moment.");
        } else {
            displayPackageList(packages);
        }
        
        InputHandler.pressEnterToContinue();
    }

    public void searchPackages() {
        clearScreen();
        System.out.println("SEARCH PACKAGES");
        System.out.println("=======================================================");
        
        System.out.println("Search by:");
        System.out.println("1. Keyword (name/location)");
        System.out.println("2. Price Range");
        System.out.println("3. Category");
        System.out.println("4. Duration");
        
        int searchType = InputHandler.getInt("Enter search type (1-4): ");
        
        switch (searchType) {
            case 1:
                searchByKeyword();
                break;
            case 2:
                searchByPriceRange();
                break;
            case 3:
                searchByCategory();
                break;
            case 4:
                searchByDuration();
                break;
            default:
                System.out.println("Invalid search type!");
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void searchByKeyword() {
        String keyword = InputHandler.getString("Enter keyword: ");
        List<TourPackage> results = packageManager.searchPackages(keyword);
        
        System.out.println("\nSearch Results for: " + keyword);
        System.out.println("=======================================================");
        
        if (results.isEmpty()) {
            System.out.println("No packages found matching your search.");
        } else {
            displayPackageList(results);
        }
    }

    private void searchByPriceRange() {
        double minPrice = InputHandler.getDouble("Enter minimum price: ");
        double maxPrice = InputHandler.getDouble("Enter maximum price: ");
        
        List<TourPackage> results = packageManager.getPackagesByPriceRange(minPrice, maxPrice);
        
        System.out.println("\nPackages in price range: $" + minPrice + " - $" + maxPrice);
        System.out.println("=======================================================");
        
        if (results.isEmpty()) {
            System.out.println("No packages found in this price range.");
        } else {
            displayPackageList(results);
        }
    }

    private void searchByCategory() {
        System.out.println("\nSelect category:");
        System.out.println("1. LOCAL");
        System.out.println("2. INTERNATIONAL");
        System.out.println("3. SEASONAL");
        
        int choice = InputHandler.getInt("Enter choice (1-3): ");
        PackageCategory category = null;
        
        switch (choice) {
            case 1: category = PackageCategory.LOCAL; break;
            case 2: category = PackageCategory.INTERNATIONAL; break;
            case 3: category = PackageCategory.SEASONAL; break;
            default:
                System.out.println("Invalid category!");
                return;
        }
        
        List<TourPackage> results = packageManager.getPackagesByCategory(category);
        
        System.out.println("\n" + category.getDisplayName() + " Packages:");
        System.out.println("=======================================================");
        
        if (results.isEmpty()) {
            System.out.println("No packages found in this category.");
        } else {
            displayPackageList(results);
        }
    }

    private void searchByDuration() {
        int minDays = InputHandler.getInt("Enter minimum days: ");
        int maxDays = InputHandler.getInt("Enter maximum days: ");
        
        List<TourPackage> allPackages = packageManager.listActivePackages();
        List<TourPackage> results = new java.util.ArrayList<TourPackage>();
        
        for (TourPackage pkg : allPackages) {
            if (pkg.getDuration() >= minDays && pkg.getDuration() <= maxDays) {
                results.add(pkg);
            }
        }
        
        System.out.println("\nPackages with duration " + minDays + "-" + maxDays + " days:");
        System.out.println("=======================================================");
        
        if (results.isEmpty()) {
            System.out.println("No packages found with this duration.");
        } else {
            displayPackageList(results);
        }
    }

    public void viewPackageDetails(String packageId) {
        clearScreen();
        TourPackage tourPackage = packageManager.getPackageById(packageId);
        
        if (tourPackage == null) {
            System.out.println("Package not found!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        displayDetailedPackageInfo(tourPackage);
        InputHandler.pressEnterToContinue();
    }

    public void createNewPackage() {
        if (currentUser == null || !currentUser.getRole().equals("AGENT")) {
            System.out.println("Only travel agents can create packages!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        clearScreen();
        System.out.println("CREATE NEW PACKAGE");
        System.out.println("=======================================================");
        
        try {
            String packageId = IDGenerator.generatePackageId();
            String name = InputHandler.getString("Package Name: ");
            String location = InputHandler.getString("Location: ");
            double price = InputHandler.getDouble("Base Price ($): ");
            int duration = InputHandler.getInt("Duration (days): ");
            String description = InputHandler.getString("Description: ");
            
            TourPackage newPackage = new TourPackage(packageId, name, location, price, duration, description);
            
            // Set category
            PackageCategory category = selectPackageCategory();
            newPackage.setCategory(category);
            
            // Set tour type
            TourType tourType = selectTourType();
            newPackage.setTourType(tourType);
            
            // Set capacity
            int maxCapacity = InputHandler.getInt("Maximum capacity: ");
            newPackage.setMaxCapacity(maxCapacity);
            
            newPackage.setCreatedBy(currentUser.getId());
            
            packageManager.addPackage(newPackage);
            
            // Add to agent's managed packages
            if (currentUser instanceof TravelAgent) {
                ((TravelAgent) currentUser).getManagedPackages().add(packageId);
            }
            
            System.out.println("Package created successfully!");
            System.out.println("Package ID: " + packageId);
            
            Logger.log("Package created by agent " + currentUser.getEmail() + ": " + packageId);
            
        } catch (Exception e) {
            System.out.println("Error creating package: " + e.getMessage());
            Logger.error("Package creation error: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    public void editPackage(String packageId) {
        TourPackage tourPackage = packageManager.getPackageById(packageId);
        
        if (tourPackage == null) {
            System.out.println("Package not found!");
            return;
        }
        
        clearScreen();
        System.out.println("EDIT PACKAGE: " + packageId);
        System.out.println("=======================================================");
        
        System.out.println("Current Details:");
        displayDetailedPackageInfo(tourPackage);
        
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
        
        Logger.log("Package updated by " + currentUser.getEmail() + ": " + packageId);
    }

    public void displayPackageManagement() {
        if (currentUser == null || !currentUser.getRole().equals("ADMIN")) {
            System.out.println("Access denied!");
            InputHandler.pressEnterToContinue();
            return;
        }
        
        while (true) {
            clearScreen();
            System.out.println("=======================================================");
            System.out.println("               PACKAGE MANAGEMENT");
            System.out.println("=======================================================");
            System.out.println("1. View All Packages");
            System.out.println("2. Search Packages");
            System.out.println("3. Package Statistics");
            System.out.println("4. Activate/Deactivate Package");
            System.out.println("5. Add New Package");
            System.out.println("6. Edit Package");
            System.out.println("7. Back to Admin Menu");
            
            int choice = InputHandler.getInt("Enter choice: ");
            
            switch (choice) {
                case 1:
                    viewAllPackagesAdmin();
                    break;
                case 2:
                    searchPackages();
                    break;
                case 3:
                    viewPackageStatistics();
                    break;
                case 4:
                    togglePackageStatus();
                    break;
                case 5:
                    createNewPackageAsAdmin();
                    break;
                case 6:
                    editPackageAsAdmin();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice!");
                    InputHandler.pressEnterToContinue();
            }
        }
    }

    private void viewAllPackagesAdmin() {
        clearScreen();
        System.out.println("ALL PACKAGES (ADMIN VIEW)");
        System.out.println("=======================================================");
        
        List<TourPackage> packages = packageManager.listPackages();
        if (packages.isEmpty()) {
            System.out.println("No packages in the system.");
        } else {
            System.out.printf("%-12s %-25s %-15s %-10s %-8s %-10s%n", 
                "PACKAGE ID", "NAME", "LOCATION", "PRICE", "DAYS", "STATUS");
            System.out.println("-------------------------------------------------------");
            
            for (TourPackage pkg : packages) {
                System.out.printf("%-12s %-25s %-15s $%-9.2f %-8d %-10s%n",
                    pkg.getPackageId(),
                    pkg.getName().length() > 24 ? pkg.getName().substring(0, 24) : pkg.getName(),
                    pkg.getLocation().length() > 14 ? pkg.getLocation().substring(0, 14) : pkg.getLocation(),
                    pkg.getBasePrice(),
                    pkg.getDuration(),
                    pkg.isActive() ? "Active" : "Inactive");
            }
            
            System.out.println("\nTotal Packages: " + packages.size());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void viewPackageStatistics() {
        List<TourPackage> packages = packageManager.listPackages();
        long totalPackages = packages.size();
        long activePackages = 0;
        
        for (TourPackage pkg : packages) {
            if (pkg.isActive()) activePackages++;
        }
        
        long inactivePackages = totalPackages - activePackages;
        
        clearScreen();
        System.out.println("PACKAGE STATISTICS");
        System.out.println("=======================================================");
        System.out.println("Total Packages: " + totalPackages);
        System.out.println("Active Packages: " + activePackages);
        System.out.println("Inactive Packages: " + inactivePackages);
        
        if (!packages.isEmpty()) {
            double totalPrice = 0.0;
            int totalDuration = 0;
            
            for (TourPackage pkg : packages) {
                totalPrice += pkg.getBasePrice();
                totalDuration += pkg.getDuration();
            }
            
            double avgPrice = totalPrice / packages.size();
            double avgDuration = (double) totalDuration / packages.size();
            
            System.out.println("Average Price: $" + String.format("%.2f", avgPrice));
            System.out.println("Average Duration: " + String.format("%.1f", avgDuration) + " days");
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void togglePackageStatus() {
        String packageId = InputHandler.getString("Enter Package ID: ");
        TourPackage tourPackage = packageManager.getPackageById(packageId);
        
        if (tourPackage != null) {
            boolean newStatus = !tourPackage.isActive();
            tourPackage.setActive(newStatus);
            packageManager.updatePackage(tourPackage);
            
            System.out.println("Package " + packageId + " has been " + 
                (newStatus ? "activated" : "deactivated"));
            Logger.log("Admin changed package status: " + packageId + " - " + newStatus);
        } else {
            System.out.println("Package not found!");
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void createNewPackageAsAdmin() {
        clearScreen();
        System.out.println("CREATE NEW PACKAGE (ADMIN)");
        System.out.println("=======================================================");
        
        try {
            String packageId = IDGenerator.generatePackageId();
            String name = InputHandler.getString("Package Name: ");
            String location = InputHandler.getString("Location: ");
            double price = InputHandler.getDouble("Base Price ($): ");
            int duration = InputHandler.getInt("Duration (days): ");
            String description = InputHandler.getString("Description: ");
            
            TourPackage newPackage = new TourPackage(packageId, name, location, price, duration, description);
            
            PackageCategory category = selectPackageCategory();
            newPackage.setCategory(category);
            
            TourType tourType = selectTourType();
            newPackage.setTourType(tourType);
            
            int maxCapacity = InputHandler.getInt("Maximum capacity: ");
            newPackage.setMaxCapacity(maxCapacity);
            
            newPackage.setCreatedBy(currentUser.getId());
            
            packageManager.addPackage(newPackage);
            
            System.out.println("Package created successfully!");
            System.out.println("Package ID: " + packageId);
            
            Logger.log("Package created by admin " + currentUser.getEmail() + ": " + packageId);
            
        } catch (Exception e) {
            System.out.println("Error creating package: " + e.getMessage());
            Logger.error("Package creation error: " + e.getMessage());
        }
        
        InputHandler.pressEnterToContinue();
    }

    private void editPackageAsAdmin() {
        String packageId = InputHandler.getString("Enter Package ID to edit: ");
        editPackage(packageId);
    }

    private void displayPackageList(List<TourPackage> packages) {
        System.out.printf("%-12s %-25s %-15s %-10s %-5s %-8s%n", 
            "PACKAGE ID", "NAME", "LOCATION", "PRICE", "DAYS", "SLOTS");
        System.out.println("-------------------------------------------------------");
        
        for (TourPackage pkg : packages) {
            System.out.printf("%-12s %-25s %-15s $%-9.2f %-5d %-8s%n",
                pkg.getPackageId(),
                pkg.getName().length() > 24 ? pkg.getName().substring(0, 24) : pkg.getName(),
                pkg.getLocation().length() > 14 ? pkg.getLocation().substring(0, 14) : pkg.getLocation(),
                pkg.getBasePrice(),
                pkg.getDuration(),
                pkg.getAvailableSlots() + "/" + pkg.getMaxCapacity());
        }
        
        System.out.println("\nTotal: " + packages.size() + " packages");
        
        // Show package details option
        if (!packages.isEmpty()) {
            String packageId = InputHandler.getString("\nEnter Package ID to view details (or press Enter to skip): ");
            if (!packageId.trim().isEmpty()) {
                viewPackageDetails(packageId);
            }
        }
    }

    private void displayDetailedPackageInfo(TourPackage tourPackage) {
        System.out.println("PACKAGE DETAILS");
        System.out.println("=======================================================");
        System.out.println("Package ID: " + tourPackage.getPackageId());
        System.out.println("Name: " + tourPackage.getName());
        System.out.println("Location: " + tourPackage.getLocation());
        System.out.println("Base Price: $" + tourPackage.getBasePrice());
        System.out.println("Duration: " + tourPackage.getDuration() + " days");
        System.out.println("Category: " + (tourPackage.getCategory() != null ? tourPackage.getCategory().getDisplayName() : "Not Set"));
        System.out.println("Tour Type: " + (tourPackage.getTourType() != null ? tourPackage.getTourType().getDisplayName() : "Not Set"));
        System.out.println("Description: " + tourPackage.getDescription());
        System.out.println("Status: " + (tourPackage.isActive() ? "Active" : "Inactive"));
        System.out.println("Capacity: " + tourPackage.getCurrentBookings() + "/" + tourPackage.getMaxCapacity());
        System.out.println("Available Slots: " + tourPackage.getAvailableSlots());
        System.out.println("Availability: " + (tourPackage.isAvailable() ? "Available" : "Fully Booked"));
    }

    private PackageCategory selectPackageCategory() {
        System.out.println("\nSelect Package Category:");
        System.out.println("1. LOCAL");
        System.out.println("2. INTERNATIONAL");
        System.out.println("3. SEASONAL");
        
        while (true) {
            int choice = InputHandler.getInt("Enter choice (1-3): ");
            switch (choice) {
                case 1: return PackageCategory.LOCAL;
                case 2: return PackageCategory.INTERNATIONAL;
                case 3: return PackageCategory.SEASONAL;
                default:
                    System.out.println("Invalid choice! Please select 1-3.");
            }
        }
    }

    private TourType selectTourType() {
        System.out.println("\nSelect Tour Type:");
        System.out.println("1. FAMILY");
        System.out.println("2. ADVENTURE");
        System.out.println("3. HISTORICAL");
        System.out.println("4. COUPLE");
        
        while (true) {
            int choice = InputHandler.getInt("Enter choice (1-4): ");
            switch (choice) {
                case 1: return TourType.FAMILY;
                case 2: return TourType.ADVENTURE;
                case 3: return TourType.HISTORICAL;
                case 4: return TourType.COUPLE;
                default:
                    System.out.println("Invalid choice! Please select 1-4.");
            }
        }
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}