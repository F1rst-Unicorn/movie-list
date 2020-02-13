package de.njsm.movielist.server.web.template.methods;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.util.List;

public class Static implements TemplateMethodModelEx {

    private String basePath;

    public Static(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Exactly one argument required");
        }
        return basePath + "/" + args.get(0);
    }
}
