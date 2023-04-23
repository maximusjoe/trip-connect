package com.example.a3717project;

public class Trip {
    private User User;
    private double x_coor;
    private double y_coor;
    private String trip_code;
    private String password;
    private String hex;
    private String destination;
    private String departure;

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    private boolean completed;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Trip() {

    }

    public com.example.a3717project.User getUser() {
        return User;
    }

    public void setUser(com.example.a3717project.User user) {
        User = user;
    }


    public double getX_coor() {
        return x_coor;
    }

    public void setX_coor(double x_coor) {
        this.x_coor = x_coor;
    }

    public double getY_coor() {
        return y_coor;
    }

    public void setY_coor(double y_coor) {
        this.y_coor = y_coor;
    }

    public String getTrip_code() {
        return trip_code;
    }

    public void setTrip_code(String trip_code) {
        this.trip_code = trip_code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
