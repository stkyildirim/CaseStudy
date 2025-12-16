package org.flightsearch.model;
import java.time.LocalDateTime;

public class RequestResponseLogDTO {

    private Long id;
    private String provider;
    private String request;
    private String response;
    private LocalDateTime createdAt;

    public RequestResponseLogDTO() {}

    public RequestResponseLogDTO(Long id, String provider, String request, String response, LocalDateTime createdAt) {
        this.id = id;
        this.provider = provider;
        this.request = request;
        this.response = response;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
