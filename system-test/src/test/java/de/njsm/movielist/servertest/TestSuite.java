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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginTest.class,
        IndexTest.class,
        AddActorTest.class,
        AddGenreTest.class,
        AddLocationTest.class,
        AddMovieTest.class,
        MovieLifecycleTest.class,
        SearchTest.class,
        CommentTest.class,
        MarkWatchedTest.class,
        EditActorTest.class,
        EditGenreTest.class,
        EditMovieTest.class,
        MergeActorTest.class,
        LogoutTest.class,
})
public class TestSuite {
    public static WebDriver DRIVER;

    @BeforeClass
    public static void beforeClass() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("intl.accept_languages", "de");
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.setProfile(profile);
        DRIVER = new FirefoxDriver(options);
    }

    @AfterClass
    public static void afterClass() {
        DRIVER.quit();
    }
}
