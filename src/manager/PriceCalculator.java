package manager;

import model.TourPackage;
import enumtype.PackageCategory;
import enumtype.TourType;

public class PriceCalculator {
    
    public double calculateFinalPrice(TourPackage tourPackage, String userType, int numberOfPeople) {
        double basePrice = tourPackage.getBasePrice() * numberOfPeople;
        double finalPrice = basePrice;
        
        // Apply user type discounts
        switch (userType.toUpperCase()) {
            case "TOURIST":
                // Regular tourist - no additional discount
                break;
            case "AGENT":
                finalPrice *= 0.95; // 5% agent discount
                break;
            case "ADMIN":
                finalPrice *= 0.90; // 10% admin discount
                break;
        }
        
        // Apply package category discounts
        if (tourPackage.getCategory() != null) {
            switch (tourPackage.getCategory()) {
                case LOCAL:
                    finalPrice *= 0.98; // 2% local discount
                    break;
                case SEASONAL:
                    finalPrice *= 0.92; // 8% seasonal discount
                    break;
                case INTERNATIONAL:
                    // No additional discount
                    break;
            }
        }
        
        // Group discounts
        if (numberOfPeople >= 10) {
            finalPrice *= 0.85; // 15% group discount
        } else if (numberOfPeople >= 5) {
            finalPrice *= 0.90; // 10% group discount
        } else if (numberOfPeople >= 3) {
            finalPrice *= 0.95; // 5% small group discount
        }
        
        return finalPrice;
    }
}
