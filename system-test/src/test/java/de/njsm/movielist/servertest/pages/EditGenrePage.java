package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class EditGenrePage extends LoggedInWebpage {

    private By name;

    private By submit;

    public EditGenrePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        name = By.id("id_name");
        submit = By.id("submit");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(name));
        assertTrue(driver.findElement(name).isDisplayed());
    }

    public EditGenrePage assertSuggestion(String name) {
        assertEquals(name, driver.findElement(this.name).getAttribute("value"));
        return this;
    }

    public EditGenrePage setName(String name) {
        driver.findElement(this.name).clear();
        driver.findElement(this.name).sendKeys(name);
        return this;
    }

    public GenreDetailPage submit() {
        driver.findElement(submit).click();
        return new GenreDetailPage(driver);
    }
}
