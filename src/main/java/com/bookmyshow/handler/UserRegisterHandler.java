package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.User;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserRegisterHandler implements HttpHandler {
    
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
                !requestBody.has("name") || !requestBody.has("phone")) {
                ResponseUtil.sendError(exchange, 400, "Missing required fields");
                return;
            }

            String email = requestBody.get("email").getAsString();
            String password = requestBody.get("password").getAsString();
            String name = requestBody.get("name").getAsString();
            String phone = requestBody.get("phone").getAsString();

            // Check if email already exists
            MongoCollection<Document> usersCollection = 
                    DatabaseConnection.getInstance().getCollection("users");
            
            if (usersCollection.find(Filters.eq("email", email)).first() != null) {
                ResponseUtil.sendError(exchange, 409, "Email already registered");
                return;
            }

            // Create user
            User user = new User(email, password, name, phone);
            user.setId(new ObjectId().toString());
            user.setPassword(User.hashPassword(password));

            // Save to database
            Document userDoc = new Document()
                    .append("_id", new ObjectId(user.getId()))
                    .append("email", user.getEmail())
                    .append("password", user.getPassword())
                    .append("name", user.getName())
                    .append("phone", user.getPhone())
                    .append("createdAt", user.getCreatedAt());

            usersCollection.insertOne(userDoc);

            ResponseUtil.sendCreated(exchange, user.toJson());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
