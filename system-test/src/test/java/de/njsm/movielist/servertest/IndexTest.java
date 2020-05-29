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

package de.njsm.movielist.servertest;

import de.njsm.movielist.servertest.pages.IndexPage;
import org.junit.Test;

public class IndexTest {

    @Test
    public void checkPresenceOfTestData() {
        IndexPage.test(TestSuite.DRIVER)
                .assertNumberOfMovies(4)
                .goToActors()
                .assertNumberOfActors(4)
                .assertActor(0, "Judy Dench", 0)
                .assertActor(1, "Natalie Portman", 1)
                .assertActor(2, "Meg Ryan", 0)
                .assertActor(3, "Kate Winslet", 1)
                .goToGenres()
                .assertNumberOfGenres(4)
                .assertGenre(0, "Crime", 0)
                .assertGenre(1, "Documentation", 0)
                .assertGenre(2, "Fantasy", 1)
                .assertGenre(3, "Thriller", 2)
                .goToHome()
                .assertMovieTitle(0, "Inception")
                .assertMovieLocation(0, "DVD")
                .assertMovieTitle(1, "Lord of the Rings")
                .assertMovieLocation(1, "DVD")
                .assertMovieTitle(2, "Revolutionary Road")
                .assertMovieLocation(2, "Box 3")
                .assertMovieTitle(3, "V for Vendetta")
                .assertMovieLocation(3, "Blu-Ray")
                .goToLatest()
                .assertMovieTitle(0, "V for Vendetta")
                .assertMovieLocation(0, "Blu-Ray")
                .assertMovieTitle(1, "Lord of the Rings")
                .assertMovieLocation(1, "DVD")
                .assertMovieTitle(2, "Inception")
                .assertMovieLocation(2, "DVD")
                .assertMovieTitle(3, "Revolutionary Road")
                .assertMovieLocation(3, "Box 3")
                .goToRemoved()
                .assertNumberOfMovies(1)
                .assertMovieTitle(0, "Eragon")
                .goToAbsent()
                .assertNumberOfMovies(1)
                .assertMovieTitle(0, "Jumper");
    }
}
