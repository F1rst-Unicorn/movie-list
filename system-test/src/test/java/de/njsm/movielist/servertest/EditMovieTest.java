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

public class EditMovieTest {

    @Test
    public void test() {
        IndexPage.test(TestSuite.DRIVER)
                .goToMovie(0)
                .edit()

                .assertName("Inception")
                .setName("Inception 2")
                .assertDescription("Great")
                .setDescription("Very Great")
                .assertYear(2000)
                .setYear(2001)
                .assertLocation("DVD")
                .setLocation("Archive")
                .assertLink("https://en.wikipedia.org/wiki/Inception")
                .setLink("https://wikipedia.org/")
                .assertActors()
                .setActors(1)
                .assertGenres("Thriller")
                .setGenres(2, 3)
                .submit()

                .assertTitle("Inception 2")
                .assertDescription("Very Great")
                .assertYear(2001)
                .assertLocation("Archive")
                .assertLink("https://wikipedia.org/")
                .assertActors("Nina Hoss")
                .assertGenres("Fantasy", "Drama");
    }
}
