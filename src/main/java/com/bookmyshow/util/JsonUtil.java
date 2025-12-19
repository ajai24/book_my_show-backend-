package com.bookmyshow.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static JsonObject readJsonBody(BufferedReader reader) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        if (body.length() == 0) {
            return new JsonObject();
        }
        return gson.fromJson(body.toString(), JsonObject.class);
    }

    public static String toJsonString(Object obj) {
        return gson.toJson(obj);
    }

    public static JsonObject toJsonObject(Object obj) {
        String json = gson.toJson(obj);
        return gson.fromJson(json, JsonObject.class);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String prettyPrint(JsonObject json) {
        return gson.toJson(json);
    }
}
