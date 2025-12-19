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
import com.bookmyshow.util.ResponseUtil;

import java.io.IOException;

public class BookingHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseUtil.handleOptions(exchange);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseUtil.sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            String query = exchange.getRequestURI().getQuery();
            
            if (query == null || !query.startsWith("userId=")) {
                ResponseUtil.sendError(exchange, 400, "Missing userId parameter");
                return;
            }

            String userId = query.substring(7);
            
            MongoCollection<Document> bookingsCollection = 
                    DatabaseConnection.getInstance().getCollection("bookings");
            
            FindIterable<Document> documents = bookingsCollection.find(
                    Filters.eq("userId", userId));

            JsonArray bookings = new JsonArray();
            
            for (Document doc : documents) {
                JsonObject booking = new JsonObject();
                booking.addProperty("id", doc.getObjectId("_id").toString());
                booking.addProperty("userId", doc.getString("userId"));
                booking.addProperty("showId", doc.getString("showId"));
                booking.addProperty("theatreId", doc.getString("theatreId"));
                booking.addProperty("movieId", doc.getString("movieId"));
                booking.addProperty("totalPrice", doc.getDouble("totalPrice"));
                booking.addProperty("status", doc.getString("status"));
                booking.addProperty("bookingTime", doc.getLong("bookingTime"));
                bookings.add(booking);
            }

            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.add("data", bookings);
            ResponseUtil.sendResponse(exchange, 200, response.toString());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
