/* movielist is client-server program to manage a household's food stock
 * Copyright (C) 2019  The movielist developers
 *
 * This file is part of the movielist program suite.
 *
 * movielist is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * movielist is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.movielist.server.business;

import de.njsm.movielist.server.business.data.*;
import de.njsm.movielist.server.db.ActorHandler;
import de.njsm.movielist.server.db.GenreHandler;
import de.njsm.movielist.server.db.MovieHandler;
import de.njsm.movielist.server.db.UserHandler;
import fj.data.Validation;

import jakarta.ws.rs.container.AsyncResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieManager extends BusinessObject {

    private MovieHandler handler;

    private ActorHandler actorHandler;

    private GenreHandler genreHandler;

    private UserHandler userHandler;

    public MovieManager(MovieHandler dbHandler, ActorHandler actorHandler, GenreHandler genreHandler, UserHandler userHandler) {
        super(dbHandler);
        handler = dbHandler;
        this.actorHandler = actorHandler;
        this.genreHandler = genreHandler;
        this.userHandler = userHandler;
    }

    public Validation<StatusCode, Integer> add(MovieDetails movie, List<Integer> actors, List<Integer> genres) {
        return runFunction(() -> handler.add(movie, actors, genres));
    }

    public Validation<StatusCode, Map<String, Object>> getContextForMovieForm(AsyncResponse ar) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return actorHandler.get()
                    .bind(d -> {
                        result.put("actors", (Iterable<Actor>) d::iterator);
                        return genreHandler.get();
                    }).bind(d -> {
                        result.put("genres", (Iterable<Genre>) d::iterator);
                        return handler.getLocations();
                    }).map(d -> {
                        result.put("locations", d);
                        return result;
                    });
        });
    }

    public StatusCode edit(MovieDetails movie, List<Integer> actors, List<Integer> genres) {
        return runOperation(() -> handler.edit(movie, actors, genres));
    }

    public Validation<StatusCode, Map<String, Object>> getDetails(int id) {
        return runFunction(() -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return handler.get(id)
                    .bind(d -> {
                        result.put("movie", d);
                        return userHandler.get();
                    }).map(d -> {
                        result.put("users", d);
                        return result;
                    });
        });
    }

    public Validation<StatusCode, Map<String, Object>> getEditFormContext(AsyncResponse ar, int movie) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return handler.get(movie)
                    .bind(d -> {
                        result.put("movie", d);
                        return actorHandler.getActorsParticipatingIn(movie);
                    }).bind(d -> {
                        result.put("actors", d);
                        return genreHandler.getWithMovie(movie);
                    }).bind(d -> {
                        result.put("genres", (Iterable<Genre>) d::iterator);
                        return handler.getLocations(movie);
                    }).map(d -> {
                        result.put("locations", d);
                        result.put("edit", true);
                        return result;
                    });
        });
    }

    public StatusCode markWatched(int id, int user) {
        return runOperation(() -> {
            handler.markWatched(id, user);
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode markToRemove(int id) {
        return runOperation(() -> {
            handler.markToRemove(id);
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode delete(int id) {
        return runOperation(() -> {
            handler.delete(id);
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode addComment(int id, String comment, User user) {
        return runOperation(() -> {
            handler.addComment(id, comment, user);
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode deleteComment(int comment) {
        return runOperation(() -> {
            handler.deleteComment(comment);
            return StatusCode.SUCCESS;
        });
    }
}
