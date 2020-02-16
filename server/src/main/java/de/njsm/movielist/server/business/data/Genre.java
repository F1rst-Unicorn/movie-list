package de.njsm.movielist.server.business.data;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class Genre {

    @PathParam("genre")
    private int id;

    @FormParam("name")
    private String name;

    private boolean selected;

    public Genre() {
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(int id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return name;
    }
}
