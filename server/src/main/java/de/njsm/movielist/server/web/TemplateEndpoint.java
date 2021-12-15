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

package de.njsm.movielist.server.web;

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.OidcUser;
import de.njsm.movielist.server.business.data.User;
import fj.data.Validation;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.MediaType;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TemplateEndpoint {

    private static final Logger LOG = LogManager.getLogger(TemplateEndpoint.class);

    private final Configuration configuration;

    public TemplateEndpoint(Configuration configuration) {
        this.configuration = configuration;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response,
                                  AsyncResponse asyncResponse, String templateName) {
        processRequest(request, response, asyncResponse, templateName, Collections.emptyMap());
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response,
                                  AsyncResponse asyncResponse, String templateName, Map<String, Object> context) {
        processRequest(request, response, asyncResponse, templateName, Validation.success(context));
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response,
                                  AsyncResponse asyncResponse, String templateName, Validation<StatusCode, Map<String, Object>> businessContext) {
        try (PrintWriter drain = response.getWriter()) {

            Template template = configuration.getTemplate(templateName);
            User user = getUser(request);

            HashMap<String, Object> context = new HashMap<>();
            context.put("user", user);
            context.put("csrftoken", request.getAttribute("_csrf"));

            if (businessContext.isFail()) {
                int statusCode = businessContext.fail().toHttpStatus().getStatusCode();
                response.setStatus(statusCode);
                template = configuration.getTemplate("error_" + statusCode + ".html.ftl");
            } else {
                context.putAll(businessContext.success());
            }

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

    protected User getUser(HttpServletRequest request) {
        Principal user = request.getUserPrincipal();
        if (user instanceof User) {
            return (User) user;
        } else if (user instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) user;
            OidcUser oidcUser = (OidcUser) token.getPrincipal();
            return new User(oidcUser.getId(), oidcUser.getName(), "");
        } else {
            return new User();
        }
    }
}
