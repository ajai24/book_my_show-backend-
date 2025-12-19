package com.bookmyshow.model;

import com.google.gson.JsonObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Admin {
    private String id;
    private String email;
    private String password;
    private String name;
    private String theatreId;
    private long createdAt;

    public Admin(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
    }

    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) 
            throws NoSuchAlgorithmException {
        String hashedPlain = hashPassword(plainPassword);
        return hashedPlain.equals(hashedPassword);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("email", email);
        json.addProperty("name", name);
        if (theatreId != null) json.addProperty("theatreId", theatreId);
        json.addProperty("createdAt", createdAt);
        return json;
    }

    public JsonObject toJsonWithPassword() {
        JsonObject json = toJson();
        json.addProperty("password", password);
        return json;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTheatreId() { return theatreId; }
    public void setTheatreId(String theatreId) { this.theatreId = theatreId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
