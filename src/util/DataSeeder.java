package util;

import model.*;
import manager.*;
import enumtype.*;

public class DataSeeder {
    
    public static void seedInitialData(UserManager userManager, PackageManager packageManager, 
                                     HotelManager hotelManager) {
        seedUsers(userManager);
        seedPackages(packageManager);
        seedHotels(hotelManager);
        Logger.log("Initial data seeded successfully");
    }

    private static void seedUsers(UserManager userManager) {
        // Create Admin
        Admin admin = new Admin("USR001", "System Admin", "admin@travel.com", "admin123", "+8801712345678");
        userManager.addUser(admin);

        // Create Travel Agents
        TravelAgent agent1 = new TravelAgent("USR002", "John Agent", "john@travel.com", "agent123", "+8801712345679");
        agent1.setAgencyName("Travel Pro Agency");
        userManager.addUser(agent1);

        // Create Tourists
        Tourist tourist1 = new Tourist("USR003", "Alice Tourist", "alice@email.com", "tourist123", "+8801712345680");
        tourist1.setNationality("Bangladesh");
        userManager.addUser(tourist1);

        Logger.log("Sample users created");
    }

    private static void seedPackages(PackageManager packageManager) {
        TourPackage pkg001 = new TourPackage("PKG001", "Cox's Bazar Beach Tour", "Cox's Bazar", 15000.0, 3, "Beautiful beach resort with sea view");
        pkg001.setCategory(PackageCategory.LOCAL);
        pkg001.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg001);

        TourPackage pkg002 = new TourPackage("PKG002", "Sundarbans Mangrove Tour", "Sundarbans", 12000.0, 2, "Wildlife and mangrove forest adventure");
        pkg002.setCategory(PackageCategory.LOCAL);
        pkg002.setTourType(TourType.ADVENTURE);
        packageManager.addPackage(pkg002);

        TourPackage pkg003 = new TourPackage("PKG003", "Srimangal Tea Garden Tour", "Srimangal", 10000.0, 2, "Visit the famous tea gardens and enjoy fresh tea");
        pkg003.setCategory(PackageCategory.LOCAL);
        pkg003.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg003);

        TourPackage pkg004 = new TourPackage("PKG004", "Sylhet City Tour", "Sylhet", 11000.0, 2, "Explore the natural beauty of Sylhet including Ratargul Swamp Forest");
        pkg004.setCategory(PackageCategory.LOCAL);
        pkg004.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg004);

        TourPackage pkg005 = new TourPackage("PKG005", "Rangamati Hill Tracts Tour", "Rangamati", 13000.0, 3, "Hill tracts and lake tour with tribal culture experience");
        pkg005.setCategory(PackageCategory.LOCAL);
        pkg005.setTourType(TourType.ADVENTURE);
        packageManager.addPackage(pkg005);

        TourPackage pkg006 = new TourPackage("PKG006", "Bandarban Adventure Tour", "Bandarban", 14000.0, 3, "Adventure and trekking in Bandarban hills with waterfall visits");
        pkg006.setCategory(PackageCategory.LOCAL);
        pkg006.setTourType(TourType.ADVENTURE);
        packageManager.addPackage(pkg006);

        TourPackage pkg007 = new TourPackage("PKG007", "Dhaka City Heritage Tour", "Dhaka", 8000.0, 1, "Historical sites in Dhaka including Lalbagh Fort and Ahsan Manzil");
        pkg007.setCategory(PackageCategory.LOCAL);
        pkg007.setTourType(TourType.HISTORICAL);
        packageManager.addPackage(pkg007);

        TourPackage pkg008 = new TourPackage("PKG008", "Paharpur Buddhist Monastery Tour", "Paharpur", 9000.0, 1, "Visit the ancient Buddhist monastery - UNESCO World Heritage Site");
        pkg008.setCategory(PackageCategory.LOCAL);
        pkg008.setTourType(TourType.HISTORICAL);
        packageManager.addPackage(pkg008);

        TourPackage pkg009 = new TourPackage("PKG009", "Mahasthangarh Archaeological Tour", "Bogura", 8500.0, 1, "Explore the ancient city ruins and archaeological sites");
        pkg009.setCategory(PackageCategory.LOCAL);
        pkg009.setTourType(TourType.HISTORICAL);
        packageManager.addPackage(pkg009);

        TourPackage pkg010 = new TourPackage("PKG010", "Kuakata Sea Beach Tour", "Kuakata", 12000.0, 2, "Sunset and sunrise sea beach - the only place to see both from same spot");
        pkg010.setCategory(PackageCategory.LOCAL);
        pkg010.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg010);

        TourPackage pkg011 = new TourPackage("PKG011", "Panchagarh Tea Garden Tour", "Panchagarh", 9500.0, 2, "Tea garden and nature tour in the northern hills");
        pkg011.setCategory(PackageCategory.LOCAL);
        pkg011.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg011);

        TourPackage pkg012 = new TourPackage("PKG012", "Tangail Handloom Village Tour", "Tangail", 7000.0, 1, "Visit traditional handloom villages and learn weaving techniques");
        pkg012.setCategory(PackageCategory.LOCAL);
        pkg012.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg012);

        TourPackage pkg013 = new TourPackage("PKG013", "Jaflong Hill and Tea Garden Tour", "Jaflong", 11000.0, 2, "Hills and tea garden tour near India border with stone collection");
        pkg013.setCategory(PackageCategory.LOCAL);
        pkg013.setTourType(TourType.ADVENTURE);
        packageManager.addPackage(pkg013);

        TourPackage pkg014 = new TourPackage("PKG014", "Chittagong City Tour", "Chittagong", 10000.0, 1, "Explore the port city of Chittagong with hills and beaches");
        pkg014.setCategory(PackageCategory.LOCAL);
        pkg014.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg014);

        TourPackage pkg015 = new TourPackage("PKG015", "Saint Martin's Island Tour", "Saint Martin's Island", 18000.0, 3, "Island beach and coral tour - Bangladesh's only coral island");
        pkg015.setCategory(PackageCategory.LOCAL);
        pkg015.setTourType(TourType.COUPLE);
        packageManager.addPackage(pkg015);

        TourPackage pkg016 = new TourPackage("PKG016", "Myanmar Border Tour", "Bandarban", 16000.0, 3, "Visit the border area with Myanmar and explore remote villages");
        pkg016.setCategory(PackageCategory.LOCAL);
        pkg016.setTourType(TourType.ADVENTURE);
        packageManager.addPackage(pkg016);

        TourPackage pkg017 = new TourPackage("PKG017", "Lalbagh Fort Tour", "Dhaka", 7500.0, 1, "Historical fort in Dhaka with Mughal architecture");
        pkg017.setCategory(PackageCategory.LOCAL);
        pkg017.setTourType(TourType.HISTORICAL);
        packageManager.addPackage(pkg017);

        TourPackage pkg018 = new TourPackage("PKG018", "Kaptai Lake Tour", "Rangamati", 13000.0, 2, "Boat tour on Kaptai Lake with hanging bridge and tribal villages");
        pkg018.setCategory(PackageCategory.LOCAL);
        pkg018.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg018);

        TourPackage pkg019 = new TourPackage("PKG019", "Bagerhat Mosque Tour", "Bagerhat", 9000.0, 1, "UNESCO World Heritage mosques including Sixty Dome Mosque");
        pkg019.setCategory(PackageCategory.LOCAL);
        pkg019.setTourType(TourType.HISTORICAL);
        packageManager.addPackage(pkg019);

        TourPackage pkg020 = new TourPackage("PKG020", "Rural Village Experience Tour", "Paharpur", 8500.0, 1, "Village and cultural tour with rural life experience");
        pkg020.setCategory(PackageCategory.LOCAL);
        pkg020.setTourType(TourType.FAMILY);
        packageManager.addPackage(pkg020);

        Logger.log("Sample packages created");
    }

    private static void seedHotels(HotelManager hotelManager) {
        // Cox's Bazar Hotel
        Hotel hotel1 = new Hotel("HTL001", "Sea Pearl Beach Resort", "Cox's Bazar", 4.5);
        hotelManager.addHotel(hotel1);

        // Dhaka Hotel
        Hotel hotel2 = new Hotel("HTL002", "Pan Pacific Sonargaon", "Dhaka", 5.0);
        hotelManager.addHotel(hotel2);

        Logger.log("Sample hotels created");
    }
}
