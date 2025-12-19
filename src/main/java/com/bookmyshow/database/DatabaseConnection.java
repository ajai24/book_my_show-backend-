package com.bookmyshow.database;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private DatabaseConnection() {
        // MongoDB Atlas Connection String
        String connectionString = "mongodb+srv://ajaikannan:ajai24@cluster0.ekqtlsx.mongodb.net/bookmyshow?retryWrites=true&w=majority";
        
        System.out.println("Attempting to connect to MongoDB: " + connectionString);
        
        try {
            this.mongoClient = MongoClients.create(connectionString);
            this.database = mongoClient.getDatabase("bookmyshow");
            initializeCollections();
            System.out.println("✅ Connected to MongoDB successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to MongoDB: " + e.getMessage());
            System.err.println("Make sure your MongoDB Atlas cluster is accessible");
            System.err.println("Check your network connection and Atlas whitelist settings");
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void initializeCollections() {
        // Create collections if they don't exist
        try {
            if (!collectionExists("users")) {
                database.createCollection("users");
            }
            if (!collectionExists("admins")) {
                database.createCollection("admins");
            }
            if (!collectionExists("theatres")) {
                database.createCollection("theatres");
            }
            if (!collectionExists("movies")) {
                database.createCollection("movies");
            }
            if (!collectionExists("shows")) {
                database.createCollection("shows");
            }
            if (!collectionExists("bookings")) {
                database.createCollection("bookings");
            }
            System.out.println("Collections initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing collections: " + e.getMessage());
        }
    }

    private boolean collectionExists(String collectionName) {
        for (String name : database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed");
        }
    }
}
