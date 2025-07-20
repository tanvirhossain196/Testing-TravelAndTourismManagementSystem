package util;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

public class SearchHelper {
    
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && 
               text.toLowerCase().contains(keyword.toLowerCase());
    }

    public static List<String> searchInList(List<String> list, String keyword) {
        List<String> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        
        for (String item : list) {
            if (containsIgnoreCase(item, keyword)) {
                results.add(item);
            }
        }
        return results;
    }

    public static boolean matchesAny(String text, String... keywords) {
        if (text == null) return false;
        for (String keyword : keywords) {
            if (containsIgnoreCase(text, keyword)) {
                return true;
            }
        }
        return false;
    }
}
