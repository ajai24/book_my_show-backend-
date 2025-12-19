package com.bookmyshow.util;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {

    public static void sendResponse(HttpExchange exchange, int statusCode, String responseBody) 
            throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    public static void sendSuccess(HttpExchange exchange, JsonObject data) throws IOException {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);
        sendResponse(exchange, 200, response.toString());
    }

    public static void sendSuccess(HttpExchange exchange, String message, JsonObject data) throws IOException {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.addProperty("message", message);
        response.add("data", data);
        sendResponse(exchange, 200, response.toString());
    }

    public static void sendError(HttpExchange exchange, int statusCode, String errorMessage) 
            throws IOException {
        JsonObject response = new JsonObject();
        response.addProperty("success", false);
        response.addProperty("error", errorMessage);
        sendResponse(exchange, statusCode, response.toString());
    }

    public static void sendCreated(HttpExchange exchange, JsonObject data) throws IOException {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", data);
        sendResponse(exchange, 201, response.toString());
    }

    public static void handleOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }
}
