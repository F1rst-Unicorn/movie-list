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

public class LoginPage extends Webpage {

    private By username;

    private By password;

    private By submit;

    private By errorLabel;

    LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        username = By.id("id_username");
        password = By.id("id_password");
        submit = By.id("id_submit");
        errorLabel = By.id("error");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(username));
        assertTrue(driver.findElement(username).isDisplayed());
        assertTrue(driver.findElement(password).isDisplayed());
        assertTrue(driver.findElement(submit).isDisplayed());
    }

    public static LoginPage test(WebDriver driver) {
        driver.navigate().to(System.getProperty("de.njsm.movielist.servertest.website"));
        return new LoginPage(driver);
    }

    public LoginPage loginWithWrongCredentials(String username, String password) {
        driver.findElement(this.username).sendKeys(username);
        driver.findElement(this.password).sendKeys(password);
        driver.findElement(this.submit).submit();
        waitUntil(ExpectedConditions.presenceOfElementLocated(errorLabel));

        assertTrue(driver.findElement(errorLabel).isDisplayed());
        return this;
    }

    public IndexPage login(String username, String password) {
        driver.findElement(this.username).sendKeys(username);
        driver.findElement(this.password).sendKeys(password);
        driver.findElement(this.submit).submit();
        return new IndexPage(driver);
    }
}
