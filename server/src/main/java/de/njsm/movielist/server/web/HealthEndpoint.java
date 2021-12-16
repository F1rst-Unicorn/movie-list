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

import de.njsm.movielist.server.business.HealthManager;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.Health;
import fj.data.Validation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
public class HealthEndpoint {

    private HealthManager healthManager;

    @Inject
    public HealthEndpoint(HealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus() {
        Validation<StatusCode, Health> health = healthManager.get();
        return "{\"database\":" + (health.isSuccess() && health.success().db) + "}";
    }
}
