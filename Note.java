package server;
import java.util.UUID;

public class Note {
    private String id;
    private String content;

    public Note(String content) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Note ID: " + id + ", Content: " + content;
    }
}

