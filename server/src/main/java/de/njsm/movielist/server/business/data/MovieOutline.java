package de.njsm.movielist.server.business.data;

import org.jooq.Record7;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MovieOutline {

    private long id;

    private String name;

    private String location;

    private boolean watchedByUser;

    private boolean deleted;

    private boolean toDelete;

    private List<String> actors;

    public MovieOutline(long id, String name, String location, boolean watchedByUser, boolean deleted, boolean toDelete, List<String> actors) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.watchedByUser = watchedByUser;
        this.deleted = deleted;
        this.toDelete = toDelete;
        this.actors = actors;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public boolean isWatchedByUser() {
        return watchedByUser;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public List<String> getActors() {
        return actors;
    }

    public static class Spliterator implements java.util.Spliterator<MovieOutline> {

        private Iterator<Record7<Integer, String, String, Boolean, Boolean, Boolean, String>> i;

        private MovieOutline current;

        public Spliterator(Iterator<Record7<Integer, String, String, Boolean, Boolean, Boolean, String>> i) {
            this.i = i;
        }

        @Override
        public boolean tryAdvance(Consumer<? super MovieOutline> action) {
            while (i.hasNext()) {
                Record7<Integer, String, String, Boolean, Boolean, Boolean, String> r = i.next();
                if (current == null) {
                    resetCurrent(r);
                } else {
                    if (current.id == r.component1()) {
                        if (r.component7() != null)
                            current.getActors().add(r.component7());
                    } else {
                        action.accept(current);
                        resetCurrent(r);
                        return true;
                    }
                }
            }

            if (current != null) {
                action.accept(current);
                current = null;
                return true;
            } else {
                return false;
            }
        }

        private void resetCurrent(Record7<Integer, String, String, Boolean, Boolean, Boolean, String> r) {
            List<String> actors = new ArrayList<>();
            if (r.component7() != null)
                actors.add(r.component7());
            current = new MovieOutline(
                    r.component1(),
                    r.component2(),
                    r.component3(),
                    r.component4(),
                    r.component5(),
                    r.component6(),
                    actors);
        }

        @Override
        public java.util.Spliterator<MovieOutline> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return i.hasNext() ? Long.MAX_VALUE : 0;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL | IMMUTABLE;
        }
    }
}
