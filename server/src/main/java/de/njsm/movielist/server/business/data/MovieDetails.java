package de.njsm.movielist.server.business.data;

import de.njsm.movielist.server.Config;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.TimeZone;

public class MovieDetails {

    @PathParam("movie")
    private int id;

    @FormParam("name")
    private String name;

    private boolean deleted;

    private boolean toDelete;

    @FormParam("year")
    private int year;

    private OffsetDateTime createdAt;

    @FormParam("link")
    private String link;

    @FormParam("description")
    private String description;

    @FormParam("location")
    private int location;

    private List<Genre> genres;

    private List<Actor> actors;

    private List<WatchStatus> watchedBy;

    private List<Comment> comments;

    public MovieDetails() {}

    public MovieDetails(int id, String name, boolean deleted, boolean toDelete, int year, OffsetDateTime createdAt, String link, String description) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
        this.toDelete = toDelete;
        this.year = year;
        this.createdAt = createdAt;
        this.link = link;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public int getYear() {
        return year;
    }

    public String getCreatedAt() {
        return createdAt.format(Config.USER_FORMAT.withZone(TimeZone.getDefault().toZoneId()));
    }

    public String getLink() {
        return link;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getDescription() {
        return description;
    }

    public List<WatchStatus> getWatchedBy() {
        return watchedBy;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public void setWatchedBy(List<WatchStatus> watchedBy) {
        this.watchedBy = watchedBy;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getLocation() {
        return location;
    }
}
