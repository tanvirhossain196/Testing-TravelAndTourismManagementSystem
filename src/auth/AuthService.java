package auth;

import manager.UserManager;
import model.*;
import enumtype.RoleType;
import util.Logger;
import util.Validator;
import util.NotificationSender;
import util.DateUtil;
import java.util.List;
import java.util.ArrayList;

public class AuthService {
    private LoginManager loginManager;
    private UserManager userManager;
    private List<String> authorizedAdminEmails;
    private boolean enableEmailVerification;
    private boolean enableTwoFactorAuth;

    public AuthService(UserManager userManager) {
        this.loginManager = new LoginManager();
        this.userManager = userManager;
        this.authorizedAdminEmails = new ArrayList<>();
        this.enableEmailVerification = false;
        this.enableTwoFactorAuth = false;
        
        // Initialize default admin emails
        initializeDefaultAdmins();
    }

    public user authenticate(String email, String password) {
        // Validate input
        if (!Validator.validateEmail(email) || !Validator.isNotEmpty(password)) {
            Logger.log("Authentication failed - invalid input format: " + email);
            return null;
        }

        // Attempt login
        if (loginManager.login(email, password, userManager)) {
            user user = userManager.getUserByEmail(email);
            
            if (user != null) {
                // Log successful authentication
                Logger.log("Authentication successful for: " + email + " (Role: " + user.getRole() + ")");
                
                // Redirect based on role
                redirectBasedOnRole(user);
                
                // Update last login time (if User class had this field)
                logUserActivity(user, "LOGIN");
                
                return user;
            }
        }

        Logger.log("Authentication failed for: " + email);
        return null;
    }

    public user authenticateWithToken(String email, String sessionToken) {
        if (loginManager.isSessionValid(email, sessionToken)) {
            user user = userManager.getUserByEmail(email);
            if (user != null && user.isActive()) {
                loginManager.refreshSession(email);
                return user;
            }
        }
        return null;
    }

    private void redirectBasedOnRole(user user) {
        switch (user.getRole()) {
            case "ADMIN":
                System.out.println("🔐 Redirecting to Admin Dashboard");
                logUserActivity(user, "ADMIN_ACCESS");
                break;
            case "TOURIST":
                System.out.println("🌍 Redirecting to Tourist Portal");
                logUserActivity(user, "TOURIST_ACCESS");
                break;
            case "AGENT":
                System.out.println("💼 Redirecting to Agent Panel");
                logUserActivity(user, "AGENT_ACCESS");
                break;
            default:
                System.out.println("❓ Unknown role, redirecting to default page");
                Logger.warning("Unknown user role: " + user.getRole() + " for user: " + user.getEmail());
        }
    }

    public void logout(String email) {
        user user = userManager.getUserByEmail(email);
        if (user != null) {
            logUserActivity(user, "LOGOUT");
        }
        loginManager.logout(email);
    }

    public boolean isUserAuthenticated(String email) {
        return loginManager.isUserLoggedIn(email);
    }

    public boolean registerUser(user user) {
        // Validate user data
        if (!validateUserForRegistration(user)) {
            return false;
        }

        // Check if email already exists
        if (userManager.emailExists(user.getEmail())) {
            Logger.log("Registration failed - email already exists: " + user.getEmail());
            return false;
        }

        // Add user to system
        userManager.addUser(user);
        
        // Send welcome notification
        NotificationSender.sendWelcomeEmail(user.getEmail(), user.getName());
        
        // Log registration
        Logger.log("New user registered: " + user.getEmail() + " (Role: " + user.getRole() + ")");
        logUserActivity(user, "REGISTRATION");
        
        return true;
    }

    public boolean hasRole(String email, String requiredRole) {
        user user = userManager.getUserByEmail(email);
        return user != null && user.getRole().equals(requiredRole);
    }

    public boolean hasAnyRole(String email, String... roles) {
        user user = userManager.getUserByEmail(email);
        if (user == null) {
            return false;
        }
        
        String userRole = user.getRole();
        for (String role : roles) {
            if (userRole.equals(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin(String email) {
        return hasRole(email, "ADMIN");
    }

    public boolean isTourist(String email) {
        return hasRole(email, "TOURIST");
    }

    public boolean isAgent(String email) {
        return hasRole(email, "AGENT");
    }

    public boolean authorizeAccess(String email, String requiredRole) {
        if (!isUserAuthenticated(email)) {
            Logger.log("Access denied - user not authenticated: " + email);
            return false;
        }

        if (!hasRole(email, requiredRole)) {
            Logger.log("Access denied - insufficient privileges: " + email + " (Required: " + requiredRole + ")");
            logSecurityEvent("ACCESS_DENIED", email, "Required role: " + requiredRole);
            return false;
        }

        return true;
    }

    public boolean authorizeAdminAccess(String email) {
        if (!authorizeAccess(email, "ADMIN")) {
            return false;
        }

        // Additional check for super admin operations
        if (authorizedAdminEmails.contains(email)) {
            return true;
        }

        user user = userManager.getUserByEmail(email);
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            return "SUPER_ADMIN".equals(admin.getAdminLevel());
        }

        return false;
    }

    public String changePassword(String email, String oldPassword, String newPassword) {
        // Validate inputs
        if (!Validator.validatePassword(newPassword)) {
            return "New password must be at least 6 characters long";
        }

        if (oldPassword.equals(newPassword)) {
            return "New password must be different from current password";
        }

        // Change password
        if (loginManager.changePassword(email, oldPassword, newPassword, userManager)) {
            logSecurityEvent("PASSWORD_CHANGED", email, "Password changed successfully");
            return "Password changed successfully";
        } else {
            logSecurityEvent("PASSWORD_CHANGE_FAILED", email, "Incorrect old password");
            return "Current password is incorrect";
        }
    }

    public String requestPasswordReset(String email) {
        user user = userManager.getUserByEmail(email);
        if (user == null) {
            Logger.log("Password reset request for non-existent user: " + email);
            return "If this email exists in our system, you will receive a reset link.";
        }

        String resetToken = loginManager.generatePasswordResetToken(email);
        
        // In a real system, this would send an email with the reset link
        System.out.println("Password Reset Token for " + email + ": " + resetToken);
        NotificationSender.sendSMS(user.getPhone(), "Your password reset token: " + resetToken);
        
        logSecurityEvent("PASSWORD_RESET_REQUESTED", email, "Reset token generated");
        return "Password reset instructions have been sent to your email and phone.";
    }

    public String resetPassword(String email, String resetToken, String newPassword) {
        if (!Validator.validatePassword(newPassword)) {
            return "New password must be at least 6 characters long";
        }

        if (loginManager.resetPassword(email, resetToken, newPassword, userManager)) {
            logSecurityEvent("PASSWORD_RESET_COMPLETED", email, "Password reset using token");
            return "Password has been reset successfully. Please login with your new password.";
        } else {
            logSecurityEvent("PASSWORD_RESET_FAILED", email, "Invalid or expired reset token");
            return "Invalid or expired reset token. Please request a new password reset.";
        }
    }

    public void blockUser(String email, String reason) {
        user user = userManager.getUserByEmail(email);
        if (user != null) {
            user.setActive(false);
            userManager.updateUser(user);
            loginManager.logoutAllSessions(email);
            
            Logger.log("User blocked: " + email + " - Reason: " + reason);
            logSecurityEvent("USER_BLOCKED", email, reason);
        }
    }

    public void unblockUser(String email) {
        user user = userManager.getUserByEmail(email);
        if (user != null) {
            user.setActive(true);
            userManager.updateUser(user);
            loginManager.unlockAccount(email);
            
            Logger.log("User unblocked: " + email);
            logSecurityEvent("USER_UNBLOCKED", email, "Account reactivated");
        }
    }

    public boolean isAccountLocked(String email) {
        return loginManager.isAccountLocked(email);
    }

    public long getRemainingLockoutTime(String email) {
        return loginManager.getRemainingLockoutTime(email);
    }

    public void unlockAccount(String email) {
        loginManager.unlockAccount(email);
        logSecurityEvent("ACCOUNT_UNLOCKED", email, "Manual unlock by admin");
    }

    public String getSessionToken(String email) {
        return loginManager.getSessionToken(email);
    }

    public void refreshUserSession(String email) {
        loginManager.refreshSession(email);
    }

    public AuthenticationResult validateSession(String email, String sessionToken) {
        if (loginManager.isSessionValid(email, sessionToken)) {
            user user = userManager.getUserByEmail(email);
            if (user != null && user.isActive()) {
                return new AuthenticationResult(true, user, "Session valid");
            } else {
                return new AuthenticationResult(false, null, "User account not active");
            }
        } else {
            return new AuthenticationResult(false, null, "Invalid or expired session");
        }
    }

    // Security monitoring and logging
    private void logUserActivity(user user, String activity) {
        String logMessage = String.format("USER_ACTIVITY: %s | User: %s (%s) | Role: %s | Time: %s",
            activity, user.getName(), user.getEmail(), user.getRole(), DateUtil.getCurrentDateTime());
        Logger.log(logMessage);
    }

    private void logSecurityEvent(String event, String email, String details) {
        loginManager.logSecurityEvent(event, email, details);
    }

    public java.util.Map<String, Object> getSecurityStatistics() {
        java.util.Map<String, Object> stats = loginManager.getSecurityStats();
        stats.put("totalUsers", userManager.getTotalUsers());
        stats.put("activeUsers", userManager.getActiveUsersCount());
        return stats;
    }

    private boolean validateUserForRegistration(user user) {
        if (user == null) {
            return false;
        }

        if (!Validator.isValidName(user.getName())) {
            Logger.log("Registration validation failed - invalid name: " + user.getName());
            return false;
        }

        if (!Validator.validateEmail(user.getEmail())) {
            Logger.log("Registration validation failed - invalid email: " + user.getEmail());
            return false;
        }

        if (!Validator.validatePhone(user.getPhone())) {
            Logger.log("Registration validation failed - invalid phone: " + user.getPhone());
            return false;
        }

        if (!Validator.validatePassword(user.getPassword())) {
            Logger.log("Registration validation failed - invalid password for: " + user.getEmail());
            return false;
        }

        return true;
    }

    private void initializeDefaultAdmins() {
        authorizedAdminEmails.add("admin@tourbd.com");
        authorizedAdminEmails.add("superadmin@tourbd.com");
    }

    public void addAuthorizedAdmin(String email) {
        if (Validator.validateEmail(email) && !authorizedAdminEmails.contains(email)) {
            authorizedAdminEmails.add(email);
            Logger.log("Added authorized admin: " + email);
        }
    }

    public void removeAuthorizedAdmin(String email) {
        if (authorizedAdminEmails.remove(email)) {
            Logger.log("Removed authorized admin: " + email);
        }
    }

    // Configuration methods
    public void setEnableEmailVerification(boolean enable) {
        this.enableEmailVerification = enable;
    }

    public void setEnableTwoFactorAuth(boolean enable) {
        this.enableTwoFactorAuth = enable;
    }

    // Getters
    public LoginManager getLoginManager() { return loginManager; }
    public boolean isEmailVerificationEnabled() { return enableEmailVerification; }
    public boolean isTwoFactorAuthEnabled() { return enableTwoFactorAuth; }

    // Inner class for authentication results
    public static class AuthenticationResult {
        private boolean success;
        private user user;
        private String message;

        public AuthenticationResult(boolean success, user user, String message) {
            this.success = success;
            this.user = user;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public user getUser() { return user; }
        public String getMessage() { return message; }
    }
}
