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

import de.njsm.movielist.server.business.data.Genre;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.GenreHandler;
import fj.data.Validation;

import jakarta.ws.rs.container.AsyncResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public Validation<StatusCode, Map<String, Object>> getGenreForEditing(int genre) {
        return runFunction(() -> {
            Map<String, Object> result = new HashMap<>();
            handler.setReadOnly();
            return handler.get(genre)
                    .map(d -> {
                        result.put("edit", true);
                        result.put("genre", d);
                        return result;
                    });
        });
    }

    public Validation<StatusCode, Map<String, Object>> getCounts(AsyncResponse ar) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            return handler.getCounts()
                    .map(d -> Collections.singletonMap("items", (Iterable<MovieCount>) d::iterator));
        });
    }

    public Validation<StatusCode, Map<String, Object>> get(AsyncResponse ar, int id, User u) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return handler.get(id)
                    .bind(d -> {
                        result.put("genre", d);
                        return handler.getMoviesInGenre(u, id);
                    }).map(d -> {
                        result.put("movies", (Iterable<MovieOutline>) d::iterator);
                        return result;
                    });
        });
    }
}
