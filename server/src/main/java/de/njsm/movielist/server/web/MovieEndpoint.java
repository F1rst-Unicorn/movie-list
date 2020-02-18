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

import de.njsm.movielist.server.business.*;
import de.njsm.movielist.server.business.data.MovieDetails;
import de.njsm.movielist.server.business.data.User;
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
import java.util.List;

@Path("movies")
public class MovieEndpoint extends TemplateEndpoint {

    private MovieManager manager;

    private UserManager userManager;

    private ActorManager actorManager;

    private GenreManager genreManager;

    public MovieEndpoint(Configuration configuration, MovieManager manager, UserManager userManager, ActorManager actorManager, GenreManager genreManager) {
        super(configuration);
        this.manager = manager;
        this.userManager = userManager;
        this.actorManager = actorManager;
        this.genreManager = genreManager;
    }

    @GET
    @Path("add")
    @Produces(MediaType.TEXT_HTML)
    public void getAddForm(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r) {

        processRequest(req, r, ar, "movie.html.ftl", (u, map) -> {
            map.put("actors", actorManager.get().success());
            map.put("genres", genreManager.get().success());
            map.put("locations", manager.getLocations().success());
        });
    }

    @POST
    @Path("add")
    @Produces(MediaType.TEXT_HTML)
    public Response addMovie(@Context HttpServletRequest req,
                             @BeanParam MovieDetails movie,
                             @FormParam("actors") List<Integer> actors,
                             @FormParam("genres") List<Integer> genres) {

        Validation<StatusCode, Integer> newMovieId = manager.add(movie, actors, genres);
        return Response.seeOther(URI.create("movies/" + newMovieId.success() + "/detail")).build();
    }

    @GET
    @Path("{movie: [0-9][0-9]*}/detail")
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

    @GET
    @Path("{movie: [0-9][0-9]*}/edit")
    @Produces(MediaType.TEXT_HTML)
    public void getEditForm(@Suspended AsyncResponse ar,
                            @Context HttpServletRequest req,
                            @Context HttpServletResponse r,
                            @PathParam("movie") int id) {

        processRequest(req, r, ar, "movie.html.ftl", (u, map) -> {
            map.put("movie", manager.get(id).success());
            map.put("actors", actorManager.getWithMovie(id).success());
            map.put("genres", genreManager.getWithMovie(id).success());
            map.put("locations", manager.getLocationsWithMovie(id).success());
            map.put("edit", true);
        });
    }

    @POST
    @Path("{movie: [0-9][0-9]*}/edit")
    @Produces(MediaType.TEXT_HTML)
    public Response edit(@Context HttpServletRequest req,
                         @BeanParam MovieDetails movie,
                         @FormParam("actors") List<Integer> actors,
                         @FormParam("genres") List<Integer> genres) {

        manager.edit(movie, actors, genres);
        return Response.seeOther(URI.create("movies/" + movie.getId() + "/detail")).build();
    }

    @POST
    @Path("{movie: [0-9][0-9]*}/mark_watched/{user}")
    public Response markWatched(@Context HttpServletRequest req,
                                @PathParam("movie") int id,
                                @PathParam("user") int user) {

        manager.markWatched(id, user);
        return Response.noContent().build();
    }

    @POST
    @Path("{movie: [0-9][0-9]*}/mark_removal")
    public Response markRemoval(@Context HttpServletRequest req,
                                @PathParam("movie") int id) {

        manager.markToRemove(id);
        return Response.seeOther(URI.create(req.getHeader("referer"))).build();
    }

    @POST
    @Path("{movie: [0-9][0-9]*}/delete")
    public Response delete(@Context HttpServletRequest req,
                           @PathParam("movie") int id) {

        manager.delete(id);
        return Response.seeOther(URI.create(req.getHeader("referer"))).build();
    }

    @POST
    @Path("{movie: [0-9][0-9]*}/add_comment")
    public Response addComment(@Context HttpServletRequest req,
                               @PathParam("movie") int id,
                               @FormParam("comment") String comment) {

        manager.addComment(id, comment, (User) req.getUserPrincipal());
        return Response.seeOther(URI.create(req.getHeader("referer"))).build();
    }

    @POST
    @Path("{movie: [0-9][0-9]*}/{comment}/delete")
    public Response deleteComment(@Context HttpServletRequest req,
                                  @PathParam("comment") int comment) {

        manager.deleteComment(comment);
        return Response.seeOther(URI.create(req.getHeader("referer"))).build();
    }
}
