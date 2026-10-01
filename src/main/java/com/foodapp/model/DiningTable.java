package com.foodapp.model;

import jakarta.persistence.*;

/**
 * A physical dining table in the restaurant. Every table has its own
 * table number (e.g. F1, FM1, C1) which is shown to the customer when
 * they book, and later printed on the bill for that order.
 */
@Entity
@Table(name = "dining_tables")
public class DiningTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tableNumber; // e.g. "F1", "FM1", "C1" - separate per table

    @Enumerated(EnumType.STRING)
    private TableType type;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private TableStatus status = TableStatus.AVAILABLE;

    // ---- Who currently holds the booking (cleared when the table is released) ----
    private String bookedByName;
    private String bookedByPhone;

    public enum TableType {
        FOUR_SEATER, FAMILY, COUPLE
    }

    public enum TableStatus {
        AVAILABLE, BOOKED
    }

    // ---- Getters and setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public TableType getType() { return type; }
    public void setType(TableType type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }

    public String getBookedByName() { return bookedByName; }
    public void setBookedByName(String bookedByName) { this.bookedByName = bookedByName; }

    public String getBookedByPhone() { return bookedByPhone; }
    public void setBookedByPhone(String bookedByPhone) { this.bookedByPhone = bookedByPhone; }
}
