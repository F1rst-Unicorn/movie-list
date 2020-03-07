package de.njsm.movielist.server.util;

import freemarker.template.Configuration;

public class FreeMarkerConfigurer extends org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer {

    @Override
    protected Configuration newConfiguration() {
        return new Configuration(Configuration.VERSION_2_3_30);
    }
}
