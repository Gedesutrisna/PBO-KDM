/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author user
 */

import dao.RoomDAO;
import models.Room;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoomService {
    private final Connection connection;
    private final RoomDAO roomDAO;
    
    public RoomService(Connection connection) {
        this.connection = connection;
        this.roomDAO = new RoomDAO(connection);
    }
    
    public Room createRoom(Room room) throws Exception {
        if (!room.isValid()) {
            throw new IllegalArgumentException("All room fields are required");
        }
        
        // Check duplicate name
        Room existing = roomDAO.findByName(room.getName());
        if (existing != null) {
            throw new IllegalArgumentException("Room with name '" + room.getName() + "' already exists");
        }
        
        connection.setAutoCommit(false);
        try {
            Room saved = roomDAO.insert(room);
            connection.commit();
            return saved;
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to create room: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void updateRoom(Room room) throws Exception {
        if (room.getId() == null) {
            throw new IllegalArgumentException("Room ID is required");
        }
        
        if (!room.isValid()) {
            throw new IllegalArgumentException("All room fields are required");
        }
        
        connection.setAutoCommit(false);
        try {
            roomDAO.update(room);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to update room: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void deleteRoom(Integer id) throws Exception {
        connection.setAutoCommit(false);
        try {
            roomDAO.delete(id);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to delete room: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.findAll();
    }
    
    public Room getRoomById(Integer id) throws SQLException {
        return roomDAO.findById(id);
    }
    
    public Room getRoomByName(String name) throws SQLException {
        return roomDAO.findByName(name);
    }
}