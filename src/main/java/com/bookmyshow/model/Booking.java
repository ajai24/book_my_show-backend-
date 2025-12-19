package com.bookmyshow.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class Booking {
    private String id;
    private String userId;
    private String showId;
    private String theatreId;
    private String movieId;
    private int[] bookedSeats;
    private double ticketPrice;
    private double tax;
    private double totalPrice;
    private String paymentMode;
    private String status;
    private long bookingTime;

    public Booking(String userId, String showId, String theatreId, String movieId,
                   int[] bookedSeats, double ticketPrice, double tax, String paymentMode) {
        this.userId = userId;
        this.showId = showId;
        this.theatreId = theatreId;
        this.movieId = movieId;
        this.bookedSeats = bookedSeats;
        this.ticketPrice = ticketPrice;
        this.tax = tax;
        this.totalPrice = (ticketPrice * bookedSeats.length) + tax;
        this.paymentMode = paymentMode;
        this.status = "CONFIRMED";
        this.bookingTime = System.currentTimeMillis();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("userId", userId);
        json.addProperty("showId", showId);
        json.addProperty("theatreId", theatreId);
        json.addProperty("movieId", movieId);
        
        JsonArray seatsArray = new JsonArray();
        for (int seat : bookedSeats) {
            seatsArray.add(seat);
        }
        json.add("bookedSeats", seatsArray);
        
        json.addProperty("ticketPrice", ticketPrice);
        json.addProperty("tax", tax);
        json.addProperty("totalPrice", totalPrice);
        json.addProperty("paymentMode", paymentMode);
        json.addProperty("status", status);
        json.addProperty("bookingTime", bookingTime);
        return json;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getShowId() { return showId; }
    public void setShowId(String showId) { this.showId = showId; }

    public String getTheatreId() { return theatreId; }
    public void setTheatreId(String theatreId) { this.theatreId = theatreId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public int[] getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int[] bookedSeats) { this.bookedSeats = bookedSeats; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getBookingTime() { return bookingTime; }
    public void setBookingTime(long bookingTime) { this.bookingTime = bookingTime; }
}
