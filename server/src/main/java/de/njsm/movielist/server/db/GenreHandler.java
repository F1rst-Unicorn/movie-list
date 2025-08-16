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
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_GENRE;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_LOCATION;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_MOVIE;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_MOVIESINGENRE;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_MOVIE_ACTORS;
import static de.njsm.movielist.server.db.jooq.Tables.MOVIES_WATCHSTATUS;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jooq.Record7;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.Genre;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.jooq.tables.records.MoviesGenreRecord;
import fj.data.Validation;

@Repository
@RequestScope
public class GenreHandler extends FailSafeDatabaseHandler {


    public GenreHandler(ConnectionFactory connectionFactory, @Qualifier("circuitBreakerDatabase") String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public StatusCode add(Genre data) {
        return runCommand(context -> {
            context.insertInto(MOVIES_GENRE)
                    .columns(MOVIES_GENRE.NAME)
                    .values(data.getName())
                    .execute();
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode edit(Genre data) {
        return runCommand(context -> {
            int result = context.update(MOVIES_GENRE)
                    .set(MOVIES_GENRE.NAME, data.getName())
                    .where(MOVIES_GENRE.ID.eq(data.getId()))
                    .execute();

            if (result == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.NOT_FOUND;
        });
    }

    public Validation<StatusCode, Stream<Genre>> getWithMovie(int movie) {
        return runFunction(context -> Validation.success(context.select(
                MOVIES_GENRE.ID,
                MOVIES_GENRE.NAME,
                DSL.case_().when(MOVIES_GENRE.ID.in(
                        context.select(MOVIES_MOVIESINGENRE.GENRE_ID)
                                .from(MOVIES_MOVIESINGENRE)
                                .where(MOVIES_MOVIESINGENRE.MOVIE_ID.eq(movie))), true)
                        .otherwise(false)
        )
                .from(MOVIES_GENRE)
                .orderBy(MOVIES_GENRE.NAME)
                .stream()
                .map(r -> new Genre(r.component1(), r.component2(), r.component3()))));
    }

    public Validation<StatusCode, Stream<MovieCount>> getCounts() {
        return runFunction(context -> Validation.success(
                context.select(MOVIES_GENRE.ID,
                        MOVIES_GENRE.NAME,
                        DSL.count(MOVIES_MOVIESINGENRE.ID))
                        .from(MOVIES_GENRE)
                        .leftOuterJoin(MOVIES_MOVIESINGENRE).on(MOVIES_GENRE.ID.eq(MOVIES_MOVIESINGENRE.GENRE_ID))
                        .groupBy(MOVIES_GENRE.ID)
                        .orderBy(MOVIES_GENRE.NAME)
                        .stream()
                        .map(r -> new MovieCount(r.component1(), r.component2(), r.component3()))
        ));
    }

    public Validation<StatusCode, Genre> get(int id) {
        return runFunction(context -> {
                    MoviesGenreRecord record = context.selectFrom(MOVIES_GENRE)
                            .where(MOVIES_GENRE.ID.eq(id))
                            .fetchOne();
                    if (record == null)
                        return Validation.fail(StatusCode.NOT_FOUND);
                    else {
                        return Validation.success(new Genre(record.getId(), record.getName()));
                    }
                }
        );
    }

    public Validation<StatusCode, Stream<Genre>> get() {
        return runFunction(context -> Validation.success(context.selectFrom(MOVIES_GENRE)
                .orderBy(MOVIES_GENRE.NAME)
                .stream()
                .map(r -> new Genre(r.getId(), r.getName()))
        ));
    }

    public Validation<StatusCode, Stream<MovieOutline>> getMoviesInGenre(User user, int genre) {
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
                            .leftOuterJoin(MOVIES_MOVIESINGENRE).on(MOVIES_MOVIE.ID.eq(MOVIES_MOVIESINGENRE.MOVIE_ID))
                            .leftOuterJoin(MOVIES_GENRE).on(MOVIES_GENRE.ID.eq(MOVIES_MOVIESINGENRE.GENRE_ID))
                            .where(MOVIES_MOVIE.DELETED.eq(false).and(MOVIES_MOVIE.TO_DELETE.eq(false)).and(MOVIES_GENRE.ID.eq(genre)))
                            .orderBy(MOVIES_MOVIE.NAME)
                            .stream();


            return Validation.success(StreamSupport.stream(new MovieOutline.Spliterator(result.iterator()), false));
        });
    }

}
