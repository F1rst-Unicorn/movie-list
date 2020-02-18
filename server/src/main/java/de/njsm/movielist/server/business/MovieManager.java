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

import de.njsm.movielist.server.business.data.Location;
import de.njsm.movielist.server.business.data.MovieDetails;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.MovieHandler;
import fj.data.Validation;

import java.util.List;

public class MovieManager extends BusinessObject {

    private MovieHandler handler;

    public MovieManager(MovieHandler dbHandler) {
        super(dbHandler);
        handler = dbHandler;
    }

    public Validation<StatusCode, Integer> add(MovieDetails movie, List<Integer> actors, List<Integer> genres) {
        return runFunction(() -> handler.add(movie, actors, genres));
    }

    public StatusCode edit(MovieDetails movie, List<Integer> actors, List<Integer> genres) {
        return runOperation(() -> handler.edit(movie, actors, genres));
    }

    public Validation<StatusCode, MovieDetails> get(int id) {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get(id);
        });
    }

    public void markWatched(int id, int user) {
        runOperation(() -> {
            handler.markWatched(id, user);
            return StatusCode.SUCCESS;
        });
    }

    public void markToRemove(int id) {
        runOperation(() -> {
            handler.markToRemove(id);
            return StatusCode.SUCCESS;
        });
    }

    public void delete(int id) {
        runOperation(() -> {
            handler.delete(id);
            return StatusCode.SUCCESS;
        });
    }

    public void addComment(int id, String comment, User user) {
        runOperation(() -> {
            handler.addComment(id, comment, user);
            return StatusCode.SUCCESS;
        });
    }

    public void deleteComment(int comment) {
        runOperation(() -> {
            handler.deleteComment(comment);
            return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, List<Location>> getLocations() {
        return runFunction(() -> handler.getLocations());
    }

    public Validation<StatusCode, List<Location>> getLocationsWithMovie(int id) {
        return runFunction(() -> handler.getLocations(id));
    }
}
