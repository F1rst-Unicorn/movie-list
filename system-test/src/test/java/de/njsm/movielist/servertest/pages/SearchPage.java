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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertTrue;

public class SearchPage extends LoggedInWebpage {

    private By submit;

    private By scopes;

    private By deleted;

    private By text;

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        submit = By.id("submit");
        scopes = By.id("id_choices");
        deleted = By.id("id_deleted_movies");
        text = By.id("id_text");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(submit));
        assertTrue(driver.findElement(submit).isDisplayed());
    }

    public SearchPage withScopes(int... i) {
        Select select = new Select(driver.findElement(scopes));
        select.deselectAll();
        for (int item : i) {
            select.selectByIndex(item);
        }
        return this;
    }

    public SearchPage withQuery(String query) {
        driver.findElement(text).clear();
        driver.findElement(text).sendKeys(query);
        return this;
    }

    public SearchPage withMissingMovies(boolean checked) {
        if (driver.findElement(deleted).isSelected() != checked) {
            driver.findElement(deleted).click();
        }
        return this;
    }

    public SearchResultPage search() {
        driver.findElement(submit).click();
        return new SearchResultPage(driver);
    }
}
