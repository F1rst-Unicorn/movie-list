package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MovieDetailPage extends LoggedInWebpage {


    private By title;

    private By description;

    private By year;

    private By location;

    private By link;

    private By actors;

    private By genres;

    private By delete;

    private By keep;

    private By absent;

    private By record;

    private By comments;

    private By comment;

    private By watched;

    public MovieDetailPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {
        title = By.id("title");
        description = By.id("description");
        year = By.id("year");
        location = By.id("location");
        link = By.id("link");
        actors = By.id("actors");
        genres = By.id("movie-genres");
        delete = By.id("delete");
        keep = By.id("keep");
        absent = By.id("make-absent");
        record = By.id("record");
        comments = By.id("comments");
        comment = By.id("comment");
        watched = By.id("Watched");
    }

    @Override
    protected void assertPageContent() {
        waitUntil(ExpectedConditions.presenceOfElementLocated(title));
        assertTrue(driver.findElement(title).isDisplayed());
    }

    public MovieDetailPage assertTitle(String title) {
        assertEquals(title, driver.findElement(this.title).getText());
        return this;
    }

    public MovieDetailPage assertDescription(String description) {
        assertEquals(description, driver.findElement(this.description).getText());
        return this;
    }

    public MovieDetailPage assertYear(int year) {
        assertEquals(String.valueOf(year), driver.findElement(this.year).getText());
        return this;
    }

    public MovieDetailPage assertLocation(String location) {
        assertEquals(location, driver.findElement(this.location).getText());
        return this;
    }

    public MovieDetailPage assertLink(String link) {
        WebElement href = driver.findElement(this.link);
        assertEquals(link, href.getAttribute("href"));
        return this;
    }

    public MovieDetailPage assertActors(String... actors) {
        WebElement list = driver.findElement(this.actors);
        for (String actor : actors) {
            assertTrue(list.findElements(By.xpath("//li")).stream()
                .anyMatch(item -> item.getText().equals(actor)));
        }
        return this;
    }

    public MovieDetailPage assertGenres(String... genres) {
        WebElement list = driver.findElement(this.genres);
        for (String genre : genres) {
            assertTrue(list.findElements(By.xpath(".//dd")).stream()
                    .anyMatch(item -> item.getText().equals(genre)));
        }
        return this;
    }

    public MovieDetailPage assertNotDeleted() {
        assertTrue(driver.findElement(delete).isDisplayed());
        return this;
    }

    public MovieDetailPage delete() {
        driver.findElement(delete).click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(absent));
        waitUntil(ExpectedConditions.visibilityOf(driver.findElement(absent)));
        waitUntil(ExpectedConditions.presenceOfElementLocated(keep));
        waitUntil(ExpectedConditions.visibilityOf(driver.findElement(keep)));
        return this;
    }

    public MovieDetailPage markAsKeep() {
        driver.findElement(keep).click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(delete));
        return this;
    }

    public MovieDetailPage markAsAbsent() {
        driver.findElement(absent).click();
        waitUntil(ExpectedConditions.presenceOfElementLocated(record));
        return this;
    }

    public MovieDetailPage recordAgain() {
        driver.findElement(record).click();
        return this;
    }

    public MovieDetailPage assertMarkDeletion() {
        assertTrue(driver.findElement(absent).isDisplayed());
        assertTrue(driver.findElement(keep).isDisplayed());
        return this;
    }

    public MovieDetailPage assertAbsent() {
        assertTrue(driver.findElement(record).isDisplayed());
        return this;
    }

    public MovieDetailPage assertNumberOfComments(int number) {
        List<WebElement> commentList = driver.findElement(comments).findElements(By.className("col"));
        assertEquals(number, commentList.size());
        return this;
    }

    public MovieDetailPage addComment(String comment) {
        driver.findElement(this.comment).sendKeys(comment);
        driver.findElement(By.id("submit")).click();
        return this;
    }

    public MovieDetailPage assertComment(int index, String comment) {
        List<WebElement> commentList = driver.findElement(comments).findElements(By.className("col"));
        assertEquals(comment, commentList.get(index).getText());
        return this;
    }

    public MovieDetailPage deleteComment(int index) {
        List<WebElement> commentList = driver.findElement(comments).findElements(By.tagName("form"));
        commentList.get(index).findElement(By.className("btn")).click();
        return this;
    }

    public MovieDetailPage assertWatched(String user) {
        assertTrue(driver.findElements(By.id("watchedBy"))
                .stream()
                .anyMatch(i -> i.getText().contains(user)));
        return this;
    }

    public MovieDetailPage unwatch(String user) {
        markWatched(user);
        return this;
    }

    public MovieDetailPage markWatched(String user) {
        driver.findElement(watched).click();
        driver.findElements(By.className("dropdown-item"))
                .stream()
                .filter(i -> i.getText().equals(user))
                .findFirst()
                .get()
                .click();
        return this;
    }

    public EditMoviePage edit() {
        driver.findElement(By.id("edit")).click();
        return new EditMoviePage(driver);
    }
}
