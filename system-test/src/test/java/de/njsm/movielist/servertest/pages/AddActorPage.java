package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static junit.framework.TestCase.assertTrue;

public class AddActorPage extends LoggedInWebpage {

    private By submit;

    private By firstName;

    private By lastName;

    public AddActorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        submit = By.id("submit");
        firstName = By.id("id_firstname");
        lastName = By.id("id_lastname");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(submit));
        assertTrue(driver.findElement(submit).isDisplayed());
        assertTrue(driver.findElement(firstName).isDisplayed());
        assertTrue(driver.findElement(lastName).isDisplayed());
    }

    public AddActorPage setFirstName(String name) {
        driver.findElement(firstName).sendKeys(name);
        return this;
    }

    public AddActorPage setLastName(String name) {
        driver.findElement(lastName).sendKeys(name);
        return this;
    }

    public IndexPage submit() {
        driver.findElement(submit).click();
        return new IndexPage(driver);
    }
}
