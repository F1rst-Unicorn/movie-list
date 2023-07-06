package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class EditMoviePage extends LoggedInWebpage {

    private By submit;

    private By name;

    private By description;

    private By year;

    private By location;

    private By link;

    private By actors;

    private By genres;

    public EditMoviePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        submit = By.id("submit");
        name = By.id("id_name");
        description = By.id("id_description");
        year = By.id("id_year");
        location = By.id("id_location");
        link = By.id("id_link");
        actors = By.id("id_actors");
        genres = By.id("id_genres");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(submit));
        waitUntil(ExpectedConditions.visibilityOf(driver.findElement(submit)));
        assertTrue(driver.findElement(submit).isDisplayed());
        assertTrue(driver.findElement(name).isDisplayed());
        assertTrue(driver.findElement(description).isDisplayed());
        assertTrue(driver.findElement(year).isDisplayed());
        assertTrue(driver.findElement(location).isDisplayed());
        assertTrue(driver.findElement(link).isDisplayed());
        assertTrue(driver.findElement(actors).isDisplayed());
        assertTrue(driver.findElement(genres).isDisplayed());
    }

    public EditMoviePage assertName(String name) {
        assertEquals(name, driver.findElement(this.name).getAttribute("value"));
        return this;
    }

    public EditMoviePage setName(String name) {
        driver.findElement(this.name).clear();
        driver.findElement(this.name).sendKeys(name);
        return this;
    }

    public EditMoviePage assertDescription(String description) {
        assertEquals(description, driver.findElement(this.description).getAttribute("value"));
        return this;
    }

    public EditMoviePage setDescription(String description) {
        driver.findElement(this.description).clear();
        driver.findElement(this.description).sendKeys(description);
        return this;
    }

    public EditMoviePage assertYear(int year) {
        assertEquals(String.valueOf(year), driver.findElement(this.year).getAttribute("value"));
        return this;
    }

    public EditMoviePage setYear(int year) {
        driver.findElement(this.year).clear();
        driver.findElement(this.year).sendKeys(String.valueOf(year));
        return this;
    }

    public EditMoviePage assertLocation(String location) {
        Select select = new Select(driver.findElement(this.location));
        assertEquals(location, select.getFirstSelectedOption().getText());
        return this;
    }

    public EditMoviePage setLocation(String location) {
        Select select = new Select(driver.findElement(this.location));
        select.selectByVisibleText(location);
        return this;
    }

    public EditMoviePage assertLink(String link) {
        assertEquals(link, driver.findElement(this.link).getAttribute("value"));
        return this;
    }

    public EditMoviePage setLink(String link) {
        waitUntil(v -> {
            driver.findElement(this.link).clear();
            driver.findElement(this.link).sendKeys(link);
            return true;
        });
        return this;
    }

    public EditMoviePage assertActors(String... actors) {
        Select select = new Select(driver.findElement(this.actors));
        assertEquals(actors.length, select.getAllSelectedOptions().size());

        for (String actor : actors) {
            assertTrue(select.getAllSelectedOptions().stream()
                    .anyMatch(i -> i.getText().equals(actor)));
        }

        return this;
    }

    public EditMoviePage setActors(int... indices) {
        Select select = new Select(driver.findElement(this.actors));
        select.deselectAll();
        for (int i : indices) {
            waitUntil(v -> {
                select.selectByIndex(i);
                return true;
            });
        }
        return this;
    }
    public EditMoviePage assertGenres(String... genres) {
        Select select = new Select(driver.findElement(this.genres));
        assertEquals(genres.length, select.getAllSelectedOptions().size());

        for (String genre : genres) {
            assertTrue(select.getAllSelectedOptions().stream()
                    .anyMatch(i -> i.getText().equals(genre)));
        }

        return this;
    }

    public EditMoviePage setGenres(int... indices) {
        Select select = new Select(driver.findElement(this.genres));
        waitUntil(v -> {
            select.deselectAll();
            return true;
        });
        for (int i : indices) {
            waitUntil(v -> {
                select.selectByIndex(i);
                return true;
            });
        }
        return this;
    }

    public MovieDetailPage submit() {
        waitUntil(v -> {
            driver.findElement(submit).click();
            return true;
        });
        return new MovieDetailPage(driver);
    }
}
