package com.bookmyshow.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class Show {
    private String id;
    private String movieId;
    private String theatreId;
    private String showTime;
    private String showDate;
    private double price;
    private int totalSeats;
    private int[] bookedSeats;
    private long createdAt;

    public Show(String movieId, String theatreId, String showTime, String showDate, 
                double price, int totalSeats) {
        this.movieId = movieId;
        this.theatreId = theatreId;
        this.showTime = showTime;
        this.showDate = showDate;
        this.price = price;
        this.totalSeats = totalSeats;
        this.bookedSeats = new int[0];
        this.createdAt = System.currentTimeMillis();
    }

    public int getAvailableSeats() {
        return totalSeats - bookedSeats.length;
    }

    public boolean isSeatBooked(int seatNumber) {
        for (int seat : bookedSeats) {
            if (seat == seatNumber) return true;
        }
        return false;
    }

    public void bookSeat(int seatNumber) {
        if (!isSeatBooked(seatNumber)) {
            int[] newBookedSeats = new int[bookedSeats.length + 1];
            System.arraycopy(bookedSeats, 0, newBookedSeats, 0, bookedSeats.length);
            newBookedSeats[bookedSeats.length] = seatNumber;
            this.bookedSeats = newBookedSeats;
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("movieId", movieId);
        json.addProperty("theatreId", theatreId);
        json.addProperty("showTime", showTime);
        json.addProperty("showDate", showDate);
        json.addProperty("price", price);
        json.addProperty("totalSeats", totalSeats);
        json.addProperty("availableSeats", getAvailableSeats());
        
        JsonArray seatsArray = new JsonArray();
        for (int seat : bookedSeats) {
            seatsArray.add(seat);
        }
        json.add("bookedSeats", seatsArray);
        json.addProperty("createdAt", createdAt);
        return json;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTheatreId() { return theatreId; }
    public void setTheatreId(String theatreId) { this.theatreId = theatreId; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int[] getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int[] bookedSeats) { this.bookedSeats = bookedSeats; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
