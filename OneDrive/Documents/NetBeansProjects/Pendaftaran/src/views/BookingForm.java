/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package views;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
/**
 *
 * @author user
 */
public class BookingForm extends javax.swing.JInternalFrame {

    /**
     * Creates new form BookingForm
     */
DBConnection db = new DBConnection();
    Connection con;
    DefaultTableModel masterTm; // Tabel untuk data Peminjaman (bookings)
    
    // Variabel untuk menyimpan ID Booking saat data dipilih dari tabel
    private int selectedBookingId = -1;
    // Format Tanggal dan Waktu
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BookingForm() {
        initComponents();
        connect();
        loadInitialData();
        refreshMasterTable();
        clearInputs();
        // Mengatur format input tanggal/waktu (pastikan library JFormattedTextField sudah benar)
        startDateInput.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(dateFormat)));
        endDateInput.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(dateFormat)));
    }

    private void connect() {
        con = db.connect();
    }
    
    private void loadInitialData() {
        // Asumsi Admin ID 1 sudah ada. Di aplikasi nyata, ini harus diambil dari sesi login.
        // loadAdminId(); 
        loadRoomsToComboBox(roomSelect);
        loadItemsToComboBox(itemSelect);
    }
    
    private void clearInputs() {
        borrowerNameInput.setText("");
        borrowerTelephoneInput.setText("");
        institutionInput.setText("");
        eventInput.setText("");
        startDateInput.setText("");
        endDateInput.setText("");
        roomSelect.setSelectedIndex(-1);
        
        // Input Item/Detail Peminjaman (di sini kita tetap menggunakan 1 baris input untuk menambah item satu per satu)
        itemSelect.setSelectedIndex(-1);
        quantityInput1.setValue(0);
        
        selectedBookingId = -1;
        createButton.setText("Tambah Peminjaman");
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    // Method pembantu untuk ComboBox
    private void loadRoomsToComboBox(javax.swing.JComboBox<String> comboBox) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        String sql = "SELECT name FROM rooms ORDER BY name";
        try (Statement s = con.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                model.addElement(r.getString("name"));
            }
            comboBox.setModel(model);
            comboBox.setSelectedIndex(-1);
        } catch (Exception e) {
            System.out.println("ERROR MEMUAT DAFTAR RUANGAN: " + e.getMessage());
        }
    }
    
    private void loadItemsToComboBox(javax.swing.JComboBox<String> comboBox) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        // Item yang ditampilkan adalah item yang ada di database (item yang dapat dipinjam)
        String sql = "SELECT name FROM room_items ORDER BY name";
        try (Statement s = con.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                model.addElement(r.getString("name"));
            }
            comboBox.setModel(model);
            comboBox.setSelectedIndex(-1);
        } catch (Exception e) {
            System.out.println("ERROR MEMUAT DAFTAR ITEM: " + e.getMessage());
        }
    }
    
    // Method untuk mendapatkan ID dari Nama (Room atau Item)
    private int getIdByName(String tableName, String name) {
        int id = -1;
        String sql = "SELECT id FROM " + tableName + " WHERE name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (Exception e) {
            System.out.println("ERROR GETTING ID FOR " + tableName + ": " + e.getMessage());
        }
        return id;
    }
    
    // Refresh tabel utama (Master Booking)
    private void refreshMasterTable() {
        masterTm = new DefaultTableModel(
            null,
            new Object[] { "ID", "Peminjam", "Event", "Start Date", "End Date", "Ruangan" }
        );
        bookingTable.setModel(masterTm);
        masterTm.getDataVector().removeAllElements ();
        try {
            // Join bookings dan room_bookings dan rooms untuk menampilkan nama ruangan
            String sql = "SELECT b.id, b.borrower_name, b.event, b.start_date, b.end_date, r.name AS room_name " +
                         "FROM bookings b " +
                         "JOIN room_bookings rb ON b.id = rb.booking_id " +
                         "JOIN rooms r ON rb.room_id = r.id " +
                         "ORDER BY b.start_date DESC";
            
            PreparedStatement s = con.prepareStatement (sql);
            ResultSet r = s.executeQuery();
            while (r.next()) {
                Object[] data = {
                    r.getInt ("id"), 
                    r.getString ("borrower_name"), 
                    r.getString ("event"), 
                    r.getString ("start_date"), 
                    r.getString ("end_date"),
                    r.getString ("room_name")
                };
                masterTm.addRow(data);
            }
            // Sembunyikan kolom ID (kolom 0) dari tampilan pengguna
            bookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
            bookingTable.getColumnModel().getColumn(0).setMinWidth(0);
            bookingTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        }catch (Exception e) {
            System.out.print ("ERROR KUERI KE DATABASE: \n" + e + "\n\n");
            JOptionPane.showMessageDialog(this, "Gagal memuat data peminjaman:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
        jScrollPane5.setViewportView(itemBookingTable);

        tambahItemButton.setText("Tambah Item");
        tambahItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahItemButtonActionPerformed(evt);
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
                            .addComponent(quantityLabel1)
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
                    .addComponent(tambahItemButton)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
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
                .addComponent(quantityLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quantityInput1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createButton)
                    .addComponent(deleteButton)
                    .addComponent(editButton)
                    .addComponent(cancelButton))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74))
            .addGroup(layout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addComponent(tambahItemButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void borrowerNameInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowerNameInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_borrowerNameInputActionPerformed

    private void institutionInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_institutionInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_institutionInputActionPerformed
    // Fungsi untuk menampilkan item yang dipinjam pada detail tabel
    private void refreshDetailTable(int bookingId) {
        DefaultTableModel detailTm = new DefaultTableModel(
            null,
            new Object[] { "ID Detail", "Item", "Qty Dipinjam" }
        );
        // !!! GANTI INI DARI bookingTable MENJADI itemBookingTable !!!
        itemBookingTable.setModel(detailTm); 
        // !!! GANTI INI DARI bookingTable MENJADI itemBookingTable !!!

        detailTm.getDataVector().removeAllElements ();
        try {
            // Join room_items_booking dan room_items untuk mendapatkan nama item
            String sql = "SELECT rib.id, ri.name, rib.quantity " +
                         "FROM room_items_booking rib " +
                         "JOIN room_items ri ON rib.room_item_id = ri.id " +
                         "WHERE rib.booking_id = ?";

            PreparedStatement ps = con.prepareStatement (sql);
            ps.setInt(1, bookingId);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                Object[] data = {
                    r.getInt ("id"), // ID Detail (untuk hapus detail item)
                    r.getString ("name"), 
                    r.getInt ("quantity")
                };
                detailTm.addRow(data);
            }
             // Sembunyikan kolom ID Detail (kolom 0)
            itemBookingTable.getColumnModel().getColumn(0).setMaxWidth(0);
            itemBookingTable.getColumnModel().getColumn(0).setMinWidth(0);
            itemBookingTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        }catch (Exception e) {
            System.out.print ("ERROR KUERI DETAIL ITEM: \n" + e + "\n\n");
            JOptionPane.showMessageDialog(this, "Gagal memuat detail item:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // TODO add your handling code here:
        String name = borrowerNameInput.getText();
        String telp = borrowerTelephoneInput.getText();
        String inst = institutionInput.getText();
        String event = eventInput.getText();
        String startDateStr = startDateInput.getText();
        String endDateStr = endDateInput.getText();
        String roomName = (String) roomSelect.getSelectedItem();
        
        // Asumsi admin_id 1 (di aplikasi nyata, ambil dari sesi)
        int adminId = 3; 

        if (name.isEmpty() || telp.isEmpty() || inst.isEmpty() || event.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || roomName == null) {
            JOptionPane.showMessageDialog(this, "Semua field master peminjaman harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedBookingId == -1) {
            // --- LOGIKA TAMBAH BARU (INSERT) ---
            try {
                // 1. INSERT ke tabel 'bookings'
                String sqlBooking = "INSERT INTO bookings (borrower_name, borrower_telephone, admin_id, institution, event, start_date, end_date, start_time, end_time) VALUES (?,?,?,?,?,?,?,?,?)";
                PreparedStatement psBooking = con.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS);
                
                // Pisahkan Tanggal dan Waktu (Asumsi format YYYY-MM-DD HH:MM:SS)
                String dateStart = startDateStr.substring(0, 10);
                String timeStart = startDateStr.substring(11);
                String dateEnd = endDateStr.substring(0, 10);
                String timeEnd = endDateStr.substring(11);

                psBooking.setString(1, name);
                psBooking.setString(2, telp);        
                psBooking.setInt(3, adminId);
                psBooking.setString(4, inst);
                psBooking.setString(5, event);
                psBooking.setString(6, dateStart); // start_date
                psBooking.setString(7, dateEnd); // end_date
                psBooking.setString(8, timeStart); // start_time (hanya waktu)
                psBooking.setString(9, timeEnd); // end_time (hanya waktu)

                int affectedRows = psBooking.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = psBooking.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        selectedBookingId = generatedKeys.getInt(1); // Ambil ID Booking yang baru
                        
                        // 2. INSERT ke tabel 'room_bookings'
                        int roomId = getIdByName("rooms", roomName);
                        if(roomId != -1) {
                            String sqlRoomBooking = "INSERT INTO room_bookings (booking_id, room_id) VALUES (?,?)";
                            PreparedStatement psRoomBooking = con.prepareStatement(sqlRoomBooking);
                            psRoomBooking.setInt(1, selectedBookingId);
                            psRoomBooking.setInt(2, roomId);
                            psRoomBooking.executeUpdate();
                            
                            JOptionPane.showMessageDialog(this, "Peminjaman berhasil ditambahkan. Silakan tambahkan item yang dipinjam.");
                            
                            // Setelah berhasil, kunci input Master dan aktifkan input Detail
                            toggleMasterInput(false);
                            tambahItemButton.setEnabled(true);
                            createButton.setText("Simpan Ulang Master");
                        } else {
                            JOptionPane.showMessageDialog(this, "Ruangan tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

            } catch(Exception e) {
                System.out.print("ERROR QUERY INSERT BOOKING:\n" + e + "\n\n");
                JOptionPane.showMessageDialog(this, "Gagal menambahkan peminjaman:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                selectedBookingId = -1; // Reset jika gagal
            }
        } else {
            // --- LOGIKA EDIT (UPDATE) ---
            try {
                // 1. UPDATE tabel 'bookings'
                 String sqlBooking = "UPDATE bookings SET borrower_name=?, borrower_telephone=?, institution=?, event=?, start_date=?, end_date=?, start_time=?, end_time=? WHERE id=?";
                PreparedStatement psBooking = con.prepareStatement(sqlBooking);
                
                String dateStart = startDateStr.substring(0, 10);
                String timeStart = startDateStr.substring(11);
                String dateEnd = endDateStr.substring(0, 10);
                String timeEnd = endDateStr.substring(11);

                psBooking.setString(1, name);
                psBooking.setString(2, telp);
                psBooking.setString(3, inst);
                psBooking.setString(4, event);
                psBooking.setString(5, dateStart);
                psBooking.setString(6, dateEnd);
                psBooking.setString(7, timeStart);
                psBooking.setString(8, timeEnd);
                psBooking.setInt(9, selectedBookingId);

                psBooking.executeUpdate();

                // 2. UPDATE tabel 'room_bookings'
                int roomId = getIdByName("rooms", roomName);
                if (roomId != -1) {
                    // Cek apakah relasi sudah ada, jika tidak ada, INSERT, jika ada, UPDATE
                    String checkSql = "SELECT id FROM room_bookings WHERE booking_id = ?";
                    PreparedStatement checkPs = con.prepareStatement(checkSql);
                    checkPs.setInt(1, selectedBookingId);
                    
                    if (checkPs.executeQuery().next()) {
                        String sqlRoomBooking = "UPDATE room_bookings SET room_id=? WHERE booking_id=?";
                        PreparedStatement psRoomBooking = con.prepareStatement(sqlRoomBooking);
                        psRoomBooking.setInt(1, roomId);
                        psRoomBooking.setInt(2, selectedBookingId);
                        psRoomBooking.executeUpdate();
                    } else {
                         String sqlRoomBooking = "INSERT INTO room_bookings (booking_id, room_id) VALUES (?,?)";
                         PreparedStatement psRoomBooking = con.prepareStatement(sqlRoomBooking);
                         psRoomBooking.setInt(1, selectedBookingId);
                         psRoomBooking.setInt(2, roomId);
                         psRoomBooking.executeUpdate();
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Peminjaman berhasil diubah.");
                toggleMasterInput(false);
                refreshMasterTable();
                
            } catch(Exception e) {
                System.out.print("ERROR QUERY UPDATE BOOKING:\n" + e + "\n\n");
                JOptionPane.showMessageDialog(this, "Gagal mengedit peminjaman:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_createButtonActionPerformed

    private void eventInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventInputActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void borrowerTelephoneInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowerTelephoneInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_borrowerTelephoneInputActionPerformed

    private void roomSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roomSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_roomSelectActionPerformed

    private void itemSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemSelectActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        clearInputs();
    }//GEN-LAST:event_cancelButtonActionPerformed
    // --- MOUSE CLICK (Saat Baris Tabel Master Diklik) ---
    private void bookingTableMouseClicked(java.awt.event.MouseEvent evt) {                                        
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Ambil ID dari kolom tersembunyi (kolom 0)
            selectedBookingId = Integer.valueOf(masterTm.getValueAt(selectedRow, 0).toString()); 
            
            // Ambil data dan tampilkan di input
            borrowerNameInput.setText(masterTm.getValueAt(selectedRow, 1).toString());
            eventInput.setText(masterTm.getValueAt(selectedRow, 2).toString());
            
            // Gabungkan tanggal dan waktu dari database menjadi format YYYY-MM-DD HH:MM:SS
            // Di database, start_date dan start_time terpisah, namun di form Anda hanya ada 1 input date/time
            // Logika ini mungkin harus disesuaikan dengan cara Anda menyimpan/mengambil data dari DB
            // Karena di refreshMasterTable kita SELECT b.start_date, b.end_date yang sudah format DateTime
            startDateInput.setText(masterTm.getValueAt(selectedRow, 3).toString()); 
            endDateInput.setText(masterTm.getValueAt(selectedRow, 4).toString());
            
            String roomName = masterTm.getValueAt(selectedRow, 5).toString();
            roomSelect.setSelectedItem(roomName);
            
            // Ambil detail lainnya dari database (telp, institution)
            loadDetailMaster(selectedBookingId);

            // Aktifkan tombol Edit dan Hapus, dan tombol Tambah Item
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            tambahItemButton.setEnabled(true); 
            createButton.setText("Simpan Ulang Master");
            
            // Tampilkan detail item untuk booking yang dipilih
            refreshDetailTable(selectedBookingId);
            toggleMasterInput(false);
        }
    }                                       

    private void loadDetailMaster(int bookingId) {
         try {
            String sql = "SELECT borrower_telephone, institution FROM bookings WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                borrowerTelephoneInput.setText(rs.getString("borrower_telephone"));
                institutionInput.setText(rs.getString("institution"));
            }
        } catch (Exception e) {
            System.out.println("ERROR MEMUAT DETAIL MASTER: " + e.getMessage());
        }
    }
    
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
        if (selectedBookingId != -1) {
            toggleMasterInput(true);
        } else {
             JOptionPane.showMessageDialog(this, "Pilih transaksi peminjaman di tabel yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void tambahItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahItemButtonActionPerformed
        // TODO add your handling code here:
                if (selectedBookingId == -1) {
            JOptionPane.showMessageDialog(this, "Simpan transaksi peminjaman utama (master) terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemName = (String) itemSelect.getSelectedItem();
        Integer quantity = (Integer) quantityInput1.getValue();
        
        if (itemName == null || quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih Item dan Quantity harus lebih dari 0.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int itemId = getIdByName("room_items", itemName);
        if (itemId == -1) {
            JOptionPane.showMessageDialog(this, "Item tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1. Ambil current_quantity dari tabel room_items
            String sqlCheckQty = "SELECT quantity FROM room_items WHERE id = ?";
            PreparedStatement psCheck = con.prepareStatement(sqlCheckQty);
            psCheck.setInt(1, itemId);
            ResultSet rsCheck = psCheck.executeQuery();
            int currentStock = 0;
            if (rsCheck.next()) {
                currentStock = rsCheck.getInt("quantity");
            }
            
            if (quantity > currentStock) {
                JOptionPane.showMessageDialog(this, "Stok item " + itemName + " yang tersedia hanya " + currentStock + ". Quantity tidak mencukupi.", "Peringatan Stok", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. INSERT ke tabel 'room_items_booking'
            String sqlInsertItem = "INSERT INTO room_items_booking (booking_id, room_item_id, quantity, current_quantity) VALUES (?,?,?,?)";
            PreparedStatement psInsert = con.prepareStatement(sqlInsertItem);
            
            psInsert.setInt(1, selectedBookingId);
            psInsert.setInt(2, itemId);        
            psInsert.setInt(3, quantity);
            psInsert.setInt(4, currentStock); // Simpan stok saat itu juga (current_quantity)

            int affectedRows = psInsert.executeUpdate();
            
            if (affectedRows > 0) {
                // 3. UPDATE stok di tabel 'room_items' (kurangi stok)
                String sqlUpdateStock = "UPDATE room_items SET quantity = quantity - ? WHERE id = ?";
                PreparedStatement psUpdate = con.prepareStatement(sqlUpdateStock);
                psUpdate.setInt(1, quantity);
                psUpdate.setInt(2, itemId);
                psUpdate.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Item " + itemName + " sebanyak " + quantity + " berhasil ditambahkan ke peminjaman.");
            }
            
            // Refresh detail table
            refreshDetailTable(selectedBookingId);
            
        } catch(Exception e) {
            System.out.print("ERROR QUERY INSERT DETAIL ITEM:\n" + e + "\n\n");
            JOptionPane.showMessageDialog(this, "Gagal menambahkan item peminjaman:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_tambahItemButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable bookingTable;
    private javax.swing.JTextField borrowerNameInput;
    private javax.swing.JTextField borrowerTelephoneInput;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel capacityLabel;
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
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
    private javax.swing.JLabel roomLabel1;
    private javax.swing.JLabel roomLabel2;
    private javax.swing.JComboBox<String> roomSelect;
    private javax.swing.JFormattedTextField startDateInput;
    private javax.swing.JButton tambahItemButton;
    // End of variables declaration//GEN-END:variables
}
