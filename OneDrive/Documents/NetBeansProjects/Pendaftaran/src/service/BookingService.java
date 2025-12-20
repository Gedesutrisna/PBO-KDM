/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author user
 */

import dao.BookingDAO;
import dao.RoomDAO;
import dao.RoomItemDAO;
import models.Booking;
import models.Room;
import models.RoomItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BookingService {
    private final Connection connection;
    private final BookingDAO bookingDAO;
    private final RoomDAO roomDAO;
    private final RoomItemDAO itemDAO;
    private final RoomItemService itemService;
    
    public BookingService(Connection connection) {
        this.connection = connection;
        this.bookingDAO = new BookingDAO(connection);
        this.roomDAO = new RoomDAO(connection);
        this.itemDAO = new RoomItemDAO(connection);
        this.itemService = new RoomItemService(connection);
    }
    
    /**
     * Create new booking with validation
     */
    public Booking createBooking(Booking booking) throws Exception {
        if (!booking.isValid()) {
            throw new IllegalArgumentException("All booking fields are required");
        }
        
        if (booking.getEndDateTime().isBefore(booking.getStartDateTime())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        if (booking.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        
        connection.setAutoCommit(false);
        try {
            Booking saved = bookingDAO.insert(booking);
            connection.commit();
            return saved;
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to create booking: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Update existing booking
     */
    public void updateBooking(Booking booking) throws Exception {
        if (booking.getId() == null) {
            throw new IllegalArgumentException("Booking ID is required");
        }
        
        if (!booking.isValid()) {
            throw new IllegalArgumentException("All booking fields are required");
        }
        
        connection.setAutoCommit(false);
        try {
            bookingDAO.update(booking);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to update booking: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Delete booking (cascades to items)
     */
    public void deleteBooking(Integer bookingId) throws Exception {
        connection.setAutoCommit(false);
        try {
            // This will also restore item stocks
            bookingDAO.delete(bookingId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new Exception("Failed to delete booking: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.findAll();
    }
    
    /**
     * Get booking by ID
     */
    public Booking getBookingById(Integer id) throws SQLException {
        return bookingDAO.findById(id);
    }
    
    /**
     * Get all rooms for combo box
     */
    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.findAll();
    }
    
    /**
     * Get all items for combo box
     */
    public List<RoomItem> getAllItems() throws SQLException {
        return itemDAO.findAll();
    }
    
    /**
     * Find room by name
     */
    public Room getRoomByName(String name) throws SQLException {
        return roomDAO.findByName(name);
    }
    
    /**
     * Add item to booking
     */
    public void addItemToBooking(Integer bookingId, String itemName, Integer quantity) 
            throws Exception {
        itemService.addItemToBooking(bookingId, itemName, quantity);
    }
    
    /**
     * Get booking items
     */
    public List<RoomItemDAO.BookingItemDetail> getBookingItems(Integer bookingId) 
            throws SQLException {
        return itemService.getBookingItems(bookingId);
    }
    
    /**
     * Delete booking item
     */
    public void deleteBookingItem(Integer bookingItemId) throws Exception {
        itemService.deleteBookingItem(bookingItemId);
    }
}