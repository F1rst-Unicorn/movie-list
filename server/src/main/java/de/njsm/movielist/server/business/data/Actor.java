package de.njsm.movielist.server.business.data;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class Actor {

    @PathParam("actor")
    private int id;

    @FormParam("firstname")
    private String firstName;

    @FormParam("lastname")
    private String lastName;

    public Actor() {}

    public Actor(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
