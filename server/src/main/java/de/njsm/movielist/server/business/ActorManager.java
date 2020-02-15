package de.njsm.movielist.server.business;

import de.njsm.movielist.server.business.data.Actor;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.ActorHandler;
import fj.data.Validation;

import javax.ws.rs.container.AsyncResponse;
import java.util.List;
import java.util.stream.Stream;

public class ActorManager extends BusinessObject {

    private ActorHandler handler;

    public ActorManager(ActorHandler dbHandler) {
        super(dbHandler);
        this.handler = dbHandler;
    }

    public Validation<StatusCode, List<MovieCount>> get() {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get();
        });
    }

    public Validation<StatusCode, Stream<MovieOutline>> getMovies(AsyncResponse ar, User u, int id) {
        return runFunction(ar, () -> {
            handler.setReadOnly();
            return handler.get(u, id);
        });
    }

    public Validation<StatusCode, Actor> get(int id) {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get(id);
        });
    }
}
