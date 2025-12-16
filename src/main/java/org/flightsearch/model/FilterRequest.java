package org.flightsearch.model;

import java.util.List;

public class FilterRequest {
    private List<String> providers;
    private String start;
    private String end;

    public List<String> getProviders() { return providers; }
    public void setProviders(List<String> providers) { this.providers = providers; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}