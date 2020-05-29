package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static junit.framework.TestCase.assertTrue;

public class MergeActorPage extends LoggedInWebpage {

    private By select;

    private By submit;

    public MergeActorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        select = By.id("id_actors");
        submit = By.id("submit");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(select));
        assertTrue(driver.findElement(select).isDisplayed());
        assertTrue(driver.findElement(submit).isDisplayed());
    }

    public MergeActorPage select(int index) {
        Select select = new Select(driver.findElement(this.select));
        select.selectByIndex(index);
        return this;
    }

    public ActorDetailPage submit() {
        driver.findElement(submit).click();
        return new ActorDetailPage(driver);
    }
}
