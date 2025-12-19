package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.Admin;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdminLoginHandler implements HttpHandler {
    
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
            if (!requestBody.has("email") || !requestBody.has("password")) {
                ResponseUtil.sendError(exchange, 400, "Missing email or password");
                return;
            }

            String email = requestBody.get("email").getAsString();
            String password = requestBody.get("password").getAsString();

            // Find admin by email
            MongoCollection<Document> adminsCollection = 
                    DatabaseConnection.getInstance().getCollection("admins");
            
            Document adminDoc = adminsCollection.find(Filters.eq("email", email)).first();
            
            if (adminDoc == null) {
                ResponseUtil.sendError(exchange, 401, "Invalid email or password");
                return;
            }

            String storedPassword = adminDoc.getString("password");
            
            // Verify password
            if (!Admin.verifyPassword(password, storedPassword)) {
                ResponseUtil.sendError(exchange, 401, "Invalid email or password");
                return;
            }

            // Create response
            JsonObject adminData = new JsonObject();
            adminData.addProperty("id", adminDoc.getObjectId("_id").toString());
            adminData.addProperty("email", adminDoc.getString("email"));
            adminData.addProperty("name", adminDoc.getString("name"));

            ResponseUtil.sendSuccess(exchange, "Login successful", adminData);
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
