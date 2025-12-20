/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author user
 */

import dao.RoomItemDAO;
import dao.RoomDAO;
import models.RoomItem;
import models.Room;
import exceptions.InsufficientStockException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoomItemService {
    private final Connection connection;
    private final RoomItemDAO itemDAO;
    private final RoomDAO roomDAO;
    
    public RoomItemService(Connection connection) {
        this.connection = connection;
        this.itemDAO = new RoomItemDAO(connection);
        this.roomDAO = new RoomDAO(connection);
    }
    
    /**
     * Create new room item
     */
    public RoomItem createRoomItem(RoomItem item) throws Exception {
        if (!item.isValid()) {
            throw new IllegalArgumentException("All item fields are required");
        }
        
        // Check if item with same name already exists
        RoomItem existing = itemDAO.findByName(item.getName());
        if (existing != null) {
            throw new IllegalArgumentException(
                "Item with name '" + item.getName() + "' already exists");
        }
        
        connection.setAutoCommit(false);
        try {
            RoomItem saved = itemDAO.insert(item);
            connection.commit();
            return saved;
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to create item: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Update existing room item
     */
    public void updateRoomItem(RoomItem item) throws Exception {
        if (item.getId() == null) {
            throw new IllegalArgumentException("Item ID is required");
        }
        
        if (!item.isValid()) {
            throw new IllegalArgumentException("All item fields are required");
        }
        
        connection.setAutoCommit(false);
        try {
            itemDAO.update(item);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to update item: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Delete room item
     */
    public void deleteRoomItem(Integer itemId) throws Exception {
        connection.setAutoCommit(false);
        try {
            itemDAO.delete(itemId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to delete item: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Get all room items
     */
    public List<RoomItem> getAllItems() throws SQLException {
        return itemDAO.findAll();
    }
    
    /**
     * Get item by ID
     */
    public RoomItem getItemById(Integer id) throws SQLException {
        return itemDAO.findById(id);
    }
    
    /**
     * Get item by name
     */
    public RoomItem getItemByName(String name) throws SQLException {
        return itemDAO.findByName(name);
    }
    
    /**
     * Get all rooms for combo box
     */
    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.findAll();
    }
    
    /**
     * Get room by name
     */
    public Room getRoomByName(String name) throws SQLException {
        return roomDAO.findByName(name);
    }
    
    /**
     * Add item to booking with stock validation
     */
    public void addItemToBooking(Integer bookingId, String itemName, Integer quantity) 
            throws Exception {
        
        if (bookingId == null || bookingId <= 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }
        
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        connection.setAutoCommit(false);
        try {
            // Get item details
            RoomItem item = itemDAO.findByName(itemName);
            if (item == null) {
                throw new IllegalArgumentException("Item not found: " + itemName);
            }
            
            // Check stock availability
            if (!item.hasStock(quantity)) {
                throw new InsufficientStockException(
                    itemName, 
                    item.getQuantity(), 
                    quantity
                );
            }
            
            // Add to booking
            itemDAO.addItemToBooking(
                bookingId, 
                item.getId(), 
                quantity, 
                item.getQuantity()
            );
            
            // Decrease stock
            itemDAO.decreaseStock(item.getId(), quantity);
            
            connection.commit();
            
        } catch (SQLException | InsufficientStockException | IllegalArgumentException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Get all items for a specific booking
     */
    public List<RoomItemDAO.BookingItemDetail> getBookingItems(Integer bookingId) 
            throws SQLException {
        return itemDAO.getBookingItems(bookingId);
    }
    
    /**
     * Delete booking item and restore stock
     */
    public void deleteBookingItem(Integer bookingItemId) throws Exception {
        connection.setAutoCommit(false);
        try {
            itemDAO.deleteBookingItem(bookingItemId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to delete booking item: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
}