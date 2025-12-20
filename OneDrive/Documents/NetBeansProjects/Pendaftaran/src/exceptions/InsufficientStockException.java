/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package exceptions;

/**
 *
 * @author user
 */

public class InsufficientStockException extends Exception {
    private final String itemName;
    private final int available;
    private final int requested;
    
    public InsufficientStockException(String itemName, int available, int requested) {
        super(String.format("Insufficient stock for '%s'. Available: %d, Requested: %d", 
                           itemName, available, requested));
        this.itemName = itemName;
        this.available = available;
        this.requested = requested;
    }
    
    public String getItemName() { return itemName; }
    public int getAvailable() { return available; }
    public int getRequested() { return requested; }
}
