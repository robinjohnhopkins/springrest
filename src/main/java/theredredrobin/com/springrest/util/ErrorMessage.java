package theredredrobin.com.springrest.util;

public class ErrorMessage {

    private String status;

    private String message ;

    public String getStatus() {
        return status;
    }

    public ErrorMessage(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
