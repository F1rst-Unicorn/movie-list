package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class EditActorPage extends LoggedInWebpage {

    private By firstName;

    private By lastName;

    public EditActorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        firstName = By.id("id_firstname");
        lastName = By.id("id_lastname");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(firstName));
        assertTrue(driver.findElement(firstName).isDisplayed());
        assertTrue(driver.findElement(lastName).isDisplayed());
    }

    public EditActorPage assertSuggestion(String firstName, String lastName) {
        assertEquals(firstName, driver.findElement(this.firstName).getAttribute("value"));
        assertEquals(lastName, driver.findElement(this.lastName).getAttribute("value"));
        return this;
    }

    public EditActorPage setName(String firstName, String lastName) {
        driver.findElement(this.firstName).clear();
        driver.findElement(this.firstName).sendKeys(firstName);
        driver.findElement(this.lastName).clear();
        driver.findElement(this.lastName).sendKeys(lastName);
        return this;
    }

    public ActorDetailPage submit() {
        driver.findElement(By.id("submit")).click();
        return new ActorDetailPage(driver);
    }
}
