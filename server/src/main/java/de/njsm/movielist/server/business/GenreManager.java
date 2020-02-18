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

    public StatusCode add(Genre data) {
        return runOperation(() -> handler.add(data));
    }

    public StatusCode edit(Genre data) {
        return runOperation(() -> handler.edit(data));
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

    public Validation<StatusCode, List<Genre>> getWithMovie(int movieId) {
        return runFunction(() -> handler.getWithMovie(movieId));
    }
}
