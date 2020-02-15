package de.njsm.movielist.server.db;

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.User;
import fj.data.Validation;

import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.movielist.server.db.jooq.Tables.AUTH_USER;

public class UserHandler extends FailSafeDatabaseHandler {

    public UserHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
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
}
