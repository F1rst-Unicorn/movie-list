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

package de.njsm.movielist.server.db;

import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_ACTOR;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_LOCATION;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_MOVIE;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_MOVIE_ACTORS;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_WATCHSTATUS;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jooq.Record7;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.Location;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import fj.data.Validation;

@Repository
@RequestScope
public class IndexHandler extends FailSafeDatabaseHandler {

    public IndexHandler(ConnectionFactory connectionFactory, @Qualifier("circuitBreakerDatabase") String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public StatusCode addLocation(Location data) {
        return runCommand(context -> {
            context.insertInto(MOVIES_LOCATION)
                    .columns(MOVIES_LOCATION.NAME, MOVIES_LOCATION.INDEX)
                    .values(data.getName(), data.getIndex())
                    .execute();
            return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, Stream<MovieOutline>> getLatest(User user) {
        return getInternally(user, false, false, MOVIES_MOVIE.CREATED_AT.desc());
    }

    public Validation<StatusCode, Stream<MovieOutline>> get(User user, boolean deleted, boolean toDelete) {
        return getInternally(user, deleted, toDelete, MOVIES_MOVIE.NAME.asc());
    }

    private Validation<StatusCode, Stream<MovieOutline>> getInternally(User user, boolean deleted, boolean toDelete, SortField<?> orderByField) {
        return runFunction(context -> {
            Stream<Record7<Integer, String, String, Boolean, Boolean, Boolean, String>> result = context.select(
                            MOVIES_MOVIE.ID,
                            MOVIES_MOVIE.NAME,
                            DSL.case_().when(MOVIES_LOCATION.INDEX.eq(0), MOVIES_LOCATION.NAME)
                                    .otherwise(DSL.concat(MOVIES_LOCATION.NAME, DSL.concat(" ", MOVIES_LOCATION.INDEX.cast(String.class)))),
                            DSL.case_().when(MOVIES_MOVIE.ID.in(
                                    context.select(MOVIES_WATCHSTATUS.MOVIE_ID)
                                            .from(MOVIES_WATCHSTATUS)
                                            .where(MOVIES_WATCHSTATUS.USER_ID.eq(user.getId()))),
                                    DSL.inline(true))
                                    .otherwise(false).as("watched_by_user"),
                            MOVIES_MOVIE.DELETED,
                            MOVIES_MOVIE.TO_DELETE,
                            DSL.concat(MOVIES_ACTOR.FIRST_NAME, DSL.concat(" ", MOVIES_ACTOR.LAST_NAME))
                    )
                            .from(MOVIES_MOVIE)
                            .join(MOVIES_LOCATION).on(MOVIES_MOVIE.LOCATION_ID.eq(MOVIES_LOCATION.ID))
                            .leftOuterJoin(MOVIES_MOVIE_ACTORS).on(MOVIES_MOVIE_ACTORS.MOVIE_ID.eq(MOVIES_MOVIE.ID))
                            .leftOuterJoin(MOVIES_ACTOR).on(MOVIES_ACTOR.ID.eq(MOVIES_MOVIE_ACTORS.ACTOR_ID))
                            .where(MOVIES_MOVIE.DELETED.eq(deleted).and(MOVIES_MOVIE.TO_DELETE.eq(toDelete)))
                            .orderBy(orderByField, MOVIES_MOVIE.ID)
                            .stream();


            return Validation.success(StreamSupport.stream(new MovieOutline.Spliterator(result.iterator()), false));
        });
    }
}
