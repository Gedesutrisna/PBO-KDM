/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author user
 */

import models.RoomItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomItemDAO {
    private final Connection connection;
    
    public RoomItemDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Insert new room item
     */
    public RoomItem insert(RoomItem item) throws SQLException {
        String sql = "INSERT INTO room_items (name, quantity, room_id) VALUES (?,?,?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setInt(3, item.getRoomId());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
            return item;
        }
    }
    
    /**
     * Update existing room item
     */
    public void update(RoomItem item) throws SQLException {
        String sql = "UPDATE room_items SET name=?, quantity=?, room_id=? WHERE id=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setInt(3, item.getRoomId());
            ps.setInt(4, item.getId());
            
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Item not found with id: " + item.getId());
            }
        }
    }
    
    /**
     * Delete room item
     */
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM room_items WHERE id=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Item not found with id: " + id);
            }
        }
    }
    
    /**
     * Find item by ID
     */
    public RoomItem findById(Integer id) throws SQLException {
        String sql = "SELECT ri.id, ri.name, ri.quantity, ri.room_id, r.name as room_name " +
                     "FROM room_items ri " +
                     "JOIN rooms r ON ri.room_id = r.id " +
                     "WHERE ri.id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoomItem(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find all room items with room name
     */
    public List<RoomItem> findAll() throws SQLException {
        List<RoomItem> items = new ArrayList<>();
        String sql = "SELECT ri.id, ri.name, ri.quantity, ri.room_id, r.name as room_name " +
                     "FROM room_items ri " +
                     "JOIN rooms r ON ri.room_id = r.id " +
                     "ORDER BY r.name, ri.name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapResultSetToRoomItem(rs));
            }
        }
        return items;
    }
    
    /**
     * Find item by name
     */
    public RoomItem findByName(String name) throws SQLException {
        String sql = "SELECT ri.id, ri.name, ri.quantity, ri.room_id, r.name as room_name " +
                     "FROM room_items ri " +
                     "JOIN rooms r ON ri.room_id = r.id " +
                     "WHERE ri.name = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoomItem(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Decrease stock when item is booked
     */
    public void decreaseStock(Integer itemId, Integer quantity) throws SQLException {
        String sql = "UPDATE room_items SET quantity = quantity - ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Failed to decrease stock, item not found");
            }
        }
    }
    
    /**
     * Increase stock when booking is cancelled/deleted
     */
    public void increaseStock(Integer itemId, Integer quantity) throws SQLException {
        String sql = "UPDATE room_items SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, itemId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Failed to increase stock, item not found");
            }
        }
    }
    
    /**
     * Add item to booking
     */
    public void addItemToBooking(Integer bookingId, Integer itemId, 
                                 Integer quantity, Integer currentStock) 
            throws SQLException {
        String sql = "INSERT INTO room_items_booking " +
                     "(booking_id, room_item_id, quantity, current_quantity) " +
                     "VALUES (?,?,?,?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.setInt(4, currentStock);
            ps.executeUpdate();
        }
    }
    
    /**
     * Get booking items (items borrowed in a specific booking)
     */
    public List<BookingItemDetail> getBookingItems(Integer bookingId) 
            throws SQLException {
        List<BookingItemDetail> items = new ArrayList<>();
        String sql = "SELECT rib.id, ri.id as item_id, ri.name, " +
                     "rib.quantity, rib.current_quantity " +
                     "FROM room_items_booking rib " +
                     "JOIN room_items ri ON rib.room_item_id = ri.id " +
                     "WHERE rib.booking_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingItemDetail item = new BookingItemDetail();
                    item.id = rs.getInt("id");
                    item.itemId = rs.getInt("item_id");
                    item.itemName = rs.getString("name");
                    item.quantity = rs.getInt("quantity");
                    item.currentQuantity = rs.getInt("current_quantity");
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    /**
     * Delete booking item and restore stock
     */
    public void deleteBookingItem(Integer bookingItemId) throws SQLException {
        // First get the item details to restore stock
        String selectSql = "SELECT room_item_id, quantity FROM room_items_booking WHERE id = ?";
        Integer itemId = null;
        Integer quantity = null;
        
        try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
            ps.setInt(1, bookingItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    itemId = rs.getInt("room_item_id");
                    quantity = rs.getInt("quantity");
                } else {
                    throw new SQLException("Booking item not found");
                }
            }
        }
        
        // Delete the booking item
        String deleteSql = "DELETE FROM room_items_booking WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
            ps.setInt(1, bookingItemId);
            ps.executeUpdate();
        }
        
        // Restore stock
        increaseStock(itemId, quantity);
    }
    
    /**
     * Map ResultSet to RoomItem
     */
    private RoomItem mapResultSetToRoomItem(ResultSet rs) throws SQLException {
        RoomItem item = new RoomItem();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setRoomId(rs.getInt("room_id"));
        item.setRoomName(rs.getString("room_name"));
        return item;
    }
    
    /**
     * Helper class for booking item details
     */
    public static class BookingItemDetail {
        public Integer id;
        public Integer itemId;
        public String itemName;
        public Integer quantity;
        public Integer currentQuantity;
    }
}