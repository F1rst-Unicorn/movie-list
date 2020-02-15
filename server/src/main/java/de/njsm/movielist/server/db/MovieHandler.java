package de.njsm.movielist.server.db;

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.*;
import de.njsm.movielist.server.db.jooq.tables.records.MoviesMovieRecord;
import fj.data.Validation;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.movielist.server.db.jooq.Tables.*;


public class MovieHandler extends FailSafeDatabaseHandler {

    public MovieHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
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

            return Validation.success(result);
        });
    }

    public StatusCode markToRemove(int id) {
        return runCommand(context -> {
            int changedRows = context.update(MOVIES_MOVIE)
                    .set(MOVIES_MOVIE.TO_DELETE, DSL.not(MOVIES_MOVIE.TO_DELETE))
                    .where(MOVIES_MOVIE.ID.eq(id))
                    .execute();

            return (changedRows == 0 ? StatusCode.NOT_FOUND : StatusCode.SUCCESS);
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
}
