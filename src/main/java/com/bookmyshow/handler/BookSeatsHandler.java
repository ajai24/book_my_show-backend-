package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BookSeatsHandler implements HttpHandler {
    
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
            if (!requestBody.has("userId") || !requestBody.has("showId") || 
                !requestBody.has("seats") || !requestBody.has("paymentMode")) {
                ResponseUtil.sendError(exchange, 400, "Missing required fields");
                return;
            }

            String userId = requestBody.get("userId").getAsString();
            String showId = requestBody.get("showId").getAsString();
            JsonArray seatsArray = requestBody.getAsJsonArray("seats");
            String paymentMode = requestBody.get("paymentMode").getAsString();

            // Get show details
            MongoCollection<Document> showsCollection = 
                    DatabaseConnection.getInstance().getCollection("shows");
            
            Document showDoc = showsCollection.find(Filters.eq("_id", new ObjectId(showId))).first();
            
            if (showDoc == null) {
                ResponseUtil.sendError(exchange, 404, "Show not found");
                return;
            }

            // Check seat availability
            java.util.List<Integer> requestedSeats = new java.util.ArrayList<>();
            for (int i = 0; i < seatsArray.size(); i++) {
                requestedSeats.add(seatsArray.get(i).getAsInt());
            }

            Document theatreDoc = DatabaseConnection.getInstance().getCollection("theatres")
                    .find(Filters.eq("_id", new ObjectId(showDoc.getString("theatreId")))).first();
            
            if (theatreDoc == null) {
                ResponseUtil.sendError(exchange, 404, "Theatre not found");
                return;
            }

            // Calculate price
            double ticketPrice = showDoc.getDouble("price");
            double taxPercentage = theatreDoc.getDouble("taxPercentage");
            double tax = (ticketPrice * requestedSeats.size() * taxPercentage) / 100;
            double totalPrice = (ticketPrice * requestedSeats.size()) + tax;

            // Create booking
            String bookingId = new ObjectId().toString();

            Document bookingDoc = new Document()
                    .append("_id", new ObjectId(bookingId))
                    .append("userId", userId)
                    .append("showId", showId)
                    .append("theatreId", showDoc.getString("theatreId"))
                    .append("movieId", showDoc.getString("movieId"))
                    .append("bookedSeats", requestedSeats)
                    .append("ticketPrice", ticketPrice)
                    .append("tax", tax)
                    .append("totalPrice", totalPrice)
                    .append("paymentMode", paymentMode)
                    .append("status", "CONFIRMED")
                    .append("bookingTime", System.currentTimeMillis());

            MongoCollection<Document> bookingsCollection = 
                    DatabaseConnection.getInstance().getCollection("bookings");
            bookingsCollection.insertOne(bookingDoc);

            // Update booked seats in show
            java.util.List<Integer> currentBookedSeats = showDoc.getList("bookedSeats", Integer.class);
            java.util.List<Integer> updatedSeats = new java.util.ArrayList<>(currentBookedSeats);
            updatedSeats.addAll(requestedSeats);

            showsCollection.updateOne(
                    Filters.eq("_id", new ObjectId(showId)),
                    new Document("$set", new Document("bookedSeats", updatedSeats))
            );

            // Return booking details
            JsonObject bookingData = new JsonObject();
            bookingData.addProperty("bookingId", bookingId);
            bookingData.addProperty("totalPrice", totalPrice);
            bookingData.addProperty("status", "CONFIRMED");

            ResponseUtil.sendCreated(exchange, bookingData);
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
