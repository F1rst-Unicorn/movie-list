package de.njsm.movielist.server.db;

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.*;
import de.njsm.movielist.server.db.jooq.tables.records.MoviesGenreRecord;
import fj.data.Validation;
import org.jooq.Record7;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.njsm.movielist.server.db.jooq.Tables.*;

public class GenreHandler extends FailSafeDatabaseHandler {


    public GenreHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
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

    public Validation<StatusCode, List<Genre>> getWithMovie(int movieId) {
        return runFunction(context -> Validation.success(context.select(
                MOVIES_GENRE.ID,
                MOVIES_GENRE.NAME,
                DSL.case_().when(MOVIES_GENRE.ID.in(
                        context.select(MOVIES_MOVIESINGENRE.GENRE_ID)
                                .from(MOVIES_MOVIESINGENRE)
                                .where(MOVIES_MOVIESINGENRE.MOVIE_ID.eq(movieId))), true)
                        .otherwise(false)
        )
                .from(MOVIES_GENRE)
                .orderBy(MOVIES_GENRE.NAME)
                .stream()
                .map(r -> new Genre(r.component1(), r.component2(), r.component3()))
                .collect(Collectors.toList())));
    }

    public Validation<StatusCode, List<MovieCount>> getCounts() {
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
                        .collect(Collectors.toList())
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

    public Validation<StatusCode, List<Genre>> get() {
        return runFunction(context -> Validation.success(context.selectFrom(MOVIES_GENRE)
                .orderBy(MOVIES_GENRE.NAME)
                .stream()
                .map(r -> new Genre(r.getId(), r.getName()))
                .collect(Collectors.toList()))
        );
    }

    public Validation<StatusCode, Stream<MovieOutline>> get(User user, int id) {
        return runFunction(context -> {
            Stream<Record7<Integer, String, String, Boolean, Boolean, Boolean, String>> result = Stream.of(1).flatMap(i ->
                    context.select(
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
                            .where(MOVIES_MOVIE.DELETED.eq(false).and(MOVIES_MOVIE.TO_DELETE.eq(false)).and(MOVIES_GENRE.ID.eq(id)))
                            .orderBy(MOVIES_MOVIE.NAME)
                            .fetchSize(1024)
                            .fetchLazy()
                            .stream());


            return Validation.success(StreamSupport.stream(new MovieOutline.Spliterator(result.iterator()), false));
        });
    }

}
