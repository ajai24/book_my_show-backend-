package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.util.ResponseUtil;

import java.io.IOException;

public class StatsHandler implements HttpHandler {
    
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
            MongoCollection<Document> usersCollection = 
                    DatabaseConnection.getInstance().getCollection("users");
            MongoCollection<Document> theatresCollection = 
                    DatabaseConnection.getInstance().getCollection("theatres");
            MongoCollection<Document> moviesCollection = 
                    DatabaseConnection.getInstance().getCollection("movies");
            MongoCollection<Document> showsCollection = 
                    DatabaseConnection.getInstance().getCollection("shows");
            MongoCollection<Document> bookingsCollection = 
                    DatabaseConnection.getInstance().getCollection("bookings");

            long totalUsers = usersCollection.countDocuments();
            long totalTheatres = theatresCollection.countDocuments();
            long totalMovies = moviesCollection.countDocuments();
            long totalShows = showsCollection.countDocuments();
            long totalBookings = bookingsCollection.countDocuments();

            JsonObject stats = new JsonObject();
            stats.addProperty("totalUsers", totalUsers);
            stats.addProperty("totalTheatres", totalTheatres);
            stats.addProperty("totalMovies", totalMovies);
            stats.addProperty("totalShows", totalShows);
            stats.addProperty("totalBookings", totalBookings);

            ResponseUtil.sendSuccess(exchange, stats);
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
