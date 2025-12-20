/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author user
 */


import models.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final Connection connection;
    
    public RoomDAO(Connection connection) {
        this.connection = connection;
    }
    
    public Room insert(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (name, capacity, location) VALUES (?,?,?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setId(rs.getInt(1));
                }
            }
            return room;
        }
    }
    
    public void update(Room room) throws SQLException {
        String sql = "UPDATE rooms SET name=?, capacity=?, location=? WHERE id=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, room.getName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setInt(4, room.getId());
            
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Room not found with id: " + room.getId());
            }
        }
    }
    
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Room not found with id: " + id);
            }
        }
    }
    
    public Room findById(Integer id) throws SQLException {
        String sql = "SELECT id, name, capacity, location FROM rooms WHERE id=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        }
        return null;
    }
    
    public List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, name, capacity, location FROM rooms ORDER BY name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }
    
    public Room findByName(String name) throws SQLException {
        String sql = "SELECT id, name, capacity, location FROM rooms WHERE name=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        }
        return null;
    }
    
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("capacity"),
            rs.getString("location")
        );
    }
}