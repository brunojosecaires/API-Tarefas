package cncs.academy.ess.repository.sql;

import cncs.academy.ess.cryptography.PBKDF2;
import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;

import java.sql.*;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import java.util.ArrayList;

import static cncs.academy.ess.cryptography.PBKDF2.bytesToHex;
import static cncs.academy.ess.cryptography.PBKDF2.generateSalt;

public class SQLUserRepository implements UserRepository {
    private final BasicDataSource dataSource;

    public SQLUserRepository(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User findById(int userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by ID", e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users");
             ResultSet rs = stmt.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public int save(User user) {
        /*calcular o HASH da PWD*/
        String password = user.getPassword();
        String hashedPasswordString;
        byte[] salt = generateSalt(); // Generate a random salt
        int iterations = 10000;
        int keyLength = 256;
        // Hash the password using PBKDF2
        byte[] hashedPassword = null;
        try {
            hashedPassword = PBKDF2.hashPassword(password, salt, iterations, keyLength);
            // Convert the hashed password to a string for storage
            hashedPasswordString = bytesToHex(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
            /*TODO:ctx.status(201).json(response);*/
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPasswordString);
            stmt.setBytes(3, salt);
            stmt.executeUpdate();
            int generatedId = 0;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public void deleteById(int userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by username", e);
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String pwd = rs.getString("password");
        byte[] salt = rs.getBytes("salt");
        return new User(id, username, pwd, salt);
    }
}