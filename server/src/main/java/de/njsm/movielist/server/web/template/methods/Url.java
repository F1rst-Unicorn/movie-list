package de.njsm.movielist.server.web.template.methods;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Url implements TemplateMethodModelEx {

    private String basePath;

    private Map<String, String> urlMap;

    public Url(String basePath, Map<String, String> urlMap) {
        this.basePath = basePath;
        this.urlMap = urlMap;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() < 1) {
            throw new TemplateModelException("At least one argument required");
        }

        String urlName = args.get(0).toString();
        String urlTemplate = urlMap.get(urlName);
        if (urlTemplate != null)
            switch (args.size()) {
            case 1:
                return basePath + "/" + urlMap.get(urlName);
            case 2:
                return basePath + "/" + String.format(Locale.US, urlMap.get(urlName), args.get(1).toString());
            case 3:
                return basePath + "/" + String.format(Locale.US, urlMap.get(urlName), args.get(1).toString(), args.get(2).toString());
            default:
                throw new TemplateModelException("You must support more arguments");
        }
        else
            throw new TemplateModelException("URL for name '" + urlName + "' not found");
    }
}
