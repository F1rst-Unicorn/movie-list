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

public class ActorPage extends LoggedInWebpage {

    private By table;

    public ActorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        table = By.tagName("table");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(table));
        assertTrue(driver.findElement(table).isDisplayed());
    }

    public ActorPage assertNumberOfActors(int expected) {
        // +1 for headline row
        assertEquals(expected + 1, driver.findElement(table).findElements(By.xpath("//tr")).size());
        return this;
    }

    public ActorPage assertActor(int index, String name, int count) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());

        assertEquals(name, getTableRows().get(index)
                .findElements(By.tagName("td")).get(0).getText());
        assertEquals(String.valueOf(count), getTableRows().get(index)
                .findElements(By.tagName("td")).get(1).getText());
        return this;
    }

    public ActorDetailPage clickOnActor(int index) {
        index++;
        assertTrue(index >= 1);
        assertTrue(index < getTableRowCount());

        getTableRows().get(index).click();
        return new ActorDetailPage(driver);
    }

    private List<WebElement> getTableRows() {
        return driver.findElement(table).findElements(By.xpath("//tr"));
    }

    private int getTableRowCount() {
        return getTableRows().size();
    }
}
