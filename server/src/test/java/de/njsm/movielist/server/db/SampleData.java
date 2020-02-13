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

package de.njsm.movielist.server.db;

import java.sql.*;

class SampleData {

    private static final String[] sampleDbData = {
            "DELETE FROM auth_user",
            "DELETE FROM movies_actor",
            "DELETE FROM movies_comment",
            "DELETE FROM movies_genre",
            "DELETE FROM movies_location",
            "DELETE FROM movies_movie",
            "DELETE FROM movies_movie_actors",
            "DELETE FROM movies_moviesingenre",
            "DELETE FROM movies_watchstatus",

            "ALTER SEQUENCE auth_user_id_seq RESTART",
            "ALTER SEQUENCE movies_actor_id_seq RESTART",
            "ALTER SEQUENCE movies_comment_id_seq RESTART",
            "ALTER SEQUENCE movies_genre_id_seq RESTART",
            "ALTER SEQUENCE movies_location_id_seq RESTART",
            "ALTER SEQUENCE movies_movie_actors_id_seq RESTART",
            "ALTER SEQUENCE movies_movie_id_seq RESTART",
            "ALTER SEQUENCE movies_moviesingenre_id_seq RESTART",
            "ALTER SEQUENCE movies_watchstatus_id_seq RESTART",

    };

    static void insertSampleData(Connection c) throws SQLException {
        Statement stmt = c.createStatement();

        for (String cmd : sampleDbData) {
            stmt.execute(cmd);
        }
    }

}
