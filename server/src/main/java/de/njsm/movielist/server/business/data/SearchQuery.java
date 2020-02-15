package de.njsm.movielist.server.business.data;

import javax.ws.rs.FormParam;
import java.util.List;

public class SearchQuery {

    @FormParam("text")
    private String term;

    @FormParam("choices")
    private List<Integer> scopes;

    @FormParam("deleted_movies")
    private boolean includeMissing;

    @FormParam("genres")
    private List<Integer> genres;

    @FormParam("unwatched_by")
    private List<Integer> users;

    public String getTerm() {
        return term;
    }

    public List<Integer> getScopes() {
        return scopes;
    }

    public boolean isIncludeMissing() {
        return includeMissing;
    }

    public List<Integer> getGenres() {
        return genres;
    }

    public List<Integer> getUsers() {
        return users;
    }
}
