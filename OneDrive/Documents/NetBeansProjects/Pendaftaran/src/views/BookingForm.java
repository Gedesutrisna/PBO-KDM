/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package views;

import database.DBConnection;
import service.BookingService;
import models.Booking;
import models.Room;
import models.RoomItem;
import dao.RoomItemDAO;
import exceptions.InsufficientStockException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author user
 */
public class BookingForm extends javax.swing.JInternalFrame {

    /**
     * Creates new form BookingForm
     */
    DBConnection db = new DBConnection();
    // Service Layer
    private final BookingService bookingService;
    
    // UI State
    private DefaultTableModel masterTableModel;
    private DefaultTableModel detailTableModel;
    private Booking selectedBooking;
    private Integer selectedBookingItemId = null;
    private final DateTimeFormatter dateTimeFormatter = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Admin ID (should come from session in real app)
    private final int ADMIN_ID = 3;
    
    // Maps to track objects by their string names
    private java.util.Map<String, Room> roomMap = new java.util.HashMap<>();
    private java.util.Map<String, RoomItem> itemMap = new java.util.HashMap<>();
    
    public BookingForm() {
        // Initialize database connection
        DBConnection db = new DBConnection();
        Connection con = db.connect();
        
        // Initialize service
        this.bookingService = new BookingService(con);
        
        // Initialize UI
        initComponents();
        setupDateFormatters();
        loadComboBoxData();
        refreshMasterTable();
        clearInputs();
    }
    
    // Setup Methods
    
    private void setupDateFormatters() {
        startDateInput.setFormatterFactory(
            new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.DateFormatter(
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                )
            )
        );
        endDateInput.setFormatterFactory(
            new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.DateFormatter(
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                )
            )
        );
    }
    
        private void loadComboBoxData() {
        try {
            // Clear maps
            roomMap.clear();
            itemMap.clear();
            
            // Load rooms
            List<Room> rooms = bookingService.getAllRooms();
            DefaultComboBoxModel<String> roomModel = new DefaultComboBoxModel<>();
            for (Room room : rooms) {
                roomModel.addElement(room.getName());
                roomMap.put(room.getName(), room); // Track room object
            }
            roomSelect.setModel(roomModel);
            roomSelect.setSelectedIndex(-1);
            
            // Load items
            List<RoomItem> items = bookingService.getAllItems();
            DefaultComboBoxModel<String> itemModel = new DefaultComboBoxModel<>();
            for (RoomItem item : items) {
                itemModel.addElement(item.getName());
                itemMap.put(item.getName(), item); // Track item object
            }
            itemSelect.setModel(itemModel);
            itemSelect.setSelectedIndex(-1);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load dropdown data:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper Methods
    /**
    * Validate datetime string format
    */
    private boolean isValidDateTimeFormat(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return false;
        }
        return dateTimeStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    /**
     * Show detailed error dialog for datetime parsing
     */
    private void showDateTimeFormatError(String fieldName, String value) {
        JOptionPane.showMessageDialog(this,
            "Invalid " + fieldName + " format!\n\n" +
            "Current value: '" + value + "'\n\n" +
            "Required format: yyyy-MM-dd HH:mm:ss\n" +
            "Example: 2024-12-25 14:30:00\n\n" +
            "Please check your date/time input.",
            "Date Format Error",
            JOptionPane.ERROR_MESSAGE);
    }
    private Booking buildBookingFromForm() {
        Booking booking = new Booking();
        booking.setBorrowerName(borrowerNameInput.getText().trim());
        booking.setBorrowerTelephone(borrowerTelephoneInput.getText().trim());
        booking.setInstitution(institutionInput.getText().trim());
        booking.setEvent(eventInput.getText().trim());
        booking.setAdminId(ADMIN_ID);

        // Parse dates with validation
        try {
            String startStr = startDateInput.getText().trim();
            String endStr = endDateInput.getText().trim();

            // Debug
            System.out.println("DEBUG - Creating booking with dates:");
            System.out.println("  Start: '" + startStr + "'");
            System.out.println("  End: '" + endStr + "'");

            // Validate not empty
            if (startStr.isEmpty() || endStr.isEmpty()) {
                throw new IllegalArgumentException("Date fields cannot be empty");
            }

            // Validate format
            if (!startStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException(
                    "Invalid start date format. Use: yyyy-MM-dd HH:mm:ss\n" +
                    "Example: 2024-12-25 14:30:00"
                );
            }

            if (!endStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException(
                    "Invalid end date format. Use: yyyy-MM-dd HH:mm:ss\n" +
                    "Example: 2024-12-25 16:30:00"
                );
            }

            LocalDateTime start = LocalDateTime.parse(startStr, dateTimeFormatter);
            LocalDateTime end = LocalDateTime.parse(endStr, dateTimeFormatter);

            booking.setStartDateTime(start);
            booking.setEndDateTime(end);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Failed to parse date.\n" +
                "Error: " + e.getMessage() + "\n" +
                "Use format: yyyy-MM-dd HH:mm:ss"
            );
        }

        // Set room
        String selectedRoomName = (String) roomSelect.getSelectedItem();
        if (selectedRoomName != null && !selectedRoomName.isEmpty()) {
            Room selectedRoom = roomMap.get(selectedRoomName);
            if (selectedRoom != null) {
                booking.setRoom(selectedRoom);
            }
        }

        return booking;
    }
    
    private void updateBookingFromForm(Booking booking) {
        // Update basic fields
        booking.setBorrowerName(borrowerNameInput.getText().trim());
        booking.setBorrowerTelephone(borrowerTelephoneInput.getText().trim());
        booking.setInstitution(institutionInput.getText().trim());
        booking.setEvent(eventInput.getText().trim());

        // Parse dates with better error handling
        try {
            String startStr = startDateInput.getText().trim();
            String endStr = endDateInput.getText().trim();

            // Debug: Print what we're trying to parse
            System.out.println("DEBUG - Start Date String: '" + startStr + "'");
            System.out.println("DEBUG - End Date String: '" + endStr + "'");

            // Validate format before parsing
            if (startStr.isEmpty() || endStr.isEmpty()) {
                throw new IllegalArgumentException("Date fields cannot be empty");
            }

            // Check if format looks correct (basic validation)
            if (!startStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException(
                    "Invalid start date format: '" + startStr + "'\n" +
                    "Expected format: yyyy-MM-dd HH:mm:ss\n" +
                    "Example: 2024-12-25 14:30:00"
                );
            }

            if (!endStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                throw new IllegalArgumentException(
                    "Invalid end date format: '" + endStr + "'\n" +
                    "Expected format: yyyy-MM-dd HH:mm:ss\n" +
                    "Example: 2024-12-25 16:30:00"
                );
            }

            LocalDateTime start = LocalDateTime.parse(startStr, dateTimeFormatter);
            LocalDateTime end = LocalDateTime.parse(endStr, dateTimeFormatter);

            booking.setStartDateTime(start);
            booking.setEndDateTime(end);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "Invalid date format. Please use: yyyy-MM-dd HH:mm:ss\n" +
                "Error: " + e.getMessage()
            );
        }

        // Set room - get from map using selected name
        String selectedRoomName = (String) roomSelect.getSelectedItem();
        if (selectedRoomName != null && !selectedRoomName.isEmpty()) {
            Room selectedRoom = roomMap.get(selectedRoomName);
            if (selectedRoom != null) {
                booking.setRoom(selectedRoom);
            } else {
                throw new IllegalArgumentException("Selected room not found: " + selectedRoomName);
            }
        }
    }
    
    /**
    * Update remaining stock label when item is selected
    */
    private void updateRemainingStockLabel() {
        String selectedItemName = (String) itemSelect.getSelectedItem();

        if (selectedItemName == null || selectedItemName.isEmpty()) {
            remainingLabel.setText("Remaining: -");
            return;
        }

        try {
            RoomItem item = itemMap.get(selectedItemName);
            if (item != null) {
                int remaining = item.getQuantity();
                remainingLabel.setText(String.format("Remaining: %d", remaining));

                // Optional: Change color based on stock level
                if (remaining == 0) {
                    remainingLabel.setForeground(java.awt.Color.RED);
                } else if (remaining < 5) {
                    remainingLabel.setForeground(java.awt.Color.ORANGE);
                } else {
                    remainingLabel.setForeground(java.awt.Color.BLACK);
                }
            } else {
                remainingLabel.setText("Remaining: -");
            }
        } catch (Exception e) {
            remainingLabel.setText("Remaining: Error");
        }
    }
    
    private void populateFormWithBooking(Booking booking) {
        borrowerNameInput.setText(booking.getBorrowerName());
        borrowerTelephoneInput.setText(booking.getBorrowerTelephone());
        institutionInput.setText(booking.getInstitution());
        eventInput.setText(booking.getEvent());

        // Format datetime dengan benar
        if (booking.getStartDateTime() != null) {
            String startFormatted = booking.getStartDateTime().format(dateTimeFormatter);
            startDateInput.setText(startFormatted);
            System.out.println("DEBUG - Populated start date: " + startFormatted);
        }

        if (booking.getEndDateTime() != null) {
            String endFormatted = booking.getEndDateTime().format(dateTimeFormatter);
            endDateInput.setText(endFormatted);
            System.out.println("DEBUG - Populated end date: " + endFormatted);
        }

        if (booking.getRoom() != null) {
            roomSelect.setSelectedItem(booking.getRoom().getName());
        }

        toggleMasterInputs(false);
    }
    
    private void refreshMasterTable() {
        masterTableModel = new DefaultTableModel(
            null,
            new Object[] { "ID", "Borrower", "Event", "Start Date", "End Date", "Room" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingTable.setModel(masterTableModel);
        
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            for (Booking b : bookings) {
                masterTableModel.addRow(new Object[] {
                    b.getId(),
                    b.getBorrowerName(),
                    b.getEvent(),
                    b.getStartDateTime().format(dateTimeFormatter),
                    b.getEndDateTime().format(dateTimeFormatter),
                    b.getRoom() != null ? b.getRoom().getName() : "-"
                });
            }
            
            // Hide ID column
            bookingTable.getColumnModel().getColumn(0).setMinWidth(0);
            bookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
            bookingTable.getColumnModel().getColumn(0).setPreferredWidth(0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load bookings:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshDetailTable(Integer bookingId) {
        detailTableModel = new DefaultTableModel(
            null,
            new Object[] { "ID", "Item", "Quantity" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        itemBookingTable.setModel(detailTableModel);
        
        try {
            List<RoomItemDAO.BookingItemDetail> items = 
                bookingService.getBookingItems(bookingId);
            
            for (RoomItemDAO.BookingItemDetail item : items) {
                detailTableModel.addRow(new Object[] {
                    item.id,
                    item.itemName,
                    item.quantity
                });
            }
            
            // Hide ID column
            itemBookingTable.getColumnModel().getColumn(0).setMinWidth(0);
            itemBookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
            itemBookingTable.getColumnModel().getColumn(0).setPreferredWidth(0);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load booking items:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleMasterInputs(boolean enable) {
        borrowerNameInput.setEnabled(enable);
        borrowerTelephoneInput.setEnabled(enable);
        institutionInput.setEnabled(enable);
        eventInput.setEnabled(enable);
        startDateInput.setEnabled(enable);
        endDateInput.setEnabled(enable);
        roomSelect.setEnabled(enable);
        
        if (enable) {
            createButton.setText("Save Booking");
        } else {
            createButton.setText("Update Booking");
        }
    }
    
    private void clearInputs() {
        borrowerNameInput.setText("");
        borrowerTelephoneInput.setText("");
        institutionInput.setText("");
        eventInput.setText("");

        // Set default dates to today
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        startDateInput.setText(now.format(dateTimeFormatter));
        endDateInput.setText(tomorrow.format(dateTimeFormatter));

        roomSelect.setSelectedIndex(-1);
        itemSelect.setSelectedIndex(-1);
        quantityInput1.setValue(0);
        remainingLabel.setText("Remaining: -");

        selectedBooking = null;
        bookingTable.clearSelection();

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        tambahItemButton.setEnabled(false);
        editItemButton.setEnabled(false);  // Disable edit item button
        createButton.setText("Create Booking");

        toggleMasterInputs(true);

        // Clear detail table
        if (detailTableModel != null) {
            detailTableModel.setRowCount(0);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        borrowerNameInput = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        capacityLabel = new javax.swing.JLabel();
        borrowerTelephoneInput = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        institutionInput = new javax.swing.JTextField();
        createButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        eventInput = new javax.swing.JTextField();
        locationLabel1 = new javax.swing.JLabel();
        locationLabel2 = new javax.swing.JLabel();
        locationLabel3 = new javax.swing.JLabel();
        startDateInput = new javax.swing.JFormattedTextField();
        endDateInput = new javax.swing.JFormattedTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        bookingTable = new javax.swing.JTable();
        roomSelect = new javax.swing.JComboBox<>();
        quantityInput1 = new javax.swing.JSpinner();
        quantityLabel1 = new javax.swing.JLabel();
        roomLabel1 = new javax.swing.JLabel();
        itemSelect = new javax.swing.JComboBox<>();
        roomLabel2 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        itemBookingTable = new javax.swing.JTable();
        tambahItemButton = new javax.swing.JButton();
        remainingLabel = new javax.swing.JLabel();
        editItemButton = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        borrowerNameInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrowerNameInputActionPerformed(evt);
            }
        });

        nameLabel.setText("Borrower Name");

        capacityLabel.setText("Borrower Telephone");

        borrowerTelephoneInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrowerTelephoneInputActionPerformed(evt);
            }
        });

        locationLabel.setText("Institution");

        institutionInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                institutionInputActionPerformed(evt);
            }
        });

        createButton.setText("Tambah");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Hapus");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Batal");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        eventInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventInputActionPerformed(evt);
            }
        });

        locationLabel1.setText("Event");

        locationLabel2.setText("Start Date");

        locationLabel3.setText("End Date");

        bookingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        bookingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookingTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(bookingTable);

        roomSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        roomSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roomSelectActionPerformed(evt);
            }
        });

        quantityLabel1.setText("Quantity");

        roomLabel1.setText("Room");

        itemSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        itemSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemSelectActionPerformed(evt);
            }
        });

        roomLabel2.setText("Item");

        itemBookingTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        itemBookingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemBookingTableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(itemBookingTable);

        tambahItemButton.setText("Tambah Item");
        tambahItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahItemButtonActionPerformed(evt);
            }
        });

        remainingLabel.setText("Remaining");

        editItemButton.setText("Edit Item");
        editItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editItemButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(createButton)
                                .addGap(18, 18, 18)
                                .addComponent(editButton)
                                .addGap(18, 18, 18)
                                .addComponent(deleteButton)
                                .addGap(18, 18, 18)
                                .addComponent(cancelButton))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameLabel)
                            .addComponent(locationLabel)
                            .addComponent(locationLabel2)
                            .addComponent(roomLabel1)
                            .addComponent(roomSelect, 0, 144, Short.MAX_VALUE)
                            .addComponent(borrowerNameInput)
                            .addComponent(institutionInput)
                            .addComponent(startDateInput))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(quantityLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(remainingLabel))
                            .addComponent(quantityInput1)
                            .addComponent(capacityLabel)
                            .addComponent(locationLabel1)
                            .addComponent(locationLabel3)
                            .addComponent(endDateInput)
                            .addComponent(eventInput)
                            .addComponent(borrowerTelephoneInput)
                            .addComponent(roomLabel2)
                            .addComponent(itemSelect, 0, 166, Short.MAX_VALUE))))
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tambahItemButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editItemButton))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(borrowerNameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(capacityLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(borrowerTelephoneInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(locationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(institutionInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(locationLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(eventInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel2)
                    .addComponent(locationLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDateInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(endDateInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(roomLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(roomSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(roomLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(itemSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quantityLabel1)
                    .addComponent(remainingLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quantityInput1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createButton)
                    .addComponent(deleteButton)
                    .addComponent(editButton)
                    .addComponent(cancelButton)
                    .addComponent(tambahItemButton)
                    .addComponent(editItemButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void borrowerNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowerNameInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_borrowerNameInputActionPerformed

    private void institutionInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_institutionInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_institutionInputActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
         if (selectedBooking == null) {
            // CREATE NEW BOOKING
            createNewBooking();
        } else {
            // UPDATE EXISTING BOOKING
            updateExistingBooking();
        }
    }//GEN-LAST:event_createButtonActionPerformed
   
    private void createNewBooking() {
        Booking booking = buildBookingFromForm();
        
        try {
            Booking saved = bookingService.createBooking(booking);
            selectedBooking = saved;
            
            JOptionPane.showMessageDialog(this,
                "Booking created successfully! You can now add items.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            toggleMasterInputs(false);
            tambahItemButton.setEnabled(true);
            createButton.setText("Update Booking");
            refreshMasterTable();
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to create booking:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateExistingBooking() {
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this,
                "No booking selected",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("=== UPDATE BOOKING DEBUG ===");
        System.out.println("Booking ID: " + selectedBooking.getId());
        System.out.println("Current Start Date: " + selectedBooking.getStartDateTime());
        System.out.println("Current End Date: " + selectedBooking.getEndDateTime());
        System.out.println("Form Start Input: '" + startDateInput.getText() + "'");
        System.out.println("Form End Input: '" + endDateInput.getText() + "'");

        try {
            // Update booking object with form data
            updateBookingFromForm(selectedBooking);

            System.out.println("After update:");
            System.out.println("New Start Date: " + selectedBooking.getStartDateTime());
            System.out.println("New End Date: " + selectedBooking.getEndDateTime());

            bookingService.updateBooking(selectedBooking);

            JOptionPane.showMessageDialog(this,
                "Booking updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            toggleMasterInputs(false);
            refreshMasterTable();

        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            System.err.println("Update error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to update booking:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void eventInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventInputActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking from the table first.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this booking?\n" +
            "Borrower: " + selectedBooking.getBorrowerName() + "\n" +
            "Event: " + selectedBooking.getEvent(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookingService.deleteBooking(selectedBooking.getId());
                
                JOptionPane.showMessageDialog(this,
                    "Booking deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                refreshMasterTable();
                clearInputs();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete booking:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void borrowerTelephoneInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowerTelephoneInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_borrowerTelephoneInputActionPerformed

    private void roomSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roomSelectActionPerformed

    private void itemSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSelectActionPerformed
        // TODO add your handling code here:
        updateRemainingStockLabel();
    }//GEN-LAST:event_itemSelectActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        clearInputs();
    }//GEN-LAST:event_cancelButtonActionPerformed
                                    
    private void toggleMasterInput(boolean enable) {
        borrowerNameInput.setEnabled(enable);
        borrowerTelephoneInput.setEnabled(enable);
        institutionInput.setEnabled(enable);
        eventInput.setEnabled(enable);
        startDateInput.setEnabled(enable);
        endDateInput.setEnabled(enable);
        roomSelect.setEnabled(enable);
        // Jika enable = false, tombol Master jadi Simpan Ulang Master (seperti di createButtonActionPerformed)
        if (enable) {
             createButton.setText("Tambah Peminjaman");
        } else {
             createButton.setText("Simpan Ulang Master");
        }
    }
    
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
        if (selectedBooking == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a booking from the table first.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        toggleMasterInputs(true);
    }//GEN-LAST:event_editButtonActionPerformed

    private void tambahItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahItemButtonActionPerformed
        if (selectedBooking == null || selectedBooking.getId() == null) {
            JOptionPane.showMessageDialog(this,
                "Please save the booking first before adding items.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedItemName = (String) itemSelect.getSelectedItem();
        Integer quantity = (Integer) quantityInput1.getValue();

        if (selectedItemName == null || selectedItemName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select an item.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantity == null || quantity <= 0) {
            JOptionPane.showMessageDialog(this,
                "Quantity must be greater than 0.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            bookingService.addItemToBooking(
                selectedBooking.getId(),
                selectedItemName,
                quantity
            );

            JOptionPane.showMessageDialog(this,
                String.format("Added %d x %s to booking", quantity, selectedItemName),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Reset item selection
            selectedBookingItemId = null;  // ← RESET
            itemSelect.setSelectedIndex(-1);
            quantityInput1.setValue(0);
            remainingLabel.setText("Remaining: -");
            editItemButton.setEnabled(false);  // ← DISABLE
            tambahItemButton.setText("Tambah Item");  // ← RESET TEXT

            // Refresh detail table and reload combo data
            refreshDetailTable(selectedBooking.getId());
            loadComboBoxData(); // ← RELOAD to update stock

        } catch (InsufficientStockException e) {
            JOptionPane.showMessageDialog(this,
                String.format("Insufficient stock!\n\nItem: %s\nAvailable: %d\nRequested: %d",
                    e.getItemName(), e.getAvailable(), e.getRequested()),
                "Stock Error",
                JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to add item:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_tambahItemButtonActionPerformed

    private void bookingTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookingTableMouseClicked
        // TODO add your handling code here:
                int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            Integer bookingId = (Integer) masterTableModel.getValueAt(selectedRow, 0);
            
            try {
                selectedBooking = bookingService.getBookingById(bookingId);
                if (selectedBooking != null) {
                    populateFormWithBooking(selectedBooking);
                    refreshDetailTable(bookingId);
                    
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    tambahItemButton.setEnabled(true);
                    createButton.setText("Update Booking");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load booking details:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_bookingTableMouseClicked

    private void editItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editItemButtonActionPerformed
        // TODO add your handling code here:
        if (selectedBookingItemId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an item from the booking items table first.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedBooking == null || selectedBooking.getId() == null) {
            JOptionPane.showMessageDialog(this,
                "No active booking selected.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedItemName = (String) itemSelect.getSelectedItem();
        Integer newQuantity = (Integer) quantityInput1.getValue();

        if (selectedItemName == null || selectedItemName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select an item.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newQuantity == null || newQuantity <= 0) {
            JOptionPane.showMessageDialog(this,
                "Quantity must be greater than 0.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Update item quantity to %d?\n\nNote: This will adjust stock accordingly.", newQuantity),
            "Confirm Edit",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete old booking item (this restores old stock)
                bookingService.deleteBookingItem(selectedBookingItemId);

                // Add new booking item with new quantity (this decreases stock)
                bookingService.addItemToBooking(
                    selectedBooking.getId(),
                    selectedItemName,
                    newQuantity
                );

                JOptionPane.showMessageDialog(this,
                    "Item updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Reset selection
                selectedBookingItemId = null;
                itemSelect.setSelectedIndex(-1);
                quantityInput1.setValue(0);
                remainingLabel.setText("Remaining: -");
                editItemButton.setEnabled(false);
                tambahItemButton.setText("Tambah Item");

                // Refresh
                refreshDetailTable(selectedBooking.getId());
                loadComboBoxData(); // Reload to update stock info

            } catch (InsufficientStockException e) {
                JOptionPane.showMessageDialog(this,
                    String.format("Insufficient stock!\n\nItem: %s\nAvailable: %d\nRequested: %d",
                        e.getItemName(), e.getAvailable(), e.getRequested()),
                    "Stock Error",
                    JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to update item:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_editItemButtonActionPerformed

    private void itemBookingTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_itemBookingTableMouseClicked
        // TODO add your handling code here:
        int selectedRow = itemBookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get booking item ID from hidden column
            selectedBookingItemId = (Integer) detailTableModel.getValueAt(selectedRow, 0);

            // Get item details
            String itemName = detailTableModel.getValueAt(selectedRow, 1).toString();
            Integer quantity = Integer.valueOf(detailTableModel.getValueAt(selectedRow, 2).toString());

            // Populate to form
            itemSelect.setSelectedItem(itemName);
            quantityInput1.setValue(quantity);
            updateRemainingStockLabel();

            // Enable edit button
            editItemButton.setEnabled(true);
            tambahItemButton.setText("Add New Item");

            System.out.println("Selected booking item ID: " + selectedBookingItemId);
        }
    }//GEN-LAST:event_itemBookingTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable bookingTable;
    private javax.swing.JTextField borrowerNameInput;
    private javax.swing.JTextField borrowerTelephoneInput;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel capacityLabel;
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editItemButton;
    private javax.swing.JFormattedTextField endDateInput;
    private javax.swing.JTextField eventInput;
    private javax.swing.JTextField institutionInput;
    private javax.swing.JTable itemBookingTable;
    private javax.swing.JComboBox<String> itemSelect;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel locationLabel1;
    private javax.swing.JLabel locationLabel2;
    private javax.swing.JLabel locationLabel3;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JSpinner quantityInput1;
    private javax.swing.JLabel quantityLabel1;
    private javax.swing.JLabel remainingLabel;
    private javax.swing.JLabel roomLabel1;
    private javax.swing.JLabel roomLabel2;
    private javax.swing.JComboBox<String> roomSelect;
    private javax.swing.JFormattedTextField startDateInput;
    private javax.swing.JButton tambahItemButton;
    // End of variables declaration//GEN-END:variables
}
