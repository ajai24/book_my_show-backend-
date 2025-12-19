package com.bookmyshow;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.bookmyshow.handler.*;
import com.bookmyshow.database.DatabaseConnection;
import com.bookmyshow.model.Admin;
import com.bookmyshow.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

public class BookMyShowApplication {
    
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            // Initialize database connection
            DatabaseConnection.getInstance();
            
            // Initialize test data
            initializeDatabase();
            
            // Create HTTP server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Register endpoints
            registerEndpoints(server);
            
            // Start server
            server.start();
            System.out.println("BookMyShow Server started on port " + PORT);
            System.out.println("Base URL: http://localhost:" + PORT);
            
            // Graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                server.stop(0);
                DatabaseConnection.getInstance().close();
            }));
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void initializeDatabase() {
        try {
            MongoCollection<Document> adminsCollection = 
                    DatabaseConnection.getInstance().getCollection("admins");
            MongoCollection<Document> usersCollection = 
                    DatabaseConnection.getInstance().getCollection("users");
            
            // Create test admin if not exists
            Document existingAdmin = adminsCollection.find(Filters.eq("email", "admin@example.com")).first();
            if (existingAdmin == null) {
                Admin admin = new Admin("admin@example.com", "Admin@123", "Admin User");
                admin.setId(new ObjectId().toString());
                
                // Hash the password
                String hashedPassword = Admin.hashPassword("Admin@123");
                
                Document adminDoc = new Document()
                        .append("_id", new ObjectId(admin.getId()))
                        .append("email", admin.getEmail())
                        .append("password", hashedPassword)
                        .append("name", admin.getName())
                        .append("createdAt", admin.getCreatedAt());
                
                adminsCollection.insertOne(adminDoc);
                System.out.println("✓ Test admin created: admin@example.com / Admin@123");
            } else {
                System.out.println("✓ Test admin already exists: admin@example.com");
            }
            
            // Create test user if not exists
            Document existingUser = usersCollection.find(Filters.eq("email", "test@example.com")).first();
            if (existingUser == null) {
                User user = new User("test@example.com", "Test@123", "Test User", "1234567890");
                user.setId(new ObjectId().toString());
                
                // Hash the password
                String hashedPassword = User.hashPassword("Test@123");
                
                Document userDoc = new Document()
                        .append("_id", new ObjectId(user.getId()))
                        .append("email", user.getEmail())
                        .append("password", hashedPassword)
                        .append("name", user.getName())
                        .append("phone", user.getPhone())
                        .append("createdAt", user.getCreatedAt());
                
                usersCollection.insertOne(userDoc);
                System.out.println("✓ Test user created: test@example.com / Test@123");
            } else {
                System.out.println("✓ Test user already exists: test@example.com");
            }
            
        } catch (Exception e) {
            System.err.println("Warning: Failed to initialize test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void registerEndpoints(HttpServer server) {
        // User endpoints
        server.createContext("/api/user/register", new UserRegisterHandler());
        server.createContext("/api/user/login", new UserLoginHandler());
        
        // Admin endpoints
        server.createContext("/api/admin/register", new AdminRegisterHandler());
        server.createContext("/api/admin/login", new AdminLoginHandler());
        
        // Theatre endpoints
        server.createContext("/api/theatres", new TheatreHandler());
        
        // Movie endpoints
        server.createContext("/api/movies", new MovieHandler());
        
        // Show endpoints
        server.createContext("/api/shows", new ShowHandler());
        
        // Booking endpoints
        server.createContext("/api/book-seats", new BookSeatsHandler());
        server.createContext("/api/bookings", new BookingHandler());
        
        // Stats endpoint
        server.createContext("/api/stats", new StatsHandler());
        
        System.out.println("Endpoints registered successfully");
    }
}
