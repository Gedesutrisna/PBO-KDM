/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author user
 */

public class Room {
    private Integer id;
    private String name;
    private Integer capacity;
    private String location;
    
    public Room() {}
    
    public Room(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // constructor
    public Room(Integer id, String name, Integer capacity, String location) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
    }
    
    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    // Validation
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               capacity != null && capacity > 0 &&
               location != null && !location.trim().isEmpty();
    }
    
    @Override
    public String toString() { return name; }
}