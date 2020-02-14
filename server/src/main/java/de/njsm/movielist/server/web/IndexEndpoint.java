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

import de.njsm.movielist.server.business.IndexManager;
import de.njsm.movielist.server.business.data.MovieOutline;
import de.njsm.movielist.server.business.data.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Path("/")
public class IndexEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(IndexEndpoint.class);

    private Configuration configuration;

    private IndexManager manager;

    public IndexEndpoint(Configuration configuration, IndexManager manager) {
        this.configuration = configuration;
        this.manager = manager;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r) {
        try (PrintWriter drain = r.getWriter()) {
            Template template = configuration.getTemplate("index.html.ftl");
            User u = (User) SecurityContextHolder.getContext().getAuthentication();

            HashMap<String, Object> context = new HashMap<>();
            context.put("user", u);
            context.put("csrftoken", req.getAttribute("_csrf"));
            context.put("movies", (Iterable<MovieOutline>) () -> manager.get(ar, u).success().iterator());
            context.put("lang", "en");
            template.process(context, drain);
            ar.resume(new Object());
            drain.flush();
        } catch (IOException | TemplateException e) {
            LOG.error(e);
        }
    }

    @GET
    @Path("to_delete")
    @Produces(MediaType.TEXT_HTML)
    public void getToDelete(@Suspended AsyncResponse ar,
            @Context HttpServletRequest req,
            @Context HttpServletResponse r) {
        try (PrintWriter drain = r.getWriter()) {
            Template template = configuration.getTemplate("index.html.ftl");

            User u = (User) SecurityContextHolder.getContext().getAuthentication();
            HashMap<String, Object> context = new HashMap<>();
            context.put("user", u);
            context.put("csrftoken", req.getAttribute("_csrf"));
            context.put("movies", (Iterable<MovieOutline>) () -> manager.getToDelete(ar, u).success().iterator());
            context.put("lang", "en");
            template.process(context, drain);
            ar.resume(new Object());
            drain.flush();
        } catch (IOException | TemplateException e) {
            LOG.error(e);
        }
    }

    @GET
    @Path("absent")
    @Produces(MediaType.TEXT_HTML)
    public void getDeleted(@Suspended AsyncResponse ar,
                           @Context HttpServletRequest req,
                           @Context HttpServletResponse r) {
        try (PrintWriter drain = r.getWriter()) {
            Template template = configuration.getTemplate("index.html.ftl");

            User u = (User) SecurityContextHolder.getContext().getAuthentication();
            HashMap<String, Object> context = new HashMap<>();
            context.put("user", u);
            context.put("csrftoken", req.getAttribute("_csrf"));
            context.put("movies", (Iterable<MovieOutline>) () -> manager.getDeleted(ar, u).success().iterator());
            context.put("lang", "en");
            template.process(context, drain);
            ar.resume(new Object());
            drain.flush();
        } catch (IOException | TemplateException e) {
            LOG.error(e);
        }
    }
}
