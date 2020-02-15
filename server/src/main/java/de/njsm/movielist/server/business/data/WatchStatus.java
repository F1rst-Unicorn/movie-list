package de.njsm.movielist.server.business.data;

import de.njsm.movielist.server.Config;

import java.time.OffsetDateTime;
import java.util.TimeZone;

public class WatchStatus {

    private User user;

    private OffsetDateTime watchedOn;

    public WatchStatus(User user, OffsetDateTime watchedOn) {
        this.user = user;
        this.watchedOn = watchedOn;
    }

    public User getUser() {
        return user;
    }

    public String getWatchedOn() {
        return watchedOn.format(Config.USER_FORMAT.withZone(TimeZone.getDefault().toZoneId()));
    }
}
