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

package de.njsm.movielist.server.web.template.methods;

import de.njsm.movielist.server.util.locale.Messages_de;
import de.njsm.movielist.server.util.locale.Messages_en;
import freemarker.core.Environment;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Translate implements TemplateMethodModelEx {

    private static final Logger LOG = LogManager.getLogger(Translate.class);

    private Map<String, ResourceBundle> translations;

    public Translate() {
        translations = new HashMap<>();
        translations.put("en", new Messages_en());
        translations.put("de", new Messages_de());
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() < 1) {
            throw new TemplateModelException("Exactly one argument required");
        }
        String translation = translate(args.get(0).toString(), Environment.getCurrentEnvironment().getLocale());

        switch (args.size()) {
            case 1:
                return translation;
            case 2:
                return String.format(Locale.US, translation, args.get(1).toString());
            case 3:
                return String.format(Locale.US, translation, args.get(1).toString(), args.get(2).toString());
            default:
                throw new TemplateModelException("You must support more arguments");
        }
    }

    public String translate(String key, Locale locale) {
        ResourceBundle language = translations.get(locale.getLanguage());
        if (language == null) {
            LOG.error("Unknown language " + locale.getLanguage());
            return key;
        }

        String translation = key;
        try {
            translation = language.getString(key);
        } catch (MissingResourceException e) {
            LOG.error("No translation for key '" + key + "' in " + locale.getLanguage());
        }
        return translation;
    }
}
