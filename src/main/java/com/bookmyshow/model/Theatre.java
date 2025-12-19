package com.bookmyshow.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class Theatre {
    private String id;
    private String name;
    private String city;
    private String address;
    private double taxPercentage;
    private String adminId;
    private long createdAt;

    public Theatre(String name, String city, String address, double taxPercentage, String adminId) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.taxPercentage = taxPercentage;
        this.adminId = adminId;
        this.createdAt = System.currentTimeMillis();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("city", city);
        json.addProperty("address", address);
        json.addProperty("taxPercentage", taxPercentage);
        json.addProperty("adminId", adminId);
        json.addProperty("createdAt", createdAt);
        return json;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(double taxPercentage) { this.taxPercentage = taxPercentage; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
