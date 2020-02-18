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

import de.njsm.movielist.server.business.IndexManager;
import de.njsm.movielist.server.business.data.Location;
import de.njsm.movielist.server.business.data.MovieOutline;
import freemarker.template.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class IndexEndpoint extends TemplateEndpoint {

    private IndexManager manager;

    public IndexEndpoint(Configuration configuration, IndexManager manager) {
        super(configuration);
        this.manager = manager;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {

        processRequest(req, r, ar, "index.html.ftl", (u, map) -> {
            map.put("movies", (Iterable<MovieOutline>) () -> manager.get(ar, u).success().iterator());
        });
    }

    @GET
    @Path("to_delete")
    @Produces(MediaType.TEXT_HTML)
    public void getToDelete(@Suspended AsyncResponse ar,
                            @Context HttpServletRequest req,
                            @Context HttpServletResponse r) {

        processRequest(req, r, ar, "index.html.ftl", (u, map) -> {
            map.put("movies", (Iterable<MovieOutline>) () -> manager.getToDelete(ar, u).success().iterator());
        });
    }

    @GET
    @Path("absent")
    @Produces(MediaType.TEXT_HTML)
    public void getDeleted(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r) {
        processRequest(req, r, ar, "index.html.ftl", (u, map) -> {
            map.put("movies", (Iterable<MovieOutline>) () -> manager.getDeleted(ar, u).success().iterator());
        });
    }

    @GET
    @Path("latest")
    @Produces(MediaType.TEXT_HTML)
    public void getLatest(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r) {
        processRequest(req, r, ar, "index.html.ftl", (u, map) -> {
            map.put("movies", (Iterable<MovieOutline>) () -> manager.getLatest(ar, u).success().iterator());
        });
    }

    @GET
    @Path("add_location")
    public void showAddActorForm(@Suspended AsyncResponse ar,
                                 @Context HttpServletRequest req,
                                 @Context HttpServletResponse r) {

        processRequest(req, r, ar, "location.html.ftl", (u, map) -> {});
    }

    @POST
    @Path("add_location")
    public Response addActor(@BeanParam Location a) {
        manager.addLocation(a);
        return Response.seeOther(URI.create("")).build();
    }


}
