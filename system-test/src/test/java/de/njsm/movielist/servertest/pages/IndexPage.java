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

package de.njsm.movielist.servertest.pages;

import static junit.framework.TestCase.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IndexPage extends MovieListWebpage {

    private By headline;

    IndexPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        headline = By.tagName("h1");
    }

    @Override
    protected void assertPageContent() {
        assertTrue(driver.findElement(headline).isDisplayed());
    }

    public static IndexPage test(WebDriver driver) {
        driver.navigate().to(System.getProperty("de.njsm.movielist.servertest.website"));
        return new IndexPage(driver);
    }

    public IndexPage assertMovieList() {
        assertPageContent();
        return this;
    }
}
