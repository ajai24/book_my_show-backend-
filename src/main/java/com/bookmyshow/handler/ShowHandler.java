package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.Show;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShowHandler implements HttpHandler {
    
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
            if (!requestBody.has("movieId") || !requestBody.has("theatreId") || 
                !requestBody.has("showTime") || !requestBody.has("showDate") ||
                !requestBody.has("price") || !requestBody.has("totalSeats")) {
                ResponseUtil.sendError(exchange, 400, "Missing required fields");
                return;
            }

            String movieId = requestBody.get("movieId").getAsString();
            String theatreId = requestBody.get("theatreId").getAsString();
            String showTime = requestBody.get("showTime").getAsString();
            String showDate = requestBody.get("showDate").getAsString();
            double price = requestBody.get("price").getAsDouble();
            int totalSeats = requestBody.get("totalSeats").getAsInt();

            // Create show
            Show show = new Show(movieId, theatreId, showTime, showDate, price, totalSeats);
            show.setId(new ObjectId().toString());

            // Save to database
            MongoCollection<Document> showsCollection = 
                    DatabaseConnection.getInstance().getCollection("shows");
            
            // Convert int[] to List for MongoDB
            java.util.List<Integer> bookedSeatsList = new java.util.ArrayList<>();
            for (int seat : show.getBookedSeats()) {
                bookedSeatsList.add(seat);
            }
            
            Document showDoc = new Document()
                    .append("_id", new ObjectId(show.getId()))
                    .append("movieId", show.getMovieId())
                    .append("theatreId", show.getTheatreId())
                    .append("showTime", show.getShowTime())
                    .append("showDate", show.getShowDate())
                    .append("price", show.getPrice())
                    .append("totalSeats", show.getTotalSeats())
                    .append("bookedSeats", bookedSeatsList)
                    .append("createdAt", show.getCreatedAt());

            showsCollection.insertOne(showDoc);

            ResponseUtil.sendCreated(exchange, show.toJson());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            MongoCollection<Document> showsCollection = 
                    DatabaseConnection.getInstance().getCollection("shows");

            JsonArray shows = new JsonArray();
            FindIterable<Document> documents;

            if (query != null && query.startsWith("movieId=")) {
                String movieId = query.substring(8);
                documents = showsCollection.find(Filters.eq("movieId", movieId));
            } else if (query != null && query.startsWith("theatreId=")) {
                String theatreId = query.substring(10);
                documents = showsCollection.find(Filters.eq("theatreId", theatreId));
            } else {
                documents = showsCollection.find();
            }

            for (Document doc : documents) {
                JsonObject show = new JsonObject();
                show.addProperty("id", doc.getObjectId("_id").toString());
                show.addProperty("movieId", doc.getString("movieId"));
                show.addProperty("theatreId", doc.getString("theatreId"));
                show.addProperty("showTime", doc.getString("showTime"));
                show.addProperty("showDate", doc.getString("showDate"));
                show.addProperty("price", doc.getDouble("price"));
                show.addProperty("totalSeats", doc.getInteger("totalSeats"));
                shows.add(show);
            }

            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.add("data", shows);
            ResponseUtil.sendResponse(exchange, 200, response.toString());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
