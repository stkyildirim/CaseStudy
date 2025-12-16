package org.flightsearch.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private boolean hasError;
    private String errorMessage;
    private List<Flight> flights = new ArrayList<>();

    public SearchResponse() {}

    public SearchResponse(boolean hasError, String errorMessage, List<Flight> flights) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.flights = flights;
    }

    // Getters & Setters
    public boolean isHasError() { return hasError; }
    public void setHasError(boolean hasError) { this.hasError = hasError; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }
}

