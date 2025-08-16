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

import fj.data.Validation;
import freemarker.template.Configuration;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;

@Path("/")
public class AuthEndpoint extends TemplateEndpoint {

    @Inject
    public AuthEndpoint(Configuration configuration) {
        super(configuration);
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    public void get(@Suspended AsyncResponse ar,
                    @Context HttpServletRequest req,
                    @Context HttpServletResponse r,
                    @QueryParam("error") String error) {

        processRequest(req, r, ar, "login.html.ftl",
                Validation.success(Collections.singletonMap("error", error != null)));
    }

    @GET
    @Path("create_account")
    @Produces(MediaType.TEXT_PLAIN)
    public String createAccount() {
        return "Account creation disabled!";
    }
}
