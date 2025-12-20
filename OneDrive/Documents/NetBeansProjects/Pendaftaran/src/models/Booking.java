/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author user
 */
import java.time.LocalDateTime;

public class Booking {
    private Integer id;
    private String borrowerName;
    private String borrowerTelephone;
    private Integer adminId;
    private String institution;
    private String event;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Room room;
    
    // Constructor
    public Booking() {}
    
    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { 
        this.borrowerName = borrowerName; 
    }
    
    public String getBorrowerTelephone() { return borrowerTelephone; }
    public void setBorrowerTelephone(String borrowerTelephone) { 
        this.borrowerTelephone = borrowerTelephone; 
    }
    
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }
    
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { 
        this.institution = institution; 
    }
    
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { 
        this.startDateTime = startDateTime; 
    }
    
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { 
        this.endDateTime = endDateTime; 
    }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
    // Validation
    public boolean isValid() {
        return borrowerName != null && !borrowerName.trim().isEmpty() &&
               borrowerTelephone != null && !borrowerTelephone.trim().isEmpty() &&
               institution != null && !institution.trim().isEmpty() &&
               event != null && !event.trim().isEmpty() &&
               startDateTime != null && endDateTime != null &&
               room != null;
    }
}