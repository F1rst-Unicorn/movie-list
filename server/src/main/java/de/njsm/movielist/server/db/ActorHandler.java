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

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.*;
import de.njsm.movielist.server.db.jooq.tables.records.MoviesActorRecord;
import fj.data.Validation;
import org.jooq.Record7;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.njsm.movielist.server.db.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

public class ActorHandler extends FailSafeDatabaseHandler {

    public ActorHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public StatusCode add(Actor data) {
        return runCommand(context -> {
            context.insertInto(MOVIES_ACTOR)
                    .columns(MOVIES_ACTOR.FIRST_NAME, MOVIES_ACTOR.LAST_NAME)
                    .values(data.getFirstName(), data.getLastName())
                    .execute();
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode edit(Actor data) {
        return runCommand(context -> {
            int result = context.update(MOVIES_ACTOR)
                    .set(MOVIES_ACTOR.FIRST_NAME, data.getFirstName())
                    .set(MOVIES_ACTOR.LAST_NAME, data.getLastName())
                    .where(MOVIES_ACTOR.ID.eq(data.getId()))
                    .execute();

            if (result == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.NOT_FOUND;
        });
    }

    public StatusCode merge(Actor data, int other) {
        return runCommand(context -> {
            context.update(MOVIES_MOVIE_ACTORS)
                    .set(MOVIES_MOVIE_ACTORS.ACTOR_ID, data.getId())
                    .where(MOVIES_MOVIE_ACTORS.ACTOR_ID.eq(other))
                    .and(row(inline(data.getId(), Integer.class), MOVIES_MOVIE_ACTORS.MOVIE_ID).notIn(
                            select(MOVIES_MOVIE_ACTORS.ACTOR_ID, MOVIES_MOVIE_ACTORS.MOVIE_ID)
                                    .from(MOVIES_MOVIE_ACTORS)
                                    .where(MOVIES_MOVIE_ACTORS.ACTOR_ID.eq(data.getId()))
                    ))
                    .execute();

            context.deleteFrom(MOVIES_MOVIE_ACTORS)
                    .where(MOVIES_MOVIE_ACTORS.ACTOR_ID.eq(other))
                    .execute();

            int result = context.deleteFrom(MOVIES_ACTOR)
                    .where(MOVIES_ACTOR.ID.eq(other))
                    .execute();

            if (result == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.NOT_FOUND;
        });
    }

    public Validation<StatusCode, Stream<Actor>> get() {
        return runFunction(context -> Validation.success(context.selectFrom(MOVIES_ACTOR)
                .orderBy(MOVIES_ACTOR.LAST_NAME, MOVIES_ACTOR.FIRST_NAME)
                .stream()
                .map(r -> new Actor(r.getId(), r.getFirstName(), r.getLastName()))));
    }

    public Validation<StatusCode, Stream<MovieCount>> getActorsWithMovieCounts() {
        return runFunction(context -> Validation.success(
                context.select(MOVIES_ACTOR.ID,
                        DSL.concat(MOVIES_ACTOR.FIRST_NAME, DSL.concat(" ", MOVIES_ACTOR.LAST_NAME)),
                        DSL.count(MOVIES_MOVIE_ACTORS.ID))
                        .from(MOVIES_ACTOR)
                        .leftOuterJoin(MOVIES_MOVIE_ACTORS).on(MOVIES_ACTOR.ID.eq(MOVIES_MOVIE_ACTORS.ACTOR_ID))
                        .groupBy(MOVIES_ACTOR.ID)
                        .orderBy(MOVIES_ACTOR.LAST_NAME)
                        .stream()
                        .map(r -> new MovieCount(r.component1(), r.component2(), r.component3()))));
    }

    public Validation<StatusCode, Stream<MovieOutline>> getMoviesOfActor(User user, int id) {
        return runFunction(context -> {
            Stream<Record7<Integer, String, String, Boolean, Boolean, Boolean, String>> result =
                    context.select(
                            MOVIES_MOVIE.ID,
                            MOVIES_MOVIE.NAME,
                            DSL.case_().when(MOVIES_LOCATION.INDEX.eq(0), MOVIES_LOCATION.NAME)
                                    .otherwise(DSL.concat(MOVIES_LOCATION.NAME, DSL.concat(" ", MOVIES_LOCATION.INDEX.cast(String.class)))),
                            DSL.case_().when(MOVIES_MOVIE.ID.in(
                                    context.select(MOVIES_WATCHSTATUS.MOVIE_ID)
                                            .from(MOVIES_WATCHSTATUS)
                                            .where(MOVIES_WATCHSTATUS.USER_ID.eq(user.getId()))),
                                    inline(true))
                                    .otherwise(false).as("watched_by_user"),
                            MOVIES_MOVIE.DELETED,
                            MOVIES_MOVIE.TO_DELETE,
                            DSL.concat(MOVIES_ACTOR.FIRST_NAME, DSL.concat(" ", MOVIES_ACTOR.LAST_NAME))
                    )
                            .from(MOVIES_MOVIE)
                            .join(MOVIES_LOCATION).on(MOVIES_MOVIE.LOCATION_ID.eq(MOVIES_LOCATION.ID))
                            .leftOuterJoin(MOVIES_MOVIE_ACTORS).on(MOVIES_MOVIE_ACTORS.MOVIE_ID.eq(MOVIES_MOVIE.ID))
                            .leftOuterJoin(MOVIES_ACTOR).on(MOVIES_ACTOR.ID.eq(MOVIES_MOVIE_ACTORS.ACTOR_ID))
                            .where(MOVIES_MOVIE.DELETED.eq(false).and(MOVIES_MOVIE.TO_DELETE.eq(false)).and(
                                    MOVIES_MOVIE.ID.in(context.select(MOVIES_MOVIE_ACTORS.MOVIE_ID)
                                            .from(MOVIES_MOVIE_ACTORS)
                                            .where(MOVIES_MOVIE_ACTORS.ACTOR_ID.eq(id)))))
                            .orderBy(MOVIES_MOVIE.NAME, MOVIES_MOVIE.ID)
                            .stream();


            return Validation.success(StreamSupport.stream(new MovieOutline.Spliterator(result.iterator()), false));
        });
    }

    public Validation<StatusCode, Actor> get(int id) {
        return runFunction(context -> {
                    MoviesActorRecord record = context.selectFrom(MOVIES_ACTOR)
                            .where(MOVIES_ACTOR.ID.eq(id))
                            .fetchOne();
                    if (record == null)
                        return Validation.fail(StatusCode.NOT_FOUND);
                    else {
                        return Validation.success(new Actor(record.getId(), record.getFirstName(), record.getLastName()));
                    }
                }
        );
    }

    public Validation<StatusCode, List<Actor>> getActorsParticipatingIn(int movie) {
        return runFunction(context -> Validation.success(context.select(
                MOVIES_ACTOR.ID,
                MOVIES_ACTOR.FIRST_NAME,
                MOVIES_ACTOR.LAST_NAME,
                DSL.case_().when(MOVIES_ACTOR.ID.in(
                        context.select(MOVIES_MOVIE_ACTORS.ACTOR_ID)
                                .from(MOVIES_MOVIE_ACTORS)
                                .where(MOVIES_MOVIE_ACTORS.MOVIE_ID.eq(movie))), true)
                        .otherwise(false)
        )
                .from(MOVIES_ACTOR)
                .orderBy(MOVIES_ACTOR.LAST_NAME)
                .stream()
                .map(r -> new Actor(r.component1(), r.component2(), r.component3(), r.component4()))
                .collect(Collectors.toList())));
    }


}
