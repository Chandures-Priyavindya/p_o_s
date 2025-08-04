package com.residuesolution.pos.enums;

public enum InventoryChangeType {
    ADD,            // Adding new stock (purchase, restock)
    REMOVE,         // Removing stock (sale, damage, theft)
    ADJUST,         // Manual stock adjustment (inventory count correction)
    SALE,           // Stock reduction due to sale
    SALE_RETURN,    // Stock increase due to sale return
    PURCHASE,       // Stock increase due to purchase
    PURCHASE_RETURN, // Stock decrease due to purchase return
    DAMAGE,         // Stock decrease due to damaged goods
    THEFT,          // Stock decrease due to theft
    EXPIRED,        // Stock decrease due to expired products
    TRANSFER_IN,    // Stock increase due to transfer from another location
    TRANSFER_OUT,   // Stock decrease due to transfer to another location
    INITIAL_STOCK,  // Initial stock entry
    AUDIT_ADJUSTMENT // Stock adjustment during audit
}