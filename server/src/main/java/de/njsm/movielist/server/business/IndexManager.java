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
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.IndexHandler;
import fj.data.Validation;
import javax.ws.rs.container.AsyncResponse;

import java.util.Collections;
import java.util.Map;

public class IndexManager extends BusinessObject {

    private IndexHandler dbBackend;

    public IndexManager(IndexHandler dbBackend) {
        super(dbBackend);
        this.dbBackend = dbBackend;
    }

    public Validation<StatusCode, Map<String, Object>> get(AsyncResponse as, User u) {
        return runAsynchronously(as, () -> {
            dbBackend.setReadOnly();
            return dbBackend.get(u, false, false).map(
                    stream -> Collections.singletonMap("movies", (Iterable<MovieOutline>) stream::iterator));
        });
    }

    public Validation<StatusCode, Map<String, Object>> getToDelete(AsyncResponse as, User u) {
        return runAsynchronously(as, () -> {
            dbBackend.setReadOnly();
            return dbBackend.get(u, false, true).map(
                    stream -> Collections.singletonMap("movies", (Iterable<MovieOutline>) stream::iterator));
        });
    }

    public Validation<StatusCode, Map<String, Object>> getDeleted(AsyncResponse as, User u) {
        return runAsynchronously(as, () -> {
            dbBackend.setReadOnly();
            return dbBackend.get(u, true, false).map(
                    stream -> Collections.singletonMap("movies", (Iterable<MovieOutline>) stream::iterator));
        });
    }

    public Validation<StatusCode, Map<String, Object>> getLatest(AsyncResponse as, User u) {
        return runAsynchronously(as, () -> {
            dbBackend.setReadOnly();
            return dbBackend.getLatest(u).map(
                    stream -> Collections.singletonMap("movies", (Iterable<MovieOutline>) stream::iterator));
        });
    }

    public StatusCode addLocation(Location data) {
        return runOperation(() -> dbBackend.addLocation(data));
    }
}
