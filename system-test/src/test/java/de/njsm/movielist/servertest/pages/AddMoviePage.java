package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertTrue;

public class AddMoviePage extends LoggedInWebpage {

    private By submit;

    private By name;

    private By description;

    private By year;

    private By location;

    private By link;

    private By actors;

    private By genres;

    public AddMoviePage(WebDriver driver) {
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
        assertTrue(driver.findElement(submit).isDisplayed());
        assertTrue(driver.findElement(name).isDisplayed());
        assertTrue(driver.findElement(description).isDisplayed());
        assertTrue(driver.findElement(year).isDisplayed());
        assertTrue(driver.findElement(location).isDisplayed());
        assertTrue(driver.findElement(link).isDisplayed());
        assertTrue(driver.findElement(actors).isDisplayed());
        assertTrue(driver.findElement(genres).isDisplayed());
    }

    public AddMoviePage setName(String name) {
        driver.findElement(this.name).sendKeys(name);
        return this;
    }

    public AddMoviePage setDescription(String description) {
        driver.findElement(this.description).sendKeys(description);
        return this;
    }

    public AddMoviePage setYear(int year) {
        driver.findElement(this.year).clear();
        driver.findElement(this.year).sendKeys(String.valueOf(year));
        return this;
    }

    public AddMoviePage setLocation(String location) {
        Select select = new Select(driver.findElement(this.location));
        select.selectByVisibleText(location);
        return this;
    }

    public AddMoviePage setLink(String link) {
        driver.findElement(this.link).sendKeys(link);
        return this;
    }

    public AddMoviePage setActors(int... indices) {
        Select select = new Select(driver.findElement(this.actors));
        for (int i : indices) {
            select.selectByIndex(i);
        }
        return this;
    }

    public AddMoviePage setGenres(int... indices) {
        Select select = new Select(driver.findElement(this.genres));
        for (int i : indices) {
            select.selectByIndex(i);
        }
        return this;
    }

    public MovieDetailPage submit() {
        driver.findElement(submit).click();
        return new MovieDetailPage(driver);
    }
}
