package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.Admin;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdminRegisterHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseUtil.handleOptions(exchange);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseUtil.sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody()));
            JsonObject requestBody = JsonUtil.readJsonBody(reader);

            // Validate required fields
            if (!requestBody.has("email") || !requestBody.has("password") || 
                !requestBody.has("name")) {
                ResponseUtil.sendError(exchange, 400, "Missing required fields");
                return;
            }

            String email = requestBody.get("email").getAsString();
            String password = requestBody.get("password").getAsString();
            String name = requestBody.get("name").getAsString();

            // Check if email already exists
            MongoCollection<Document> adminsCollection = 
                    DatabaseConnection.getInstance().getCollection("admins");
            
            if (adminsCollection.find(Filters.eq("email", email)).first() != null) {
                ResponseUtil.sendError(exchange, 409, "Email already registered");
                return;
            }

            // Create admin
            Admin admin = new Admin(email, password, name);
            admin.setId(new ObjectId().toString());
            admin.setPassword(Admin.hashPassword(password));

            // Save to database
            Document adminDoc = new Document()
                    .append("_id", new ObjectId(admin.getId()))
                    .append("email", admin.getEmail())
                    .append("password", admin.getPassword())
                    .append("name", admin.getName())
                    .append("createdAt", admin.getCreatedAt());

            adminsCollection.insertOne(adminDoc);

            ResponseUtil.sendCreated(exchange, admin.toJson());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
