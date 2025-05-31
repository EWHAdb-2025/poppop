package db2025.DB2025Team05_poppop.DB2025Team05_common;

import db2025.DB2025Team05_poppop.DB2025Team05_domain.User;

// AppSession.java
public class AppSession {
    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
