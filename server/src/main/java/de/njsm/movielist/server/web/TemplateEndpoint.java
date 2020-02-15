package de.njsm.movielist.server.web;

import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.util.BiConsumerWithExceptions;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class TemplateEndpoint extends Endpoint {

    private static final Logger LOG = LogManager.getLogger(TemplateEndpoint.class);

    private Configuration configuration;

    public TemplateEndpoint(Configuration configuration) {
        this.configuration = configuration;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response,
                                  AsyncResponse asyncResponse, String templateName, BiConsumerWithExceptions<User, Map<String, Object>, Exception> logic) {
        try (PrintWriter drain = response.getWriter()) {
            Template template = configuration.getTemplate(templateName);
            User u = (User) request.getUserPrincipal();
            if (u == null) {
                u = new User();
            }

            HashMap<String, Object> context = new HashMap<>();
            context.put("user", u);
            context.put("csrftoken", request.getAttribute("_csrf"));
            logic.accept(u, context);

            response.setContentType(MediaType.TEXT_HTML);
            Environment env = template.createProcessingEnvironment(context, drain);
            env.setLocale(request.getLocale());
            env.process();
            asyncResponse.resume(new Object());
            drain.flush();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            asyncResponse.resume(new Object());
        }

    }


}
