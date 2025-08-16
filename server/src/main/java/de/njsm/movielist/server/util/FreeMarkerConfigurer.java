/* movielist is client-server program to manage a household's food stock
 * Copyright (C) 2019  The movielist developers
 *
 * This file is part of the movielist program suite.
 *
 * movielist is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * movielist is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.movielist.server.util;

import static java.util.Map.entry;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import de.njsm.movielist.server.web.template.methods.Static;
import de.njsm.movielist.server.web.template.methods.Translate;
import de.njsm.movielist.server.web.template.methods.Url;
import freemarker.template.Configuration;

@Component
public class FreeMarkerConfigurer extends org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer {

    public FreeMarkerConfigurer(
        @Qualifier("freemarkerProperties") Properties freemarkerProperties,
        @Qualifier("basePath") String basePath,
        @Qualifier("staticBasePath") String staticBasePath
    ) {
        setTemplateLoaderPath("/WEB-INF/ftl/");
        setDefaultEncoding("UTF-8");
        setFreemarkerSettings(freemarkerProperties);
        setFreemarkerVariables(Map.of(
            "translate", new Translate(),
            "url", new Url(
                basePath,
                Map.ofEntries(
                    entry("index", ""),
                    entry("latest", "latest"),
                    entry("removed_movies", "to_delete"),
                    entry("absent_movies", "absent"),
                    entry("create_account", "create_account"),
                    entry("login", "login"),
                    entry("logout", "logout"),
                    entry("add_movie", "movies/add"),
                    entry("add_location", "add_location"),
                    entry("add_comment", "movies/%s/add_comment"),
                    entry("search", "search"),
                    entry("detail", "movies/%s/detail"),
                    entry("edit", "movies/%s/edit"),
                    entry("mark_removal", "movies/%s/mark_removal"),
                    entry("mark_watched", "movies/1234/mark_watched/5678"),
                    entry("delete", "movies/%s/delete"),
                    entry("delete_comment", "movies/%s/%s/delete"),
                    entry("actors", "actors"),
                    entry("add_actor", "actors/add"),
                    entry("actor_detail", "actors/%s/detail"),
                    entry("actor_edit", "actors/%s/edit"),
                    entry("actor_merge", "actors/%s/merge"),
                    entry("genres", "genres"),
                    entry("add_genre", "genres/add"),
                    entry("genre_detail", "genres/%s/detail"),
                    entry("genre_edit", "genres/%s/edit"),
                    entry("oidc", "oauth2/authorization/oauth2")
                )
            ),
            "static", new Static(staticBasePath)
        ));
    }

    @Override
    protected Configuration newConfiguration() {
        return new Configuration(Configuration.VERSION_2_3_30);
    }
}
