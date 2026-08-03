package com.findjob.jobboard.model;

/**
 * Currency Enum - Supported currencies for job postings
 */
public enum Currency {
    USD("$", "US Dollar"),
    EUR("€", "Euro"),
    GBP("£", "British Pound"),
    NGN("₦", "Nigerian Naira"),
    CAD("C$", "Canadian Dollar"),
    AUD("A$", "Australian Dollar"),
    INR("₹", "Indian Rupee"),
    JPY("¥", "Japanese Yen"),
    CHF("CHF", "Swiss Franc");
    
    private final String symbol;
    private final String name;
    
    Currency(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return symbol + " - " + name;
    }
}
