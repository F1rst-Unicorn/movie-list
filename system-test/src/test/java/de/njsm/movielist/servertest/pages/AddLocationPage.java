package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static junit.framework.TestCase.assertTrue;

public class AddLocationPage extends LoggedInWebpage {

    private By submit;

    private By name;

    private By index;

    public AddLocationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        submit = By.id("submit");
        name = By.id("id_name");
        index = By.id("id_index");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(submit));
        assertTrue(driver.findElement(submit).isDisplayed());
        assertTrue(driver.findElement(name).isDisplayed());
        assertTrue(driver.findElement(index).isDisplayed());
    }

    public AddLocationPage setName(String name) {
        driver.findElement(this.name).sendKeys(name);
        return this;
    }

    public AddLocationPage setIndex(String index) {
        driver.findElement(this.index).sendKeys(index);
        return this;
    }

    public IndexPage submit() {
        driver.findElement(submit).click();
        return new IndexPage(driver);
    }
}
