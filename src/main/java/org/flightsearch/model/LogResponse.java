package org.flightsearch.model;

import java.util.ArrayList;
import java.util.List;

public class LogResponse {
    private boolean hasError = false;
    private String errorMessage;
    private List<RequestResponseLogDTO> logs = new ArrayList<>();

    public LogResponse() {
    }

    public LogResponse(boolean hasError, String errorMessage, List<RequestResponseLogDTO> logs) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.logs = logs;
    }

    public LogResponse(boolean hasError, String errorMessage) {
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.logs = new ArrayList<>();
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<RequestResponseLogDTO> getLogs() {
        return logs;
    }

    public void setLogs(List<RequestResponseLogDTO> logs) {
        this.logs = logs;
    }
}

