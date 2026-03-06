package com.bms.persistence;

/**
 * AuthContext - Singleton to manage the currently logged-in customer session
 * Provides session state throughout the application
 */
public class AuthContext {

    private static volatile AuthContext instance;

    private String currentUser;
    private boolean isAuthenticated;

    private AuthContext() {
        this.currentUser = null;
        this.isAuthenticated = false;
    }

    public static AuthContext getInstance() {
        if (instance == null) {
            synchronized (AuthContext.class) {
                if (instance == null) {
                    instance = new AuthContext();
                }
            }
        }
        return instance;
    }

    public synchronized void login(String username) {
        if (!isAuthenticated) {
            this.currentUser = username;
            this.isAuthenticated = true;
            System.out.println("User '" + username + "' logged in successfully.");
        } else {
            System.out.println("Login failed: An active session already exists for '" + this.currentUser + "'.");
        }
    }

    public synchronized void logout() {
        if (isAuthenticated) {
            System.out.println("User '" + this.currentUser + "' logged out.");
            this.currentUser = null;
            this.isAuthenticated = false;
        } else {
            System.out.println("Logout failed: No active session found.");
        }
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}