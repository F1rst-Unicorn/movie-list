package de.njsm.movielist.server.business.data;

public class User {

    public int id;

    public String name;

    public boolean authenticated;

    public User(String name, boolean authenticated) {
        this.name = name;
        this.authenticated = authenticated;
    }

    public String getName() {
        return name;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public int getId() {
        return 1;
    }
}
