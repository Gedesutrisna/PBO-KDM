/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author user
 */

import models.Booking;
import models.Room;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private final Connection connection;
    
    public BookingDAO(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Insert new booking
     */
    public Booking insert(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (borrower_name, borrower_telephone, " +
                     "admin_id, institution, event, start_date, end_date, " +
                     "start_time, end_time) VALUES (?,?,?,?,?,?,?,?,?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            setInsertParameters(ps, booking);
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    booking.setId(rs.getInt(1));
                }
            }
            
            // Insert room booking
            if (booking.getRoom() != null) {
                insertRoomBooking(booking.getId(), booking.getRoom().getId());
            }
            
            return booking;
        }
    }
    
    /**
     * Update existing booking
     */
    public void update(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET borrower_name=?, borrower_telephone=?, " +
                     "institution=?, event=?, start_date=?, end_date=?, " +
                     "start_time=?, end_time=? WHERE id=?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setUpdateParameters(ps, booking);
            ps.setInt(9, booking.getId());
            ps.executeUpdate();
            
            // Update room booking
            if (booking.getRoom() != null) {
                updateRoomBooking(booking.getId(), booking.getRoom().getId());
            }
        }
    }
    
    /**
     * Delete booking
     */
    public void delete(Integer bookingId) throws SQLException {
        // Delete items first
        String deleteItems = "DELETE FROM room_items_booking WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteItems)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
        
        // Delete room booking
        String deleteRoomBooking = "DELETE FROM room_bookings WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteRoomBooking)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
        
        // Delete booking
        String deleteBooking = "DELETE FROM bookings WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteBooking)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Find booking by ID
     */
    public Booking findById(Integer id) throws SQLException {
        String sql = "SELECT b.*, r.id as room_id, r.name as room_name " +
                     "FROM bookings b " +
                     "LEFT JOIN room_bookings rb ON b.id = rb.booking_id " +
                     "LEFT JOIN rooms r ON rb.room_id = r.id " +
                     "WHERE b.id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find all bookings
     */
    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, r.id as room_id, r.name as room_name " +
                     "FROM bookings b " +
                     "LEFT JOIN room_bookings rb ON b.id = rb.booking_id " +
                     "LEFT JOIN rooms r ON rb.room_id = r.id " +
                     "ORDER BY b.start_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Set parameters for INSERT (includes admin_id)
     */
    private void setInsertParameters(PreparedStatement ps, Booking booking) 
            throws SQLException {
        LocalDateTime start = booking.getStartDateTime();
        LocalDateTime end = booking.getEndDateTime();
        
        ps.setString(1, booking.getBorrowerName());
        ps.setString(2, booking.getBorrowerTelephone());
        ps.setInt(3, booking.getAdminId());
        ps.setString(4, booking.getInstitution());
        ps.setString(5, booking.getEvent());
        ps.setDate(6, Date.valueOf(start.toLocalDate()));
        ps.setDate(7, Date.valueOf(end.toLocalDate()));
        ps.setTime(8, Time.valueOf(start.toLocalTime()));
        ps.setTime(9, Time.valueOf(end.toLocalTime()));
    }
    
    /**
     * Set parameters for UPDATE (excludes admin_id)
     */
    private void setUpdateParameters(PreparedStatement ps, Booking booking) 
            throws SQLException {
        LocalDateTime start = booking.getStartDateTime();
        LocalDateTime end = booking.getEndDateTime();
        
        ps.setString(1, booking.getBorrowerName());
        ps.setString(2, booking.getBorrowerTelephone());
        // No admin_id for UPDATE
        ps.setString(3, booking.getInstitution());
        ps.setString(4, booking.getEvent());
        ps.setDate(5, Date.valueOf(start.toLocalDate()));
        ps.setDate(6, Date.valueOf(end.toLocalDate()));
        ps.setTime(7, Time.valueOf(start.toLocalTime()));
        ps.setTime(8, Time.valueOf(end.toLocalTime()));
        // Parameter 9 will be booking.getId() set in update() method
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setBorrowerName(rs.getString("borrower_name"));
        booking.setBorrowerTelephone(rs.getString("borrower_telephone"));
        booking.setAdminId(rs.getInt("admin_id"));
        booking.setInstitution(rs.getString("institution"));
        booking.setEvent(rs.getString("event"));
        
        // Combine date and time
        Date startDate = rs.getDate("start_date");
        Time startTime = rs.getTime("start_time");
        if (startDate != null && startTime != null) {
            booking.setStartDateTime(LocalDateTime.of(
                startDate.toLocalDate(), startTime.toLocalTime()));
        }
        
        Date endDate = rs.getDate("end_date");
        Time endTime = rs.getTime("end_time");
        if (endDate != null && endTime != null) {
            booking.setEndDateTime(LocalDateTime.of(
                endDate.toLocalDate(), endTime.toLocalTime()));
        }
        
        // Set room
        Integer roomId = rs.getInt("room_id");
        if (!rs.wasNull()) {
            booking.setRoom(new Room(roomId, rs.getString("room_name")));
        }
        
        return booking;
    }
    
    private void insertRoomBooking(Integer bookingId, Integer roomId) 
            throws SQLException {
        String sql = "INSERT INTO room_bookings (booking_id, room_id) VALUES (?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }
    
    private void updateRoomBooking(Integer bookingId, Integer roomId) 
            throws SQLException {
        String check = "SELECT id FROM room_bookings WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(check)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Update
                String update = "UPDATE room_bookings SET room_id=? WHERE booking_id=?";
                try (PreparedStatement upPs = connection.prepareStatement(update)) {
                    upPs.setInt(1, roomId);
                    upPs.setInt(2, bookingId);
                    upPs.executeUpdate();
                }
            } else {
                // Insert
                insertRoomBooking(bookingId, roomId);
            }
        }
    }
}