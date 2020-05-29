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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ActorDetailPage extends MovieListWebpage {

    private By title;

    private By edit;

    private By merge;

    public ActorDetailPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        title = By.id("title");
        edit = By.id("edit");
        merge = By.id("merge");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(title));
        assertTrue(driver.findElement(title).isDisplayed());
    }

    public ActorDetailPage assertTitle(String name) {
        assertEquals(name, driver.findElement(title).getText());
        return this;
    }

    public EditActorPage edit() {
        driver.findElement(edit).click();
        return new EditActorPage(driver);
    }

    public MergeActorPage merge() {
        driver.findElement(merge).click();
        return new MergeActorPage(driver);
    }
}
