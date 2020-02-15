package de.njsm.movielist.server.business;

import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.SearchQuery;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.db.SearchHandler;
import fj.data.Validation;

import java.util.stream.Stream;

public class SearchManager extends BusinessObject {

    private SearchHandler handler;

    public SearchManager(SearchHandler dbHandler) {
        super(dbHandler);
        handler = dbHandler;
    }

    public Validation<StatusCode, Stream<MovieOutline>> get(User user, SearchQuery query) {
        return runFunction(() -> {
            handler.setReadOnly();
            return handler.get(user, query);
        });
    }
}
