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

import de.njsm.movielist.server.business.GenreManager;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.Genre;
import de.njsm.movielist.server.business.data.MovieCount;
import de.njsm.movielist.server.business.data.MovieOutline;
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
import java.net.URI;
import java.util.List;

@Path("genres")
public class GenreEndpoint extends TemplateEndpoint {

    private GenreManager manager;

    public GenreEndpoint(Configuration configuration, GenreManager manager) {
        super(configuration);
        this.manager = manager;
    }

    @GET
    @Path("add")
    public void showAddGenreForm(@Suspended AsyncResponse ar,
                                 @Context HttpServletRequest req,
                                 @Context HttpServletResponse r) {

        processRequest(req, r, ar, "genre.html.ftl", (u, map) -> {
            map.put("edit", false);
        });
    }

    @POST
    @Path("add")
    public Response addGenre(@BeanParam Genre a) {
        manager.add(a);
        return Response.seeOther(URI.create("")).build();
    }

    @GET
    @Path("{genre}/edit")
    @Produces(MediaType.TEXT_HTML)
    public void getEditForm(@Suspended AsyncResponse ar,
                            @Context HttpServletRequest req,
                            @Context HttpServletResponse r,
                            @PathParam("genre") int id) {

        processRequest(req, r, ar, "genre.html.ftl", (u, map) -> {
            Validation<StatusCode, Genre> genre = manager.get(id);
            map.put("genre", genre.success());
            map.put("edit", true);
        });
    }

    @POST
    @Path("{genre}/edit")
    public Response editGenre(@BeanParam Genre a) {
        manager.edit(a);
        return Response.seeOther(URI.create("genres/" + a.getId() + "/detail")).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {

        processRequest(req, r, ar, "genres.html.ftl", (u, map) -> {
            Validation<StatusCode, List<MovieCount>> movie = manager.getCounts();
            map.put("items", movie.success());
        });
    }

    @GET
    @Path("{genre}/detail")
    @Produces(MediaType.TEXT_HTML)
    public void getDetails(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r,
                           @PathParam("genre") int id) {

        processRequest(req, r, ar, "genre_detail.html.ftl", (u, map) -> {
            Validation<StatusCode, Genre> genre = manager.get(id);
            map.put("movies", (Iterable<MovieOutline>) () -> manager.getMovies(ar, u, id).success().iterator());
            map.put("genre", genre.success());
        });
    }

}
