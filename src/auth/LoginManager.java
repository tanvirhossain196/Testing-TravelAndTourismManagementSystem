package auth;

import util.Logger;
import util.DateUtil;
import manager.UserManager;
import model.user;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginManager {
    private Map<String, String> activeSessions;
    private Map<String, Integer> loginAttempts;
    private Map<String, String> sessionTokens; // email -> sessionToken
    private Map<String, Long> sessionTimestamps; // sessionToken -> timestamp
    private Map<String, String> passwordResetTokens; // email -> resetToken
    private Map<String, Long> accountLockoutTime; // email -> lockout expiry time
    
    private int maxLoginAttempts;
    private long sessionTimeoutMinutes;
    private long accountLockoutMinutes;
    private boolean enableSessionTimeout;
    private boolean enableAccountLockout;

    public LoginManager() {
        this.activeSessions = new ConcurrentHashMap<>();
        this.loginAttempts = new ConcurrentHashMap<>();
        this.sessionTokens = new ConcurrentHashMap<>();
        this.sessionTimestamps = new ConcurrentHashMap<>();
        this.passwordResetTokens = new ConcurrentHashMap<>();
        this.accountLockoutTime = new ConcurrentHashMap<>();
        
        this.maxLoginAttempts = 3;
        this.sessionTimeoutMinutes = 120; // 2 hours
        this.accountLockoutMinutes = 30; // 30 minutes
        this.enableSessionTimeout = true;
        this.enableAccountLockout = true;
    }

    public boolean login(String email, String password, UserManager userManager) {
        // Check if account is locked
        if (isAccountLocked(email)) {
            Logger.log("Login attempt on locked account: " + email);
            return false;
        }

        // Validate credentials
        if (isValidCredentials(email, password, userManager)) {
            // Successful login
            String sessionId = generateSessionId();
            String sessionToken = generateSessionToken();
            
            activeSessions.put(email, sessionId);
            sessionTokens.put(email, sessionToken);
            sessionTimestamps.put(sessionToken, System.currentTimeMillis());
            
            // Reset login attempts on successful login
            loginAttempts.remove(email);
            accountLockoutTime.remove(email);
            
            Logger.log("User logged in successfully: " + email);
            return true;
        } else {
            // Failed login
            incrementLoginAttempts(email);
            Logger.log("Failed login attempt for: " + email + " (Attempt: " + getLoginAttempts(email) + ")");
            
            // Lock account if max attempts reached
            if (enableAccountLockout && getLoginAttempts(email) >= maxLoginAttempts) {
                lockAccount(email);
                Logger.log("Account locked due to multiple failed attempts: " + email);
            }
            
            return false;
        }
    }

    public void logout(String email) {
        String sessionToken = sessionTokens.get(email);
        
        activeSessions.remove(email);
        sessionTokens.remove(email);
        
        if (sessionToken != null) {
            sessionTimestamps.remove(sessionToken);
        }
        
        Logger.log("User logged out: " + email);
    }

    public boolean isValidCredentials(String email, String password, UserManager userManager) {
        user user = userManager.getUserByEmail(email);
        return user != null && user.getPassword().equals(password) && user.isActive();
    }

    public boolean isUserLoggedIn(String email) {
        if (!activeSessions.containsKey(email)) {
            return false;
        }

        // Check session timeout if enabled
        if (enableSessionTimeout) {
            String sessionToken = sessionTokens.get(email);
            if (sessionToken != null && isSessionExpired(sessionToken)) {
                // Session expired, logout user
                logout(email);
                Logger.log("Session expired for user: " + email);
                return false;
            }
        }

        return true;
    }

    public boolean isSessionValid(String email, String sessionToken) {
        String storedToken = sessionTokens.get(email);
        if (storedToken == null || !storedToken.equals(sessionToken)) {
            return false;
        }

        // Check if session is expired
        if (enableSessionTimeout && isSessionExpired(sessionToken)) {
            logout(email);
            return false;
        }

        // Update session timestamp
        sessionTimestamps.put(sessionToken, System.currentTimeMillis());
        return true;
    }

    public String getSessionToken(String email) {
        return sessionTokens.get(email);
    }

    public void refreshSession(String email) {
        String sessionToken = sessionTokens.get(email);
        if (sessionToken != null) {
            sessionTimestamps.put(sessionToken, System.currentTimeMillis());
        }
    }

    private boolean isSessionExpired(String sessionToken) {
        Long timestamp = sessionTimestamps.get(sessionToken);
        if (timestamp == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long sessionAge = currentTime - timestamp;
        long timeoutMillis = sessionTimeoutMinutes * 60 * 1000;

        return sessionAge > timeoutMillis;
    }

    private void incrementLoginAttempts(String email) {
        loginAttempts.put(email, loginAttempts.getOrDefault(email, 0) + 1);
    }

    private int getLoginAttempts(String email) {
        return loginAttempts.getOrDefault(email, 0);
    }

    public boolean isAccountLocked(String email) {
        if (!enableAccountLockout) {
            return false;
        }

        Long lockoutExpiry = accountLockoutTime.get(email);
        if (lockoutExpiry == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime >= lockoutExpiry) {
            // Lockout period expired, unlock account
            unlockAccount(email);
            return false;
        }

        return true;
    }

    private void lockAccount(String email) {
        long lockoutExpiry = System.currentTimeMillis() + (accountLockoutMinutes * 60 * 1000);
        accountLockoutTime.put(email, lockoutExpiry);
    }

    public void unlockAccount(String email) {
        loginAttempts.remove(email);
        accountLockoutTime.remove(email);
        Logger.log("Account unlocked: " + email);
    }

    public long getRemainingLockoutTime(String email) {
        Long lockoutExpiry = accountLockoutTime.get(email);
        if (lockoutExpiry == null) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long remainingTime = lockoutExpiry - currentTime;
        
        return Math.max(0, remainingTime / 1000); // Return seconds
    }

    public String generatePasswordResetToken(String email) {
        String resetToken = generateRandomToken();
        passwordResetTokens.put(email, resetToken);
        Logger.log("Password reset token generated for: " + email);
        return resetToken;
    }

    public boolean isValidPasswordResetToken(String email, String token) {
        String storedToken = passwordResetTokens.get(email);
        return storedToken != null && storedToken.equals(token);
    }

    public void invalidatePasswordResetToken(String email) {
        passwordResetTokens.remove(email);
    }

    public boolean changePassword(String email, String oldPassword, String newPassword, UserManager userManager) {
        user user = userManager.getUserByEmail(email);
        if (user == null) {
            return false;
        }

        if (!user.getPassword().equals(oldPassword)) {
            Logger.log("Password change failed - incorrect old password: " + email);
            return false;
        }

        user.setPassword(newPassword);
        userManager.updateUser(user);
        
        // Logout user to force re-login with new password
        logout(email);
        
        Logger.log("Password changed successfully: " + email);
        return true;
    }

    public boolean resetPassword(String email, String resetToken, String newPassword, UserManager userManager) {
        if (!isValidPasswordResetToken(email, resetToken)) {
            Logger.log("Invalid password reset token: " + email);
            return false;
        }

        user user = userManager.getUserByEmail(email);
        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);
        userManager.updateUser(user);
        
        // Invalidate reset token and logout user
        invalidatePasswordResetToken(email);
        logout(email);
        
        Logger.log("Password reset successfully: " + email);
        return true;
    }

    public void logoutAllSessions(String email) {
        // Remove all sessions for the user
        activeSessions.remove(email);
        String sessionToken = sessionTokens.remove(email);
        if (sessionToken != null) {
            sessionTimestamps.remove(sessionToken);
        }
        
        Logger.log("All sessions logged out for user: " + email);
    }

    public void cleanupExpiredSessions() {
        if (!enableSessionTimeout) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long timeoutMillis = sessionTimeoutMinutes * 60 * 1000;
        
        sessionTimestamps.entrySet().removeIf(entry -> {
            String sessionToken = entry.getKey();
            long timestamp = entry.getValue();
            
            if (currentTime - timestamp > timeoutMillis) {
                // Find and remove the associated session
                sessionTokens.entrySet().removeIf(tokenEntry -> 
                    tokenEntry.getValue().equals(sessionToken));
                
                // Find and remove from active sessions
                activeSessions.entrySet().removeIf(sessionEntry -> {
                    String email = sessionEntry.getKey();
                    String userToken = sessionTokens.get(email);
                    return sessionToken.equals(userToken);
                });
                
                Logger.log("Expired session cleaned up: " + sessionToken);
                return true;
            }
            return false;
        });
    }

    // Security monitoring methods
    public Map<String, Object> getSecurityStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSessions", activeSessions.size());
        stats.put("lockedAccounts", accountLockoutTime.size());
        stats.put("totalLoginAttempts", loginAttempts.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("pendingPasswordResets", passwordResetTokens.size());
        return stats;
    }

    public void logSecurityEvent(String event, String email, String details) {
        String logMessage = String.format("SECURITY_EVENT: %s | User: %s | Details: %s | Time: %s", 
            event, email, details, DateUtil.getCurrentDateTime());
        Logger.log(logMessage);
    }

    private String generateSessionId() {
        return "SID_" + System.currentTimeMillis() + "_" + Math.random();
    }

    private String generateSessionToken() {
        return "TOKEN_" + System.currentTimeMillis() + "_" + generateRandomString(16);
    }

    private String generateRandomToken() {
        return "RST_" + System.currentTimeMillis() + "_" + generateRandomString(20);
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return result.toString();
    }

    // Configuration methods
    public void setMaxLoginAttempts(int maxAttempts) {
        this.maxLoginAttempts = maxAttempts;
    }

    public void setSessionTimeout(long timeoutMinutes) {
        this.sessionTimeoutMinutes = timeoutMinutes;
    }

    public void setAccountLockoutDuration(long lockoutMinutes) {
        this.accountLockoutMinutes = lockoutMinutes;
    }

    public void setEnableSessionTimeout(boolean enable) {
        this.enableSessionTimeout = enable;
    }

    public void setEnableAccountLockout(boolean enable) {
        this.enableAccountLockout = enable;
    }

    // Getters
    public int getMaxLoginAttempts() { return maxLoginAttempts; }
    public long getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
    public long getAccountLockoutMinutes() { return accountLockoutMinutes; }
    public boolean isSessionTimeoutEnabled() { return enableSessionTimeout; }
    public boolean isAccountLockoutEnabled() { return enableAccountLockout; }
    public int getActiveSessionCount() { return activeSessions.size(); }
    public int getLockedAccountCount() { return accountLockoutTime.size(); }
}
