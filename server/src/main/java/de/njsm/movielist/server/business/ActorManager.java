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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.ws.rs.container.AsyncResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.movielist.server.business.data.Actor;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.ActorHandler;
import fj.data.Validation;

@Service
@RequestScope
public class ActorManager extends BusinessObject {

    private ActorHandler handler;

    public ActorManager(ActorHandler dbHandler) {
        super(dbHandler);
        this.handler = dbHandler;
    }

    public StatusCode add(Actor data) {
        return runOperation(() -> handler.add(data));
    }

    public StatusCode edit(Actor data) {
        return runOperation(() -> handler.edit(data));
    }

    public StatusCode merge(Actor data, int other) {
        return runOperation(() -> {
            if (data.getId() != other)
                return handler.merge(data, other);
            else
                return StatusCode.INVALID_ARGUMENT;
        });
    }

    public Validation<StatusCode, Map<String, Object>> getMovieCounts(AsyncResponse ar) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            return handler.getActorsWithMovieCounts()
                    .map(d -> Collections.singletonMap("items", (Iterable<MovieCount>) d::iterator));
        });
    }

    public Validation<StatusCode, Map<String, Object>> get(AsyncResponse ar, User u, int id) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return handler.get(id)
                    .bind(d -> {
                        result.put("actor", d);
                        return handler.getMoviesOfActor(u, id);
                    }).map(d -> {
                        result.put("movies", (Iterable<MovieOutline>) d::iterator);
                        return result;
                    });
        });
    }

    public Validation<StatusCode, Stream<Actor>> get(AsyncResponse ar) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            return handler.get();
        });
    }

    public Validation<StatusCode, Map<String, Object>> getEditForm(int actor) {
        return runFunction(() -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return handler.get(actor)
                    .map(d -> {
                        result.put("actor", d);
                        result.put("edit", true);
                        return result;
                    });
        });
    }

    public Validation<StatusCode, Map<String, Object>> getMergeForm(AsyncResponse ar, int actor) {
        return runAsynchronously(ar, () -> {
            handler.setReadOnly();
            Map<String, Object> result = new HashMap<>();
            return handler.get(actor)
                    .bind(d -> {
                        result.put("actor", d);
                        return handler.get();
                    }).map(d -> {
                        result.put("actors", (Iterable<Actor>) d::iterator);
                        return result;
                    });
        });
    }
}
