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

public abstract class LoggedInWebpage extends Webpage {

    private By logoutButton;

    public LoggedInWebpage(WebDriver driver) {
        super(driver);
        init();
        assertLoggedIn();
    }

    private void init() {
        logoutButton = By.id("logout");
    }

    private void assertLoggedIn() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(logoutButton));
        assertTrue(driver.findElement(logoutButton).isDisplayed());
    }

    public LoginPage logout() {
        driver.findElement(logoutButton).submit();
        return new LoginPage(driver);
    }

    public IndexPage goToHome() {
        driver.findElement(By.id("home")).click();
        return new IndexPage(driver);
    }

    public IndexPage goToLatest() {
        driver.findElement(By.id("nav-dropdown")).click();
        driver.findElement(By.id("latest")).click();
        return new IndexPage(driver);
    }

    public IndexPage goToRemoved() {
        driver.findElement(By.id("mgmt-dropdown")).click();
        driver.findElement(By.id("removed")).click();
        return new IndexPage(driver);
    }

    public IndexPage goToAbsent() {
        driver.findElement(By.id("mgmt-dropdown")).click();
        driver.findElement(By.id("absent")).click();
        return new IndexPage(driver);
    }

    public SearchPage goToSearch() {
        driver.findElement(By.id("search")).click();
        return new SearchPage(driver);
    }

    public ActorPage goToActors() {
        driver.findElement(By.id("nav-dropdown")).click();
        driver.findElement(By.id("actors")).click();
        return new ActorPage(driver);
    }

    public GenrePage goToGenres() {
        driver.findElement(By.id("nav-dropdown")).click();
        driver.findElement(By.id("genres")).click();
        return new GenrePage(driver);
    }

    public AddActorPage goToAddActor() {
        driver.findElement(By.id("add-dropdown")).click();
        driver.findElement(By.id("add-actor")).click();
        return new AddActorPage(driver);
    }

    public AddGenrePage goToAddGenre() {
        driver.findElement(By.id("add-dropdown")).click();
        driver.findElement(By.id("add-genre")).click();
        return new AddGenrePage(driver);
    }

    public AddLocationPage goToAddLocation() {
        driver.findElement(By.id("add-dropdown")).click();
        driver.findElement(By.id("add-location")).click();
        return new AddLocationPage(driver);
    }

    public AddMoviePage goToAddMovie() {
        driver.findElement(By.id("add-dropdown")).click();
        driver.findElement(By.id("add-movie")).click();
        return new AddMoviePage(driver);
    }
}
