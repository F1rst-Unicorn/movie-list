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

package de.njsm.movielist.server.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Repository;
import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.User;
import fj.data.Validation;

@Repository
public class OidcUserService extends FailSafeDatabaseHandler implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final Logger LOG = LogManager.getLogger(OidcUserService.class);

    private final org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService delegate =
            new org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService();

    private final UserHandler userHandler;

    private final ConnectionHandler connectionHandler;

    public OidcUserService(
        @Qualifier("persistentConnectionFactory") ConnectionFactory connectionFactory,
        @Qualifier("circuitBreakerDatabase") String resourceIdentifier,
        int timeout,
        @Qualifier("persistentUserBackend") UserHandler userHandler
    ) {
        super(connectionFactory, resourceIdentifier, timeout);
        this.userHandler = userHandler;
        this.connectionHandler = new ConnectionHandler(resourceIdentifier, connectionFactory, timeout);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = delegate.loadUser(userRequest);

        String name = user.getGivenName();
        if (name == null || name.isEmpty()) {
            name = user.getSubject();
        }

        Validation<StatusCode, User> result = userHandler.getOrCreate(name);
        StatusCode commitResult = connectionHandler.commit();
        if (result.isFail() || commitResult.isFail()) {
            LOG.error("Getting or creating user failed with " + result.fail().name() + " / " + commitResult.name());
            return null;
        }

        User dbUser = result.success();
        return new de.njsm.movielist.server.business.data.OidcUser(user.getAuthorities(), user.getIdToken(), user.getUserInfo(), dbUser.getId(), dbUser.getName());
    }
}
