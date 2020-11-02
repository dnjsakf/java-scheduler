package websockets.models;

public class ErrorMessage extends Message {
    
    private String code = null;
    private String error = null;
    
    public ErrorMessage() {}
    public ErrorMessage(String error) {
        this.error = error;
    }
    public ErrorMessage(String code, String error) {
        this.code = code;
        this.error = error;
    }
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
