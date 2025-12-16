package org.flightsearch.model;

import java.time.LocalDateTime;

public class SearchRequest {

    private String origin;
    private String destination;
    private LocalDateTime departureDate;

    public SearchRequest() {}

    public SearchRequest(String origin, String destination, LocalDateTime departureDate) {
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
    }

    // Getters & Setters
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDateTime departureDate) { this.departureDate = departureDate; }
}

