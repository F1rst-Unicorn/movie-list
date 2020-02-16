package de.njsm.movielist.server.business.data;

import javax.ws.rs.FormParam;

public class Location {

    private int id;

    @FormParam("name")
    private String name;

    @FormParam("index")
    private int index;

    private boolean selected;

    public Location() {
    }

    public Location(int id, String name, int index) {
        this.id = id;
        this.name = name;
        this.index = index;
    }

    public Location(int id, String name, int index, boolean selected) {
        this.id = id;
        this.name = name;
        this.index = index;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        if (index == 0)
            return name;
        else
            return name + " " + index;
    }
}
