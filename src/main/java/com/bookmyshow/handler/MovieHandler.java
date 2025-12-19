package com.bookmyshow.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.Movie;
import com.bookmyshow.util.JsonUtil;
import com.bookmyshow.util.ResponseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MovieHandler implements HttpHandler {
    
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
            if (!requestBody.has("title") || !requestBody.has("genre") || 
                !requestBody.has("language") || !requestBody.has("duration")) {
                ResponseUtil.sendError(exchange, 400, "Missing required fields");
                return;
            }

            String title = requestBody.get("title").getAsString();
            String genre = requestBody.get("genre").getAsString();
            String language = requestBody.get("language").getAsString();
            int duration = requestBody.get("duration").getAsInt();
            String releaseDate = requestBody.has("releaseDate") ? 
                    requestBody.get("releaseDate").getAsString() : "";
            String posterUrl = requestBody.has("posterUrl") ? 
                    requestBody.get("posterUrl").getAsString() : "";
            String description = requestBody.has("description") ? 
                    requestBody.get("description").getAsString() : "";
            double imdbRating = requestBody.has("imdbRating") ? 
                    requestBody.get("imdbRating").getAsDouble() : 0.0;

            // Create movie
            Movie movie = new Movie(title, genre, language, duration, 
                    releaseDate, posterUrl, description, imdbRating);
            movie.setId(new ObjectId().toString());

            // Save to database
            MongoCollection<Document> moviesCollection = 
                    DatabaseConnection.getInstance().getCollection("movies");
            
            Document movieDoc = new Document()
                    .append("_id", new ObjectId(movie.getId()))
                    .append("title", movie.getTitle())
                    .append("genre", movie.getGenre())
                    .append("language", movie.getLanguage())
                    .append("duration", movie.getDuration())
                    .append("releaseDate", movie.getReleaseDate())
                    .append("posterUrl", movie.getPosterUrl())
                    .append("description", movie.getDescription())
                    .append("imdbRating", movie.getImdbRating())
                    .append("createdAt", movie.getCreatedAt());

            moviesCollection.insertOne(movieDoc);

            ResponseUtil.sendCreated(exchange, movie.toJson());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            MongoCollection<Document> moviesCollection = 
                    DatabaseConnection.getInstance().getCollection("movies");

            JsonArray movies = new JsonArray();
            FindIterable<Document> documents = moviesCollection.find();

            for (Document doc : documents) {
                JsonObject movie = new JsonObject();
                movie.addProperty("id", doc.getObjectId("_id").toString());
                movie.addProperty("title", doc.getString("title"));
                movie.addProperty("genre", doc.getString("genre"));
                movie.addProperty("language", doc.getString("language"));
                movie.addProperty("duration", doc.getInteger("duration"));
                movie.addProperty("releaseDate", doc.getString("releaseDate"));
                movie.addProperty("imdbRating", doc.getDouble("imdbRating"));
                movies.add(movie);
            }

            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.add("data", movies);
            ResponseUtil.sendResponse(exchange, 200, response.toString());
        } catch (Exception e) {
            ResponseUtil.sendError(exchange, 500, "Internal server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
