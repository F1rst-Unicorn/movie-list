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

import static de.njsm.movielist.server.db.jooq.Tables.AUTH_USER;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.jooq.tables.records.AuthUserRecord;
import fj.data.Validation;

@Repository
@RequestScope
public class UserHandler extends FailSafeDatabaseHandler {

    public UserHandler(ConnectionFactory connectionFactory, @Qualifier("circuitBreakerDatabase") String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public Validation<StatusCode, List<User>> get() {
        return runFunction(context -> Validation.success(
                context.selectFrom(AUTH_USER)
                        .fetch()
                        .stream()
                        .map(r -> new User(r.getId(), r.getUsername()))
                        .collect(Collectors.toList())
        ));
    }

    Validation<StatusCode, User> getOrCreate(String username) {
        return runFunction(context -> {
            AuthUserRecord record = context.selectFrom(AUTH_USER)
                    .where(AUTH_USER.USERNAME.eq(username))
                    .fetchOne();
            if (record == null) {
                int id = context.insertInto(AUTH_USER)
                        .columns(AUTH_USER.USERNAME, AUTH_USER.PASSWORD)
                        .values(username, "")
                        .returning(AUTH_USER.ID)
                        .fetch()
                        .getValue(0, AUTH_USER.ID);
                return Validation.success(new User(id, username, ""));
            } else {
                return Validation.success(new User(record.getId(), record.getUsername(), record.getPassword()));
            }
        });
    }
}
