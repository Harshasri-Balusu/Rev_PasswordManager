package org.example.dao;

import org.example.config.DBConnection;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private final Connection conn;
    public UserDao(){
        this.conn = DBConnection.getInstance();
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException{

        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setMasterPassword(rs.getString("master_password"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;

    }
    // Register User
    public boolean registerUser(User user){

        String sql = "INSERT INTO users(username, full_name, email, master_password) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)){

                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFullName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getMasterPassword());

                return ps.executeUpdate() > 0;
        } catch (SQLException e){
                logger.error("Error registering user", e);
        }
        return false;
    }

    // Check Username Exists
    public boolean isUsernameExists(String username){

        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            logger.error("Error checking username existence", e);
        }
        return false;
    }

    // Login
    public User getUserByUsername(String username){

        String sql = "SELECT * FROM users WHERE username = ?";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e){
            logger.error("Error fetching user by username", e);
        }
        return null;
    }

    public User getUserById(int userId) {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            logger.error("Error fetching user by id", e);
        }
        return null;
    }


    // Update Profile
    public boolean updateProfile(int userId, String fullName, String email) {

        String sql = "UPDATE users SET full_name = ?, email = ? WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating profile", e);
        }
        return false;
    }

    // Change Master Password
    public boolean updateMasterPassword(int userId, String newHashedPassword) {

        String sql = "UPDATE users SET master_password = ? WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating master password", e);
        }
        return false;
    }

}
