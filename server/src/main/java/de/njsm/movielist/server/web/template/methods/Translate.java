package de.njsm.movielist.server.web.template.methods;

import de.njsm.movielist.util.locale.Messages_de;
import de.njsm.movielist.util.locale.Messages_en;
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
        if (args.size() != 1) {
            throw new TemplateModelException("Exactly one argument required");
        }
        return translate(args.get(0).toString(), Environment.getCurrentEnvironment().getLocale());
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
