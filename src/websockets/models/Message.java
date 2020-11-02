package websockets.models;

public class Message {
    private String from = null;
    private String to = null;
    private String content = null;
    
    @Override
    public String toString() {
        return "[ from="+this.from+", to="+this.to+", content="+this.content+"]"; 
    }
    
    public Message() {}
    
    public Message(String from, String content) {
        this.from = from;
        this.content = content;
    }
    
    public Message(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}