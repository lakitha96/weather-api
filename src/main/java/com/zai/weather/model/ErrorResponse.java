package com.zai.weather.model;

/**
 * @author lakithaprabudh
 */
public class ErrorResponse {
    private String error;
    private String message;
    private String path;
    private int status;
    private long timestamp;

    public ErrorResponse(String error, String message, String path, int status) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
