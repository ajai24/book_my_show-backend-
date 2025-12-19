package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.Theatre;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TheatreHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseUtil.handleOptions(exchange);
            return;
        }

        String method = exchange.getRequestMethod();
        
        if ("POST".equalsIgnoreCase(method)) {
            handlePost(exchange);
        } else if ("GET".equalsIgnoreCase(method)) {
            handleGet(exchange);
        } else {
            ResponseUtil.sendError(exchange, 405, "Method not allowed");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody()));
            JsonObject requestBody = JsonUtil.readJsonBody(reader);

            // Validate required fields
            if (!requestBody.has("name") || !requestBody.has("city") || 
                !requestBody.has("address") || !requestBody.has("taxPercentage") ||
                !requestBody.has("adminId")) {
                ResponseUtil.sendError(exchange, 400, "Missing required fields");
                return;
            }

            String name = requestBody.get("name").getAsString();
            String city = requestBody.get("city").getAsString();
            String address = requestBody.get("address").getAsString();
            double taxPercentage = requestBody.get("taxPercentage").getAsDouble();
            String adminId = requestBody.get("adminId").getAsString();

            // Create theatre
            Theatre theatre = new Theatre(name, city, address, taxPercentage, adminId);
            theatre.setId(new ObjectId().toString());

            // Save to database
            MongoCollection<Document> theatresCollection = 
                    DatabaseConnection.getInstance().getCollection("theatres");
            
            Document theatreDoc = new Document()
                    .append("_id", new ObjectId(theatre.getId()))
                    .append("name", theatre.getName())
                    .append("city", theatre.getCity())
                    .append("address", theatre.getAddress())
                    .append("taxPercentage", theatre.getTaxPercentage())
                    .append("adminId", theatre.getAdminId())
                    .append("createdAt", theatre.getCreatedAt());

            theatresCollection.insertOne(theatreDoc);

            ResponseUtil.sendCreated(exchange, theatre.toJson());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            MongoCollection<Document> theatresCollection = 
                    DatabaseConnection.getInstance().getCollection("theatres");

            JsonArray theatres = new JsonArray();
            FindIterable<Document> documents;

            if (query != null && query.startsWith("city=")) {
                String city = query.substring(5);
                documents = theatresCollection.find(Filters.eq("city", city));
            } else {
                documents = theatresCollection.find();
            }

            for (Document doc : documents) {
                JsonObject theatre = new JsonObject();
                theatre.addProperty("id", doc.getObjectId("_id").toString());
                theatre.addProperty("name", doc.getString("name"));
                theatre.addProperty("city", doc.getString("city"));
                theatre.addProperty("address", doc.getString("address"));
                theatre.addProperty("taxPercentage", doc.getDouble("taxPercentage"));
                theatres.add(theatre);
            }

            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.add("data", theatres);
            ResponseUtil.sendResponse(exchange, 200, response.toString());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
