/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

/**
 *
 * @author user
 */

import java.time.LocalDateTime;

public class ValidationUtil {
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isPositiveNumber(Integer number) {
        return number != null && number > 0;
    }
    
    public static boolean isValidDateRange(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null && end.isAfter(start);
    }
    
    public static boolean isValidPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) return false;
        // Basic phone validation (adjust regex as needed)
        return phone.matches("^[0-9+\\-\\s()]{8,20}$");
    }
}

