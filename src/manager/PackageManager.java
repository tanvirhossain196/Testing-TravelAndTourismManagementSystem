package manager;

import model.TourPackage;
import enumtype.PackageCategory;
import enumtype.TourType;
import util.Logger;
import util.FileHandler;
import java.util.*;
import java.util.stream.Collectors;

public class PackageManager {
    private Map<String, TourPackage> packages;
    private static final String PACKAGES_FILE = "packages.dat";

    public PackageManager() {
        this.packages = new HashMap<>();
        loadPackagesFromFile();
    }

    public void addPackage(TourPackage tourPackage) {
        if (tourPackage != null && !packages.containsKey(tourPackage.getPackageId())) {
            packages.put(tourPackage.getPackageId(), tourPackage);
            savePackagesToFile();
            Logger.log("Package added: " + tourPackage.getName());
        }
    }

    public void removePackage(String packageId) {
        TourPackage removedPackage = packages.remove(packageId);
        if (removedPackage != null) {
            savePackagesToFile();
            Logger.log("Package removed: " + removedPackage.getName());
        }
    }

    public List<TourPackage> listPackages() {
        return new ArrayList<>(packages.values());
    }

    public List<TourPackage> listActivePackages() {
        return packages.values().stream()
                .filter(TourPackage::isActive)
                .collect(Collectors.toList());
    }

    public TourPackage getPackageById(String packageId) {
        return packages.get(packageId);
    }

    public List<TourPackage> searchPackages(String keyword) {
        return packages.values().stream()
                .filter(pkg -> pkg.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                              pkg.getLocation().toLowerCase().contains(keyword.toLowerCase()) ||
                              pkg.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<TourPackage> getPackagesByCategory(PackageCategory category) {
        return packages.values().stream()
                .filter(pkg -> pkg.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<TourPackage> getPackagesByTourType(TourType tourType) {
        return packages.values().stream()
                .filter(pkg -> pkg.getTourType() == tourType)
                .collect(Collectors.toList());
    }

    public List<TourPackage> getPackagesByPriceRange(double minPrice, double maxPrice) {
        return packages.values().stream()
                .filter(pkg -> pkg.getBasePrice() >= minPrice && pkg.getBasePrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public List<TourPackage> getPackagesByDuration(int minDays, int maxDays) {
        return packages.values().stream()
                .filter(pkg -> pkg.getDuration() >= minDays && pkg.getDuration() <= maxDays)
                .collect(Collectors.toList());
    }

    public List<TourPackage> getPackagesByLocation(String location) {
        return packages.values().stream()
                .filter(pkg -> pkg.getLocation().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<TourPackage> getPackagesByCreator(String creatorId) {
        return packages.values().stream()
                .filter(pkg -> creatorId.equals(pkg.getCreatedBy()))
                .collect(Collectors.toList());
    }

    public void updatePackage(TourPackage tourPackage) {
        if (tourPackage != null && packages.containsKey(tourPackage.getPackageId())) {
            packages.put(tourPackage.getPackageId(), tourPackage);
            savePackagesToFile();
            Logger.log("Package updated: " + tourPackage.getName());
        }
    }

    public void activatePackage(String packageId) {
        TourPackage pkg = packages.get(packageId);
        if (pkg != null) {
            pkg.setActive(true);
            updatePackage(pkg);
        }
    }

    public void deactivatePackage(String packageId) {
        TourPackage pkg = packages.get(packageId);
        if (pkg != null) {
            pkg.setActive(false);
            updatePackage(pkg);
        }
    }

    public int getTotalPackages() {
        return packages.size();
    }

    public int getActivePackagesCount() {
        return (int) packages.values().stream().filter(TourPackage::isActive).count();
    }

    public double getAveragePackagePrice() {
        return packages.values().stream()
                .mapToDouble(TourPackage::getBasePrice)
                .average()
                .orElse(0.0);
    }

    public TourPackage getMostExpensivePackage() {
        return packages.values().stream()
                .max(Comparator.comparing(TourPackage::getBasePrice))
                .orElse(null);
    }

    public TourPackage getCheapestPackage() {
        return packages.values().stream()
                .min(Comparator.comparing(TourPackage::getBasePrice))
                .orElse(null);
    }

    public Map<PackageCategory, Long> getPackageCountByCategory() {
        return packages.values().stream()
                .filter(pkg -> pkg.getCategory() != null)
                .collect(Collectors.groupingBy(TourPackage::getCategory, Collectors.counting()));
    }

    public Map<TourType, Long> getPackageCountByTourType() {
        return packages.values().stream()
                .filter(pkg -> pkg.getTourType() != null)
                .collect(Collectors.groupingBy(TourPackage::getTourType, Collectors.counting()));
    }

    public List<TourPackage> getAvailablePackages() {
        return packages.values().stream()
                .filter(TourPackage::isAvailable)
                .collect(Collectors.toList());
    }

    public List<TourPackage> getFullyBookedPackages() {
        return packages.values().stream()
                .filter(pkg -> !pkg.isAvailable())
                .collect(Collectors.toList());
    }

    private void loadPackagesFromFile() {
        try {
            List<String> lines = FileHandler.readFromFile(PACKAGES_FILE);
            for (String line : lines) {
                TourPackage pkg = parsePackageFromString(line);
                if (pkg != null) {
                    packages.put(pkg.getPackageId(), pkg);
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to load packages from file: " + e.getMessage());
        }
    }

    private void savePackagesToFile() {
        try {
            FileHandler.clearFile(PACKAGES_FILE);
            for (TourPackage pkg : packages.values()) {
                String packageString = convertPackageToString(pkg);
                FileHandler.writeToFile(PACKAGES_FILE, packageString);
            }
        } catch (Exception e) {
            Logger.error("Failed to save packages to file: " + e.getMessage());
        }
    }

    private TourPackage parsePackageFromString(String packageString) {
        try {
            String[] parts = packageString.split("\\|");
            if (parts.length >= 6) {
                TourPackage pkg = new TourPackage(parts[0], parts[1], parts[2], 
                                                Double.parseDouble(parts[3]), 
                                                Integer.parseInt(parts[4]), parts[5]);
                if (parts.length > 6) {
                    pkg.setActive(Boolean.parseBoolean(parts[6]));
                }
                if (parts.length > 7) {
                    pkg.setCategory(PackageCategory.valueOf(parts[7]));
                }
                if (parts.length > 8) {
                    pkg.setTourType(TourType.valueOf(parts[8]));
                }
                return pkg;
            }
        } catch (Exception e) {
            Logger.error("Failed to parse package: " + e.getMessage());
        }
        return null;
    }

    private String convertPackageToString(TourPackage pkg) {
        return String.join("|",
            pkg.getPackageId(), pkg.getName(), pkg.getLocation(),
            String.valueOf(pkg.getBasePrice()), String.valueOf(pkg.getDuration()),
            pkg.getDescription(), String.valueOf(pkg.isActive()),
            pkg.getCategory() != null ? pkg.getCategory().name() : "",
            pkg.getTourType() != null ? pkg.getTourType().name() : "");
    }
}
