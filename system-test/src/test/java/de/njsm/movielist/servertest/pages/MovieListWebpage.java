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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public abstract class MovieListWebpage extends LoggedInWebpage {

    protected By table;

    private By delete;

    private By keep;

    private By absent;

    private By record;

    public MovieListWebpage(WebDriver driver) {
        super(driver);
        init();
        assertPage();
    }

    private void init() {
        table = By.tagName("table");
        delete = By.id("delete");
        keep = By.id("keep");
        absent = By.id("absent");
        record = By.id("record");
    }

    private void assertPage() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(table));
        assertTrue(driver.findElement(table).isDisplayed());
    }

    public MovieListWebpage assertNumberOfMovies(int expected) {
        // +1 for headline row
        assertEquals(expected, getTableRowCount() - 1);
        return this;
    }

    public MovieListWebpage assertMovieTitle(int index, String title) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());

        assertEquals(title, getTableRows().get(index)
                .findElements(By.tagName("td")).get(0).getText());
        return this;
    }

    public MovieListWebpage assertMovieLocation(int index, String location) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());

        assertEquals(location, getTableRows().get(index)
                .findElements(By.tagName("td")).get(2).getText());
        return this;
    }

    private List<WebElement> getTableRows() {
        return driver.findElement(table).findElements(By.xpath("//tr"));
    }

    private int getTableRowCount() {
        return getTableRows().size();
    }

    public MovieDetailPage goToMovie(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());
        WebElement movie = getTableRows().get(index);
        movie.findElements(By.tagName("td")).get(0).click();
        return new MovieDetailPage(driver);
    }

    public MovieListWebpage deleteMovie(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());
        getTableRows().get(index).findElement(delete).click();
        return this;
    }

    public MovieListWebpage keepMovie(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());
        getTableRows().get(index).findElement(keep).click();
        return this;
    }

    public MovieListWebpage markAbsent(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());
        getTableRows().get(index).findElement(absent).click();
        return this;
    }

    public MovieListWebpage recordAgain(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());
        getTableRows().get(index).findElement(record).click();
        return this;
    }

    public MovieListWebpage markSeen(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());

        WebElement element = getTableRows().get(index).findElement(By.tagName("a"));
        element.click();
        waitUntil(ExpectedConditions.invisibilityOf(element));
        return this;
    }
}
