package de.njsm.movielist.server.business.data;

public class Comment {

    private int id;

    private String content;

    private User creator;

    public Comment(int id, String content, User creator) {
        this.id = id;
        this.content = content;
        this.creator = creator;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getCreator() {
        return creator;
    }
}
