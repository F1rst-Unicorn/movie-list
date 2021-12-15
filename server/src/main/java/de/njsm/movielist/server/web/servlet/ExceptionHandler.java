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

package de.njsm.movielist.server.web.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

@Path("error")
public class ExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(ExceptionHandler.class);

    static final String EXCEPTION_KEY = "javax.servlet.error.exception";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public String put(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String delete(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String post(@Context HttpServletRequest request,
                        @Context HttpServletResponse response) {
        return processError(request, response);
    }

    private String processError(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        Throwable throwable = (Throwable) request.getAttribute(EXCEPTION_KEY);

        LOG.error("Caught exception leaving web app", throwable);

        response.setStatus(500);
        return "\"General Error\"";
    }

}
