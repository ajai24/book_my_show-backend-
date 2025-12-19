package com.bookmyshow.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class Movie {
    private String id;
    private String title;
    private String genre;
    private String language;
    private int duration;
    private String releaseDate;
    private String posterUrl;
    private String description;
    private double imdbRating;
    private long createdAt;

    public Movie(String title, String genre, String language, int duration, 
                 String releaseDate, String posterUrl, String description, double imdbRating) {
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.description = description;
        this.imdbRating = imdbRating;
        this.createdAt = System.currentTimeMillis();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("title", title);
        json.addProperty("genre", genre);
        json.addProperty("language", language);
        json.addProperty("duration", duration);
        json.addProperty("releaseDate", releaseDate);
        json.addProperty("posterUrl", posterUrl);
        json.addProperty("description", description);
        json.addProperty("imdbRating", imdbRating);
        json.addProperty("createdAt", createdAt);
        return json;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getImdbRating() { return imdbRating; }
    public void setImdbRating(double imdbRating) { this.imdbRating = imdbRating; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
