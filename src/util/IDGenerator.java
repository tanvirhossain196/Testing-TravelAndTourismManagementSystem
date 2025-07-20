package util;

import java.util.Random;
import java.util.UUID;

public class IDGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new Random();

    public static String generateUserId() {
        return "USR" + generateRandomString(6);
    }

    public static String generatePackageId() {
        return "PKG" + generateRandomString(6);
    }

    public static String generateBookingId() {
        return "BKG" + generateRandomString(8);
    }

    public static String generateHotelId() {
        return "HTL" + generateRandomString(6);
    }

    public static String generateTransportId() {
        return "TRP" + generateRandomString(6);
    }

    public static String generatePaymentId() {
        return "PAY" + generateRandomString(8);
    }

    public static String generateTicketId() {
        return "TKT" + generateRandomString(10);
    }

    public static String generateInvoiceId() {
        return "INV" + generateRandomString(8);
    }

    public static String generateRoomId() {
        return "ROM" + generateRandomString(6);
    }

    public static String generateGuideId() {
        return "GID" + generateRandomString(6);
    }

    public static String generateReviewId() {
        return "REV" + generateRandomString(8);
    }

    public static String generateRatingId() {
        return "RAT" + generateRandomString(8);
    }

    public static String generateLocationId() {
        return "LOC" + generateRandomString(6);
    }

    public static String generateItineraryId() {
        return "ITN" + generateRandomString(8);
    }

    public static String generateVehicleId() {
        return "VEH" + generateRandomString(6);
    }

    public static String generateSeatId() {
        return "SEAT" + generateRandomString(6);
    }

    public static String generateScheduleId() {
        return "SCH" + generateRandomString(8);
    }

    public static String generateAssignmentId() {
        return "ASG" + generateRandomString(8);
    }

    public static String generateGroupTourId() {
        return "GRP" + generateRandomString(8);
    }

    public static String generateCancellationId() {
        return "CAN" + generateRandomString(8);
    }

    public static String generateRefundId() {
        return "REF" + generateRandomString(8);
    }

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public static String generateResetToken() {
        return "RST" + System.currentTimeMillis() + generateRandomString(12);
    }

    public static String generateVerificationCode() {
        return generateRandomString(6, "0123456789");
    }

    public static String generateQRCode() {
        return "QR" + generateRandomString(16);
    }

    public static String generateBarcodeId() {
        return "BC" + generateRandomString(12, "0123456789");
    }

    public static String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + generateRandomString(6);
    }

    public static String generateConfirmationCode() {
        return "CONF" + generateRandomString(8);
    }

    // Main method that was missing - now added
    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return result.toString();
    }

    // Overloaded method with custom character set
    public static String generateRandomString(int length, String customCharacters) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(customCharacters.charAt(random.nextInt(customCharacters.length())));
        }
        return result.toString();
    }

    // Generate alphanumeric string
    public static String generateAlphanumericString(int length) {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return generateRandomString(length, alphanumeric);
    }

    // Generate numeric string
    public static String generateNumericString(int length) {
        String numeric = "0123456789";
        return generateRandomString(length, numeric);
    }

    // Generate alphabetic string
    public static String generateAlphabeticString(int length) {
        String alphabetic = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        return generateRandomString(length, alphabetic);
    }

    // Generate unique ID with timestamp
    public static String generateUniqueId(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + generateRandomString(4);
    }

    // Generate ID with specific format
    public static String generateFormattedId(String prefix, int numericLength, int alphaLength) {
        return prefix + generateNumericString(numericLength) + generateRandomString(alphaLength);
    }

    // Generate short URL code
    public static String generateShortCode() {
        return generateRandomString(8, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
    }

    // Generate secure token
    public static String generateSecureToken(int length) {
        String secureChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        return generateRandomString(length, secureChars);
    }

    // Generate OTP (One Time Password)
    public static String generateOTP() {
        return generateNumericString(6);
    }

    // Generate PIN
    public static String generatePIN() {
        return generateNumericString(4);
    }

    // Generate reference number
    public static String generateReferenceNumber() {
        return "REF" + System.currentTimeMillis() + generateRandomString(4);
    }

    // Generate batch ID
    public static String generateBatchId() {
        return "BATCH" + System.currentTimeMillis();
    }

    // Check if ID is valid format
    public static boolean isValidIdFormat(String id, String expectedPrefix) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        return id.startsWith(expectedPrefix) && id.length() > expectedPrefix.length();
    }

    // Extract timestamp from time-based ID
    public static long extractTimestamp(String id) {
        try {
            String[] parts = id.split("_");
            if (parts.length >= 2) {
                return Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException e) {
            // Invalid format
        }
        return -1;
    }

    // Generate ID with checksum
    public static String generateIdWithChecksum(String prefix, int length) {
        String baseId = prefix + generateRandomString(length - 1);
        int checksum = calculateChecksum(baseId);
        return baseId + (checksum % 10);
    }

    // Calculate simple checksum
    private static int calculateChecksum(String input) {
        int sum = 0;
        for (char c : input.toCharArray()) {
            sum += (int) c;
        }
        return sum;
    }

    // Validate ID with checksum
    public static boolean validateIdWithChecksum(String id) {
        if (id == null || id.length() < 2) {
            return false;
        }
        
        String baseId = id.substring(0, id.length() - 1);
        int providedChecksum = Character.getNumericValue(id.charAt(id.length() - 1));
        int calculatedChecksum = calculateChecksum(baseId) % 10;
        
        return providedChecksum == calculatedChecksum;
    }
}
