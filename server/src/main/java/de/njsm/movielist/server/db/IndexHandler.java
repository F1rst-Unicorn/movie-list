package de.njsm.movielist.server.db;

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import fj.data.Validation;
import org.jooq.Record7;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.njsm.movielist.server.db.jooq.Tables.*;

public class IndexHandler extends FailSafeDatabaseHandler {

    public IndexHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public Validation<StatusCode, Stream<MovieOutline>> getLatest(User user) {
        return getInternally(user, false, false, MOVIES_MOVIE.CREATED_AT.desc());
    }

    public Validation<StatusCode, Stream<MovieOutline>> get(User user, boolean deleted, boolean toDelete) {
        return getInternally(user, deleted, toDelete, MOVIES_MOVIE.NAME.asc());
    }

    public Validation<StatusCode, Stream<MovieOutline>> getInternally(User user, boolean deleted, boolean toDelete, SortField<?> orderByField) {
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
                            .where(MOVIES_MOVIE.DELETED.eq(deleted).and(MOVIES_MOVIE.TO_DELETE.eq(toDelete)))
                            .orderBy(orderByField)
                            .fetchSize(1024)
                            .fetchLazy()
                            .stream());


            return Validation.success(StreamSupport.stream(new MovieOutline.Spliterator(result.iterator()), false));
        });
    }
}
