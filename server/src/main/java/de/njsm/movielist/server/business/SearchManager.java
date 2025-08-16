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

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.container.AsyncResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.SearchQuery;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.GenreHandler;
import de.njsm.movielist.server.db.SearchHandler;
import de.njsm.movielist.server.db.UserHandler;
import fj.data.Validation;

@Service
@RequestScope
public class SearchManager extends BusinessObject {

    private SearchHandler handler;

    private UserHandler userHandler;

    private GenreHandler genreHandler;

    public SearchManager(SearchHandler dbHandler, UserHandler userHandler, GenreHandler genreHandler) {
        super(dbHandler);
        handler = dbHandler;
        this.userHandler = userHandler;
        this.genreHandler = genreHandler;
    }

    public Validation<StatusCode, Map<String, Object>> getFormContent(AsyncResponse ar) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return userHandler.get()
                    .bind(d -> {
                        result.put("users", d);
                        return genreHandler.getCounts();
                    }).map(d -> {
                        result.put("genres", (Iterable<MovieCount>) d::iterator);
                        return result;
                    });
        });
    }

    public Validation<StatusCode, Map<String, Object>> search(AsyncResponse ar, User u, SearchQuery query) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            result.put("query", query);

            return userHandler.get()
                    .bind(d -> {
                        result.put("users", d);
                        return handler.get(u, query);
                    }).bind(d -> {
                        result.put("movies", (Iterable<MovieOutline>) d::iterator);
                        return genreHandler.getCounts();
                    }).map(d -> {
                        result.put("genres", (Iterable<MovieCount>) d::iterator);
                        return result;
                    });
        });
    }
}
