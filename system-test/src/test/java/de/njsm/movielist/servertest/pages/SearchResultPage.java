package de.njsm.movielist.servertest.pages;

import org.openqa.selenium.WebDriver;

public class SearchResultPage extends MovieListWebpage {

    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initialise() {}

    @Override
    protected void assertPageContent() {}

    public SearchPage search() {
        return new SearchPage(driver);
    }
}
