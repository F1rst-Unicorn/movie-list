package de.njsm.movielist.server.business;

import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.UserHandler;
import fj.data.Validation;

import java.util.List;

public class UserManager extends BusinessObject {

    private UserHandler handler;

    public UserManager(UserHandler dbHandler) {
        super(dbHandler);
        handler = dbHandler;
    }

    public Validation<StatusCode, List<User>> get() {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get();
        });
    }
}
