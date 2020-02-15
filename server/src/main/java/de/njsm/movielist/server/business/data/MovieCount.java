package de.njsm.movielist.server.business.data;

public class MovieCount {

    private long id;

    private String name;

    private int count;

    public MovieCount(long id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
