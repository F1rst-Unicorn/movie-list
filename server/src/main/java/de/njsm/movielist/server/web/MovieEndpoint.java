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

import de.njsm.movielist.server.business.MovieManager;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.UserManager;
import de.njsm.movielist.server.business.data.MovieDetails;
import fj.data.Validation;
import freemarker.template.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

@Path("/movies/{movie: [0-9][0-9]*}")
public class MovieEndpoint extends TemplateEndpoint {

    private MovieManager manager;

    private UserManager userManager;

    public MovieEndpoint(Configuration configuration, MovieManager manager, UserManager userManager) {
        super(configuration);
        this.manager = manager;
        this.userManager = userManager;
    }

    @GET
    @Path("detail")
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r,
                    @PathParam("movie") int id) {

        processRequest(req, r, ar, "movie_detail.html.ftl", (u, map) -> {
            Validation<StatusCode, MovieDetails> movie = manager.get(id);

            if (movie.isSuccess())
                map.put("movie", movie.success());
            else {
                r.setStatus(movie.fail().toHttpStatus().getStatusCode());
                throw new IOException("Invalid ID");
            }

            map.put("users", userManager.get().success());
        });
    }

    @POST
    @Path("mark_watched/{user}")
    public Response markWatched(@Context HttpServletRequest req,
                                @PathParam("movie") int id,
                                @PathParam("user") int user) {

        manager.markWatched(id, user);
        return Response.noContent().build();
    }

    @POST
    @Path("mark_removal")
    public Response markRemoval(@Context HttpServletRequest req,
                                @PathParam("movie") int id) {

        manager.markToRemove(id);
        return Response.seeOther(URI.create(req.getHeader("referer"))).build();
    }

    @POST
    @Path("delete")
    public Response delete(@Context HttpServletRequest req,
                                @PathParam("movie") int id) {

        manager.delete(id);
        return Response.seeOther(URI.create(req.getHeader("referer"))).build();
    }
}
