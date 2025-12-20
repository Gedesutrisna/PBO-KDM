/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author user
 */

public class RoomItem {
    private Integer id;
    private String name;
    private Integer quantity;
    private Integer roomId;  
    private String roomName;
    
    public RoomItem() {}
    
    public RoomItem(Integer id, String name, Integer quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }
    
    // Full constructor with room info
    public RoomItem(Integer id, String name, Integer quantity, Integer roomId, String roomName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.roomId = roomId;
        this.roomName = roomName;
    }
    
    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    
    // Validation
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               quantity != null && quantity >= 0 &&
               roomId != null && roomId > 0;
    }
    
    public boolean hasStock(int requested) {
        return quantity != null && quantity >= requested;
    }
    
    @Override
    public String toString() { return name; }
}
