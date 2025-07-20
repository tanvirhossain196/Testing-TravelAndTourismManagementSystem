package util;

import java.text.DecimalFormat;

public class CurrencyFormatter {
    private static final DecimalFormat BDT_FORMAT = new DecimalFormat("৳#,##0.00");
    private static final DecimalFormat USD_FORMAT = new DecimalFormat("$#,##0.00");

    public static String formatBDT(double amount) {
        return BDT_FORMAT.format(amount);
    }

    public static String formatUSD(double amount) {
        return USD_FORMAT.format(amount);
    }

    public static String formatCurrency(double amount, String currencyCode) {
        switch (currencyCode.toUpperCase()) {
            case "BDT":
                return formatBDT(amount);
            case "USD":
                return formatUSD(amount);
            default:
                return String.format("%.2f %s", amount, currencyCode);
        }
    }

    public static double convertUSDToBDT(double usdAmount) {
        return usdAmount * 110.0; // Approximate rate
    }

    public static double convertBDTToUSD(double bdtAmount) {
        return bdtAmount / 110.0; // Approximate rate
    }
}
