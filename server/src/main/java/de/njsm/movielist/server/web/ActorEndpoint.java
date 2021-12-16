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

import de.njsm.movielist.server.business.ActorManager;
import de.njsm.movielist.server.business.data.Actor;
import freemarker.template.Configuration;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;

@Path("actors")
public class ActorEndpoint extends TemplateEndpoint {

    private ActorManager manager;

    @Inject
    public ActorEndpoint(Configuration configuration, ActorManager manager) {
        super(configuration);
        this.manager = manager;
    }

    @GET
    @Path("add")
    public void showAddActorForm(@Suspended AsyncResponse ar,
                                 @Context HttpServletRequest req,
                                 @Context HttpServletResponse r) {

        processRequest(req, r, ar, "actor.html.ftl", Collections.singletonMap("edit", false));
    }

    @POST
    @Path("add")
    public Response addActor(@BeanParam Actor a) {
        manager.add(a);
        return Response.seeOther(URI.create(""))
                .build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {

        processRequest(req, r, ar, "actors.html.ftl", manager.getMovieCounts(ar));
    }

    @GET
    @Path("{actor}/detail")
    @Produces(MediaType.TEXT_HTML)
    public void getDetails(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r,
                           @PathParam("actor") int id) {

        processRequest(req, r, ar, "actor_detail.html.ftl", manager.get(ar, getUser(req), id));
    }

    @GET
    @Path("{actor}/edit")
    @Produces(MediaType.TEXT_HTML)
    public void getEditForm(@Suspended AsyncResponse ar,
                            @Context HttpServletRequest req,
                            @Context HttpServletResponse r,
                            @PathParam("actor") int id) {

        processRequest(req, r, ar, "actor.html.ftl", manager.getEditForm(id));
    }

    @POST
    @Path("{actor}/edit")
    public Response editActor(@BeanParam Actor a) {
        manager.edit(a);
        return Response.seeOther(URI.create("actors/" + a.getId() + "/detail"))
                .build();
    }

    @GET
    @Path("{actor}/merge")
    @Produces(MediaType.TEXT_HTML)
    public void getMergeForm(@Suspended AsyncResponse ar,
                             @Context HttpServletRequest req,
                             @Context HttpServletResponse r,
                             @PathParam("actor") int id) {

        processRequest(req, r, ar, "actor_merge.html.ftl", manager.getMergeForm(ar, id));
    }

    @POST
    @Path("{actor}/merge")
    public Response merge(@BeanParam Actor actor, @FormParam("other") int id) {
        manager.merge(actor, id);
        return Response.seeOther(URI.create("actors/" + actor.getId() + "/detail"))
                .build();
    }
}
