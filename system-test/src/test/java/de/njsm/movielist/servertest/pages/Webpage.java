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

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class Webpage {

    protected WebDriver driver;

    public Webpage(WebDriver driver) {
        this.driver = driver;
        initialise();
        assertPageContent();
    }

    protected <T> T waitUntil(ExpectedCondition<T> condition) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .ignoring(ElementNotInteractableException.class)
                .until(condition);
    }

    /**
     * Do constructor work here
     */
    protected abstract void initialise();

    /**
     * Each page has to have a check verifying that we are really on its page
     */
    protected abstract void assertPageContent();
}
