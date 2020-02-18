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

public class Location {

    private int id;

    @FormParam("name")
    private String name;

    @FormParam("index")
    private int index;

    private boolean selected;

    public Location() {
    }

    public Location(int id, String name, int index) {
        this.id = id;
        this.name = name;
        this.index = index;
    }

    public Location(int id, String name, int index, boolean selected) {
        this.id = id;
        this.name = name;
        this.index = index;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public String toString() {
        if (index == 0)
            return name;
        else
            return name + " " + index;
    }
}
