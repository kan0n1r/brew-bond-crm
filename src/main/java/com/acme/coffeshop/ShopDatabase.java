package com.acme.coffeshop;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class ShopDatabase {
    private static final String DB_PATH = System.getenv().getOrDefault("SHOP_DB_PATH", "data/brew-bond-crm.db");
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private ShopDatabase() {
    }

    static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("SQLite JDBC driver is unavailable.", exception);
        }
        return DriverManager.getConnection(URL);
    }

    static void initialize() throws Exception {
        Path path = Paths.get(DB_PATH).toAbsolutePath().getParent();
        if (path != null) {
            Files.createDirectories(path);
        }

        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT NOT NULL COLLATE NOCASE UNIQUE, "
                    + "password_hash BLOB NOT NULL, "
                    + "password_salt BLOB NOT NULL, "
                    + "role TEXT NOT NULL CHECK (role IN ('USER', 'ADMIN')))");
            statement.execute("CREATE TABLE IF NOT EXISTS products ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "price_cents INTEGER NOT NULL CHECK (price_cents > 0))");
        }

        seedProducts();
        seedConfiguredAdmin();
    }

    private static void seedProducts() throws SQLException {
        try (Connection connection = connect();
             PreparedStatement count = connection.prepareStatement("SELECT COUNT(*) FROM products");
             ResultSet result = count.executeQuery()) {
            if (result.next() && result.getInt(1) == 0) {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO products(name, description, price_cents) VALUES (?, ?, ?)")) {
                    addProduct(insert, "House Blend", "Smooth medium roast coffee.", 1299);
                    addProduct(insert, "Espresso", "Rich, dark-roasted whole beans.", 1499);
                    addProduct(insert, "Tea Sampler", "A small selection of herbal teas.", 899);
                }
            }
        }
    }

    private static void addProduct(PreparedStatement insert, String name, String description, int price)
            throws SQLException {
        insert.setString(1, name);
        insert.setString(2, description);
        insert.setInt(3, price);
        insert.executeUpdate();
    }

    private static void seedConfiguredAdmin() throws Exception {
        String username = System.getenv("SHOP_ADMIN_USERNAME");
        String password = System.getenv("SHOP_ADMIN_PASSWORD");
        if (username == null && password == null) {
            return;
        }
        if (username == null || password == null || !InputRules.validUsername(username)
                || !InputRules.validPassword(password)) {
            throw new IllegalStateException("Configure both valid SHOP_ADMIN_USERNAME and SHOP_ADMIN_PASSWORD values.");
        }

        try (Connection connection = connect();
             PreparedStatement query = connection.prepareStatement("SELECT 1 FROM users WHERE role = 'ADMIN'");
             ResultSet result = query.executeQuery()) {
            if (result.next()) {
                return;
            }
        }

        PasswordUtil.Credential credential = PasswordUtil.create(password);
        try (Connection connection = connect();
             PreparedStatement insert = connection.prepareStatement(
                     "INSERT INTO users(username, password_hash, password_salt, role) VALUES (?, ?, ?, 'ADMIN')")) {
            insert.setString(1, username.trim());
            insert.setBytes(2, credential.getHash());
            insert.setBytes(3, credential.getSalt());
            insert.executeUpdate();
        }
    }
}
