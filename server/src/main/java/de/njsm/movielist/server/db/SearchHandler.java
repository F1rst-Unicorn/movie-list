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
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.SearchQuery;
import de.njsm.movielist.server.business.data.User;
import fj.data.Validation;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static de.njsm.movielist.server.db.jooq.Tables.*;

public class SearchHandler extends FailSafeDatabaseHandler {


    public SearchHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public Validation<StatusCode, Stream<MovieOutline>> get(User user, SearchQuery query) {
        return runFunction(context -> {

            Condition genreCondition = (query.getGenres().isEmpty() ? DSL.trueCondition() : MOVIES_MOVIESINGENRE.GENRE_ID.in(query.getGenres()));
            Condition userCondition = (query.getUsers().isEmpty() ? DSL.trueCondition() : MOVIES_MOVIE.ID.notIn(context.select(MOVIES_WATCHSTATUS.MOVIE_ID)
                    .from(MOVIES_WATCHSTATUS)
                    .where(MOVIES_WATCHSTATUS.USER_ID.in(query.getUsers()))));
            Condition[] fulltextConditions = mapTermToPredicates(query.getTerm());

            List<Integer> scopes = query.getScopes().stream().filter(i -> i >= 0 && i < 3).collect(Collectors.toList());
            if (query.getScopes().isEmpty())
                scopes = Arrays.asList(0, 1, 2);

            Iterator<Integer> x = scopes.iterator();
            int i = x.next();
            SelectOrderByStep<Record17<Integer, String, String, Integer, Boolean, Boolean, OffsetDateTime, Integer, Boolean, Integer, Integer, String, Integer, String, String, String, String>>
                    table = selectInternalColumns(i, user, context)
                    .where(
                            DSL.and(
                                    MOVIES_MOVIE.DELETED.eq(query.isIncludeMissing()),
                                    MOVIES_MOVIE.TO_DELETE.eq(query.isIncludeMissing()),
                                    genreCondition,
                                    userCondition,
                                    fulltextConditions[i]
                            )
                    );

            while (x.hasNext()) {
                i = x.next();
                table.union(selectInternalColumns(i, user, context)
                        .where(
                                DSL.and(
                                        MOVIES_MOVIE.DELETED.eq(query.isIncludeMissing()),
                                        MOVIES_MOVIE.TO_DELETE.eq(query.isIncludeMissing()),
                                        genreCondition,
                                        userCondition,
                                        fulltextConditions[i]
                                )

                        )
                );
            }

            return Validation.success(
                    StreamSupport.stream(
                            new MovieOutline.Spliterator(
                                    context.select(
                                            DSL.field("mid", Integer.class),
                                            DSL.field("mname", String.class),
                                            DSL.field("lname", String.class),
                                            DSL.field("watched_by_user", Boolean.class),
                                            DSL.field("mdeleted", Boolean.class),
                                            DSL.field("mto_delete", Boolean.class),
                                            DSL.field("aname", String.class)
                                    ).from(table.orderBy(DSL.field("kind").asc(), MOVIES_MOVIE.CREATED_AT.desc(), DSL.field("mid")))
                                            .stream()
                                            .iterator()), false)
            );
        });
    }

    private SelectOnConditionStep<Record17<Integer, String, String, Integer, Boolean, Boolean, OffsetDateTime, Integer, Boolean, Integer, Integer, String, Integer, String, String, String, String>>
    selectInternalColumns(int kind, User user, DSLContext context) {
        return context.selectDistinct(
                MOVIES_MOVIE.ID.as("mid"),
                MOVIES_MOVIE.NAME.as("mname"),
                MOVIES_MOVIE.DESCRIPTION,
                MOVIES_MOVIE.YEAR,
                MOVIES_MOVIE.TO_DELETE.as("mto_delete"),
                MOVIES_MOVIE.DELETED.as("mdeleted"),
                MOVIES_MOVIE.CREATED_AT,
                MOVIES_MOVIE.LOCATION_ID,
                DSL.case_().when(MOVIES_MOVIE.ID.in(
                        context.select(MOVIES_WATCHSTATUS.MOVIE_ID)
                                .from(MOVIES_WATCHSTATUS)
                                .where(MOVIES_WATCHSTATUS.USER_ID.eq(user.getId()))),
                        DSL.inline(true))
                        .otherwise(false).as("watched_by_user"),
                DSL.inline(kind).as("kind"),
                MOVIES_LOCATION.ID,
                MOVIES_LOCATION.NAME,
                MOVIES_LOCATION.INDEX,
                DSL.case_().when(MOVIES_LOCATION.INDEX.eq(0), MOVIES_LOCATION.NAME)
                        .otherwise(DSL.concat(MOVIES_LOCATION.NAME, DSL.concat(" ", MOVIES_LOCATION.INDEX.cast(String.class)))).as("lname"),
                MOVIES_ACTOR.FIRST_NAME,
                MOVIES_ACTOR.LAST_NAME,
                DSL.concat(MOVIES_ACTOR.FIRST_NAME, DSL.concat(" ", MOVIES_ACTOR.LAST_NAME)).as("aname")
        )
                .from(MOVIES_MOVIE)
                .join(MOVIES_LOCATION).on(MOVIES_LOCATION.ID.eq(MOVIES_MOVIE.LOCATION_ID))
                .leftOuterJoin(MOVIES_MOVIESINGENRE).on(MOVIES_MOVIE.ID.eq(MOVIES_MOVIESINGENRE.MOVIE_ID))
                .leftOuterJoin(MOVIES_MOVIE_ACTORS).on(MOVIES_MOVIE_ACTORS.MOVIE_ID.eq(MOVIES_MOVIE.ID))
                .leftOuterJoin(MOVIES_ACTOR).on(MOVIES_ACTOR.ID.eq(MOVIES_MOVIE_ACTORS.ACTOR_ID))
                .leftOuterJoin(MOVIES_COMMENT).on(MOVIES_MOVIE.ID.eq(MOVIES_COMMENT.CREATOR_ID));
    }

    private Condition[] mapTermToPredicates(String term) {
        if (term != null && !term.isEmpty())
            return new Condition[]{
                    DSL.condition("to_tsvector(coalesce(movies_movie.name, '')) @@ plainto_tsquery(?) = true", term),
                    DSL.condition("to_tsvector(coalesce(movies_movie.description, '')) @@ plainto_tsquery(?) = true", term),
                    DSL.condition("to_tsvector(coalesce(movies_comment.content, '')) @@ plainto_tsquery(?) = true", term),
            };
        else
            return new Condition[]{
                    DSL.trueCondition(),
                    DSL.trueCondition(),
                    DSL.trueCondition(),
            };
    }
}
