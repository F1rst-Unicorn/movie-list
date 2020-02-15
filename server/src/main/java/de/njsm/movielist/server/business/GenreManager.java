package de.njsm.movielist.server.business;

import de.njsm.movielist.server.business.data.Genre;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.GenreHandler;
import fj.data.Validation;

import javax.ws.rs.container.AsyncResponse;
import java.util.List;
import java.util.stream.Stream;

public class GenreManager extends BusinessObject {

    private GenreHandler handler;

    public GenreManager(GenreHandler dbHandler) {
        super(dbHandler);
        this.handler = dbHandler;
    }

    public Validation<StatusCode, List<Genre>> get() {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get();
        });
    }

    public Validation<StatusCode, List<MovieCount>> getCounts() {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.getCounts();
        });
    }

    public Validation<StatusCode, Genre> get(int id) {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get(id);
        });
    }

    public Validation<StatusCode, Stream<MovieOutline>> getMovies(AsyncResponse ar, User u, int id) {
        return runFunction(ar, () -> {
            handler.setReadOnly();
            return handler.get(u, id);
        });
    }
}
