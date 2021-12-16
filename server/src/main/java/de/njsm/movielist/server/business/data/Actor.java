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

package de.njsm.movielist.server.business.data;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

public class Actor {

    @PathParam("actor")
    private int id;

    @FormParam("firstname")
    private String firstName;

    @FormParam("lastname")
    private String lastName;

    private boolean selected;

    public Actor() {}

    public Actor(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Actor(int id, String firstName, String lastName, boolean selected) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
