package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }

    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMAT);
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DATETIME_FORMAT);
    }

    public static long daysBetween(String startDate, String endDate) {
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);
        return ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isValidDate(String dateString) {
        try {
            parseDate(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidDateTime(String dateTimeString) {
        try {
            parseDateTime(dateTimeString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMAT);
    }

    public static String addDays(String date, int days) {
        LocalDate localDate = parseDate(date);
        return localDate.plusDays(days).format(DATE_FORMAT);
    }

    public static String subtractDays(String date, int days) {
        LocalDate localDate = parseDate(date);
        return localDate.minusDays(days).format(DATE_FORMAT);
    }

    public static boolean isDateBefore(String date1, String date2) {
        LocalDate d1 = parseDate(date1);
        LocalDate d2 = parseDate(date2);
        return d1.isBefore(d2);
    }

    public static boolean isDateAfter(String date1, String date2) {
        LocalDate d1 = parseDate(date1);
        LocalDate d2 = parseDate(date2);
        return d1.isAfter(d2);
    }

    public static boolean isDateEqual(String date1, String date2) {
        LocalDate d1 = parseDate(date1);
        LocalDate d2 = parseDate(date2);
        return d1.isEqual(d2);
    }

    public static String getFirstDayOfMonth(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.withDayOfMonth(1).format(DATE_FORMAT);
    }

    public static String getLastDayOfMonth(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.withDayOfMonth(localDate.lengthOfMonth()).format(DATE_FORMAT);
    }

    public static int getDayOfWeek(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.getDayOfWeek().getValue();
    }

    public static String getDayName(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.getDayOfWeek().toString();
    }

    public static String getMonthName(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.getMonth().toString();
    }

    public static int getYear(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.getYear();
    }

    public static int getMonth(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.getMonthValue();
    }

    public static int getDay(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.getDayOfMonth();
    }

    public static boolean isWeekend(String date) {
        int dayOfWeek = getDayOfWeek(date);
        return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
    }

    public static boolean isLeapYear(String date) {
        LocalDate localDate = parseDate(date);
        return localDate.isLeapYear();
    }

    public static long hoursBetween(String startDateTime, String endDateTime) {
        LocalDateTime start = parseDateTime(startDateTime);
        LocalDateTime end = parseDateTime(endDateTime);
        return ChronoUnit.HOURS.between(start, end);
    }

    public static long minutesBetween(String startDateTime, String endDateTime) {
        LocalDateTime start = parseDateTime(startDateTime);
        LocalDateTime end = parseDateTime(endDateTime);
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static String addHours(String dateTime, int hours) {
        LocalDateTime localDateTime = parseDateTime(dateTime);
        return localDateTime.plusHours(hours).format(DATETIME_FORMAT);
    }

    public static String addMinutes(String dateTime, int minutes) {
        LocalDateTime localDateTime = parseDateTime(dateTime);
        return localDateTime.plusMinutes(minutes).format(DATETIME_FORMAT);
    }

    public static String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String formatTimestamp(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp), 
            java.time.ZoneId.systemDefault()
        );
        return dateTime.format(DATETIME_FORMAT);
    }

    public static long parseTimestamp(String dateTimeString) {
        LocalDateTime dateTime = parseDateTime(dateTimeString);
        return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
