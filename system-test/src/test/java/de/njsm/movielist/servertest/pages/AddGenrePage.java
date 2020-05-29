package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static junit.framework.TestCase.assertTrue;

public class AddGenrePage extends LoggedInWebpage {

    private By submit;

    private By name;

    public AddGenrePage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        submit = By.id("submit");
        name = By.id("id_name");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(submit));
        assertTrue(driver.findElement(name).isDisplayed());
        assertTrue(driver.findElement(submit).isDisplayed());
    }


    public AddGenrePage setName(String name) {
        driver.findElement(this.name).sendKeys(name);
        return this;
    }

    public IndexPage submit() {
        driver.findElement(submit).click();
        return new IndexPage(driver);
    }
}
