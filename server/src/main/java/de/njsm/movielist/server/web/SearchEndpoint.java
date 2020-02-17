/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.movielist.server.web;

import de.njsm.movielist.server.business.GenreManager;
import de.njsm.movielist.server.business.SearchManager;
import de.njsm.movielist.server.business.UserManager;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.SearchQuery;
import freemarker.template.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("search")
public class SearchEndpoint extends TemplateEndpoint {

    private GenreManager genreManager;

    private UserManager userManager;

    private SearchManager searchManager;

    public SearchEndpoint(Configuration configuration, GenreManager genreManager, UserManager userManager, SearchManager searchManager) {
        super(configuration);
        this.genreManager = genreManager;
        this.userManager = userManager;
        this.searchManager = searchManager;
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {
        processRequest(req, r, ar, "search.html.ftl", (user, map) -> {
            map.put("users", userManager.get().success());
            map.put("genres", genreManager.getCounts().success());
        });
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public void search(@Suspended AsyncResponse ar,
                       @Context HttpServletRequest req,
                       @Context HttpServletResponse r,
                       @BeanParam SearchQuery query) {
        processRequest(req, r, ar, "search.html.ftl", (user, map) -> {
            map.put("users", userManager.get().success());
            map.put("genres", genreManager.getCounts().success());
            map.put("movies", (Iterable<MovieOutline>) () -> searchManager.get(ar, user, query).success().iterator());
        });

    }
}
