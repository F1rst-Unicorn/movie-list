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

package de.njsm.movielist.server.web;

import de.njsm.movielist.server.business.SearchManager;
import de.njsm.movielist.server.business.data.SearchQuery;
import freemarker.template.Configuration;

import jakarta.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("search")
public class SearchEndpoint extends TemplateEndpoint {

    private final SearchManager searchManager;

    @Inject
    public SearchEndpoint(Configuration configuration, SearchManager searchManager) {
        super(configuration);
        this.searchManager = searchManager;
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {
        processRequest(req, r, ar, "search.html.ftl", searchManager.getFormContent(ar));
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public void search(@Suspended AsyncResponse ar,
                       @Context HttpServletRequest req,
                       @Context HttpServletResponse r,
                       @BeanParam SearchQuery query) {
        processRequest(req, r, ar, "search.html.ftl", searchManager.search(ar, getUser(req), query));
    }
}
