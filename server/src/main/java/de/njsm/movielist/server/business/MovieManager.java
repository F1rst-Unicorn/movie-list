package de.njsm.movielist.server.business;

import de.njsm.movielist.server.business.data.MovieDetails;
import de.njsm.movielist.server.db.MovieHandler;
import fj.data.Validation;

public class MovieManager extends BusinessObject {

    private MovieHandler handler;

    public MovieManager(MovieHandler dbHandler) {
        super(dbHandler);
        handler = dbHandler;
    }

    public Validation<StatusCode, MovieDetails> get(int id) {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get(id);
        });
    }
}
