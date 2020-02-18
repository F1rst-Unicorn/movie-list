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

import de.njsm.movielist.server.Config;

import java.time.OffsetDateTime;
import java.util.TimeZone;

public class WatchStatus {

    private User user;

    private OffsetDateTime watchedOn;

    public WatchStatus(User user, OffsetDateTime watchedOn) {
        this.user = user;
        this.watchedOn = watchedOn;
    }

    public User getUser() {
        return user;
    }

    public String getWatchedOn() {
        return watchedOn.format(Config.USER_FORMAT.withZone(TimeZone.getDefault().toZoneId()));
    }
}
