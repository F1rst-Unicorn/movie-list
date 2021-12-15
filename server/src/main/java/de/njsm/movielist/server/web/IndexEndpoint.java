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
import freemarker.template.Configuration;

import jakarta.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class IndexEndpoint extends TemplateEndpoint {

    private IndexManager manager;

    @Inject
    public IndexEndpoint(Configuration configuration, IndexManager manager) {
        super(configuration);
        this.manager = manager;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {

        processRequest(req, r, ar, "index.html.ftl", manager.get(ar, getUser(req)));
    }

    @GET
    @Path("to_delete")
    @Produces(MediaType.TEXT_HTML)
    public void getToDelete(@Suspended AsyncResponse ar,
                            @Context HttpServletRequest req,
                            @Context HttpServletResponse r) {

        processRequest(req, r, ar, "index.html.ftl", manager.getToDelete(ar, getUser(req)));
    }

    @GET
    @Path("absent")
    @Produces(MediaType.TEXT_HTML)
    public void getDeleted(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r) {
        processRequest(req, r, ar, "index.html.ftl", manager.getDeleted(ar, getUser(req)));
    }

    @GET
    @Path("latest")
    @Produces(MediaType.TEXT_HTML)
    public void getLatest(@Suspended AsyncResponse ar,
                          @Context HttpServletRequest req,
                          @Context HttpServletResponse r) {
        processRequest(req, r, ar, "index.html.ftl", manager.getLatest(ar, getUser(req)));
    }

    @GET
    @Path("add_location")
    public void showAddActorForm(@Suspended AsyncResponse ar,
                                 @Context HttpServletRequest req,
                                 @Context HttpServletResponse r) {

        processRequest(req, r, ar, "location.html.ftl");
    }

    @POST
    @Path("add_location")
    public Response addActor(@BeanParam Location a) {
        manager.addLocation(a);
        return Response.seeOther(URI.create("")).build();
    }


}
