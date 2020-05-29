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
import de.njsm.movielist.server.db.jooq.tables.records.MoviesMovieRecord;
import fj.data.Validation;
import org.jooq.InsertValuesStep2;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.movielist.server.db.jooq.Tables.*;


public class MovieHandler extends FailSafeDatabaseHandler {

    public MovieHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public Validation<StatusCode, Integer> add(MovieDetails movie, List<Integer> actors, List<Integer> genres) {
        return runFunction(context -> {
            int newMovieId = context.insertInto(MOVIES_MOVIE)
                    .columns(
                            MOVIES_MOVIE.NAME,
                            MOVIES_MOVIE.DESCRIPTION,
                            MOVIES_MOVIE.LINK,
                            MOVIES_MOVIE.YEAR,
                            MOVIES_MOVIE.TO_DELETE,
                            MOVIES_MOVIE.DELETED,
                            MOVIES_MOVIE.CREATED_AT,
                            MOVIES_MOVIE.LOCATION_ID
                    ).values(
                            movie.getName(),
                            movie.getDescription(),
                            movie.getLink(),
                            movie.getYear(),
                            false,
                            false,
                            OffsetDateTime.now(),
                            movie.getLocation()
                    ).returning(MOVIES_MOVIE.ID)
                    .fetch()
                    .getValue(0, MOVIES_MOVIE.ID);

            InsertValuesStep2<?, Integer, Integer> stmt = context.insertInto(MOVIES_MOVIE_ACTORS)
                    .columns(MOVIES_MOVIE_ACTORS.ACTOR_ID, MOVIES_MOVIE_ACTORS.MOVIE_ID);
            for (int actor : actors) {
                stmt = stmt.values(actor, newMovieId);
            }
            stmt.execute();

            stmt = context.insertInto(MOVIES_MOVIESINGENRE)
                    .columns(MOVIES_MOVIESINGENRE.GENRE_ID, MOVIES_MOVIESINGENRE.MOVIE_ID);
            for (int genre : genres) {
                stmt = stmt.values(genre, newMovieId);
            }
            stmt.execute();

            return Validation.success(newMovieId);
        });
    }

    public StatusCode edit(MovieDetails movie, List<Integer> actors, List<Integer> genres) {
        return runCommand(context -> {
            context.update(MOVIES_MOVIE)
                    .set(MOVIES_MOVIE.NAME, movie.getName())
                    .set(MOVIES_MOVIE.DESCRIPTION, movie.getDescription())
                    .set(MOVIES_MOVIE.YEAR, movie.getYear())
                    .set(MOVIES_MOVIE.LOCATION_ID, movie.getLocation())
                    .set(MOVIES_MOVIE.LINK, movie.getLink())
                    .where(MOVIES_MOVIE.ID.eq(movie.getId()))
                    .execute();

            context.deleteFrom(MOVIES_MOVIE_ACTORS)
                    .where(MOVIES_MOVIE_ACTORS.MOVIE_ID.eq(movie.getId()))
                    .execute();
            InsertValuesStep2<?, Integer, Integer> stmt = context.insertInto(MOVIES_MOVIE_ACTORS)
                    .columns(MOVIES_MOVIE_ACTORS.ACTOR_ID, MOVIES_MOVIE_ACTORS.MOVIE_ID);
            for (int actor : actors) {
                stmt = stmt.values(actor, movie.getId());
            }
            stmt.execute();

            context.deleteFrom(MOVIES_MOVIESINGENRE)
                    .where(MOVIES_MOVIESINGENRE.MOVIE_ID.eq(movie.getId()))
                    .execute();
            stmt = context.insertInto(MOVIES_MOVIESINGENRE)
                    .columns(MOVIES_MOVIESINGENRE.GENRE_ID, MOVIES_MOVIESINGENRE.MOVIE_ID);
            for (int genre : genres) {
                stmt = stmt.values(genre, movie.getId());
            }
            stmt.execute();

            return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, MovieDetails> get(int id) {
        return runFunction(context -> {
            MoviesMovieRecord record = context.selectFrom(MOVIES_MOVIE)
                    .where(MOVIES_MOVIE.ID.eq(id))
                    .fetchOne();

            if (record == null) {
                return Validation.fail(StatusCode.NOT_FOUND);
            }

            MovieDetails result = new MovieDetails(
                    record.getId(),
                    record.getName(),
                    record.getDeleted(),
                    record.getToDelete(),
                    record.getYear(),
                    record.getCreatedAt(),
                    record.getLink(),
                    record.getDescription()
            );

            List<Genre> genres = context.select(MOVIES_GENRE.ID, MOVIES_GENRE.NAME)
                    .from(MOVIES_GENRE)
                    .join(MOVIES_MOVIESINGENRE).on(MOVIES_GENRE.ID.eq(MOVIES_MOVIESINGENRE.GENRE_ID))
                    .where(MOVIES_MOVIESINGENRE.MOVIE_ID.eq(id))
                    .stream()
                    .map(r -> new Genre(r.component1(), r.component2()))
                    .collect(Collectors.toList());
            result.setGenres(genres);

            List<WatchStatus> watches = context.select(AUTH_USER.ID, AUTH_USER.USERNAME, MOVIES_WATCHSTATUS.WATCHED_ON)
                    .from(MOVIES_WATCHSTATUS)
                    .join(AUTH_USER).on(MOVIES_WATCHSTATUS.USER_ID.eq(AUTH_USER.ID))
                    .where(MOVIES_WATCHSTATUS.MOVIE_ID.eq(id))
                    .stream()
                    .map(r -> new WatchStatus(
                            new User(r.component1(), r.component2()),
                            r.component3()))
                    .collect(Collectors.toList());
            result.setWatchedBy(watches);

            List<Actor> actors = context.select(MOVIES_ACTOR.ID, MOVIES_ACTOR.FIRST_NAME, MOVIES_ACTOR.LAST_NAME)
                    .from(MOVIES_ACTOR)
                    .join(MOVIES_MOVIE_ACTORS).on(MOVIES_ACTOR.ID.eq(MOVIES_MOVIE_ACTORS.ACTOR_ID))
                    .where(MOVIES_MOVIE_ACTORS.MOVIE_ID.eq(id))
                    .stream()
                    .map(r -> new Actor(r.component1(), r.component2(), r.component3()))
                    .collect(Collectors.toList());
            result.setActors(actors);

            List<Comment> comments = context.select(AUTH_USER.ID, AUTH_USER.USERNAME, MOVIES_COMMENT.ID, MOVIES_COMMENT.CONTENT)
                    .from(MOVIES_COMMENT)
                    .join(AUTH_USER).on(MOVIES_COMMENT.CREATOR_ID.eq(AUTH_USER.ID))
                    .where(MOVIES_COMMENT.MOVIE_ID.eq(id))
                    .orderBy(MOVIES_COMMENT.CREATED_AT.desc())
                    .stream()
                    .map(r -> new Comment(r.component3(), r.component4(), new User(r.component1(), r.component2())))
                    .collect(Collectors.toList());
            result.setComments(comments);

            String location = context.select(DSL.case_().when(MOVIES_LOCATION.INDEX.eq(0), MOVIES_LOCATION.NAME)
                                                        .otherwise(DSL.concat(MOVIES_LOCATION.NAME, DSL.concat(" ", MOVIES_LOCATION.INDEX.cast(String.class)))))
                    .from(MOVIES_LOCATION)
                    .where(MOVIES_LOCATION.ID.eq(record.getLocationId()))
                    .fetchOne()
                    .component1();
            result.setPrettyLocation(location);

            return Validation.success(result);
        });
    }

    public StatusCode markToRemove(int id) {
        return runCommand(context -> {
            int changedRows = context.update(MOVIES_MOVIE)
                    .set(MOVIES_MOVIE.TO_DELETE, DSL.not(MOVIES_MOVIE.TO_DELETE))
                    .where(MOVIES_MOVIE.ID.eq(id))
                    .execute();

            if (changedRows == 0)
                return StatusCode.NOT_FOUND;
            else
                return StatusCode.SUCCESS;
        });
    }

    public StatusCode markWatched(int id, int user) {
        return runCommand(context -> {

            int found = context.deleteFrom(MOVIES_WATCHSTATUS)
                    .where(MOVIES_WATCHSTATUS.USER_ID.eq(user).and(MOVIES_WATCHSTATUS.MOVIE_ID.eq(id)))
                    .execute();

            if (found == 0) {
                context.insertInto(MOVIES_WATCHSTATUS)
                        .columns(MOVIES_WATCHSTATUS.MOVIE_ID, MOVIES_WATCHSTATUS.USER_ID, MOVIES_WATCHSTATUS.WATCHED_ON)
                        .values(id, user, OffsetDateTime.now())
                        .execute();
            }

            return StatusCode.SUCCESS;
        });
    }

    public StatusCode delete(int id) {
        return runCommand(context -> {
            int changedRows = context.update(MOVIES_MOVIE)
                    .set(MOVIES_MOVIE.TO_DELETE, false)
                    .set(MOVIES_MOVIE.DELETED, DSL.not(MOVIES_MOVIE.DELETED))
                    .set(MOVIES_MOVIE.CREATED_AT, OffsetDateTime.now())
                    .where(MOVIES_MOVIE.ID.eq(id))
                    .execute();

            return (changedRows == 0 ? StatusCode.NOT_FOUND : StatusCode.SUCCESS);
        });
    }

    public StatusCode addComment(int id, String comment, User user) {
        return runCommand(context -> {
            context.insertInto(MOVIES_COMMENT)
                    .columns(MOVIES_COMMENT.MOVIE_ID, MOVIES_COMMENT.CREATOR_ID, MOVIES_COMMENT.CREATED_AT, MOVIES_COMMENT.CONTENT)
                    .values(id, user.getId(), OffsetDateTime.now(), comment)
                    .execute();

            return StatusCode.SUCCESS;
        });
    }

    public StatusCode deleteComment(int comment) {
        return runCommand(context -> {
            int result = context.deleteFrom(MOVIES_COMMENT)
                    .where(MOVIES_COMMENT.ID.eq(comment))
                    .execute();

            if (result == 0)
                return StatusCode.NOT_FOUND;
            else
                return StatusCode.SUCCESS;
        });
    }

    public Validation<StatusCode, List<Location>> getLocations() {
        return runFunction(context -> Validation.success(context.selectFrom(MOVIES_LOCATION)
                .orderBy(MOVIES_LOCATION.NAME, MOVIES_LOCATION.INDEX)
                .stream()
                .map(r -> new Location(r.getId(), r.getName(), r.getIndex()))
                .collect(Collectors.toList())));
    }

    public Validation<StatusCode, List<Location>> getLocations(int movieId) {
        return runFunction(context -> Validation.success(context.select(
                MOVIES_LOCATION.ID,
                MOVIES_LOCATION.NAME,
                MOVIES_LOCATION.INDEX,
                DSL.case_().when(MOVIES_LOCATION.ID.eq(
                        context.select(MOVIES_MOVIE.LOCATION_ID)
                                .from(MOVIES_MOVIE)
                                .where(MOVIES_MOVIE.ID.eq(movieId))), true)
                        .otherwise(false)
        )
                .from(MOVIES_LOCATION)
                .orderBy(MOVIES_LOCATION.NAME, MOVIES_LOCATION.INDEX)
                .stream()
                .map(r -> new Location(r.component1(), r.component2(), r.component3(), r.component4()))
                .collect(Collectors.toList())));
    }
}
