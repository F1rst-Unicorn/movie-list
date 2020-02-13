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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Endpoint {

    private static final Logger LOG = LogManager.getLogger(Endpoint.class);

    protected boolean isValid(String parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter != null && ! parameter.isEmpty()) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    protected boolean isValid(int parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter > 0) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }

    protected boolean isValidVersion(int parameter, String name) {
        LOG.debug("Checking parameter " + name);

        if (parameter >= 0) {
            return true;
        }

        LOG.info("Request is invalid as " + name + " has value '" + parameter + "'");
        return false;
    }
}
