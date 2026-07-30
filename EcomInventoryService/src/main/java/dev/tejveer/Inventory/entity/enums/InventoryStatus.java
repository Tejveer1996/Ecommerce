package dev.tejveer.Inventory.entity.enums;

/**
 * Active -> Inventory is available for sale
 * INACTIVE -> Inventory disabled for some reasons of seller like discontinued product, seasonal product and all
 * BLOCKED -> Inventory frozen because of some issue like defective batch, legal compliance and all
 */
public enum InventoryStatus {
    ACTIVE, INACTIVE, BLOCKED
}
