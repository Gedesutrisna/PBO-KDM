/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author user
 */

public class BookingItem {
    private Integer id;
    private Integer bookingId;
    private Integer roomItemId;
    private String itemName;
    private Integer quantity;
    private Integer currentQuantity;
    
    public BookingItem() {}
    
    public BookingItem(Integer id, Integer bookingId, Integer roomItemId, 
                      String itemName, Integer quantity, Integer currentQuantity) {
        this.id = id;
        this.bookingId = bookingId;
        this.roomItemId = roomItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.currentQuantity = currentQuantity;
    }
    
    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    
    public Integer getRoomItemId() { return roomItemId; }
    public void setRoomItemId(Integer roomItemId) { this.roomItemId = roomItemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Integer getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(Integer currentQuantity) { 
        this.currentQuantity = currentQuantity; 
    }
}