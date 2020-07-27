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

import de.njsm.movielist.server.business.StatusCode;
import de.njsm.movielist.server.business.data.User;
import de.njsm.movielist.server.util.PasswordEncoder;
import fj.data.Validation;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.impl.DSL;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static de.njsm.movielist.server.db.jooq.Tables.AUTH_USER;

public class AuthHandler extends FailSafeDatabaseHandler implements UserDetailsService, AuthenticationProvider {

    public AuthHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.isAuthenticated())
            return authentication;

        Validation<StatusCode, Authentication> r = runFunction(context -> {

            Record5<Integer, String, String, String, byte[]> e = context.select(AUTH_USER.ID,
                    AUTH_USER.USERNAME,
                    DSL.field("(regexp_match(password, '^.*\\$(.*)\\$.*\\$.*$'))[1]", String.class).as("iterations"),
                    DSL.field("(regexp_match(password, '^.*\\$.*\\$(.*)\\$.*$'))[1]", String.class).as("salt"),
                    DSL.field("decode((regexp_match(password, '^.*\\$.*\\$.*\\$(.*)$'))[1], 'base64')", byte[].class).as("password"))
                    .from(AUTH_USER)
                    .where(AUTH_USER.USERNAME.eq(authentication.getName()))
                    .fetchOne();

            if (e == null)
                return Validation.fail(StatusCode.NOT_FOUND);

            PasswordEncoder p = new PasswordEncoder(Integer.parseInt(e.component3()),
                    e.component4(),
                    e.component5());

            if (p.matches(authentication.getCredentials())) {
                return Validation.success(new User(e.component1(), e.component2(), (String) authentication.getCredentials()));
            } else {
                return Validation.fail(StatusCode.ACCESS_DENIED);
            }
        });

        commit();
        if (r.isSuccess())
            return r.success();
        else if (r.fail() == StatusCode.NOT_FOUND) {
            throw new UsernameNotFoundException("");
        } else {
            throw new BadCredentialsException("");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Validation<StatusCode, UserDetails> r = runFunction(context -> {
            Record2<Integer, String> e = context.select(AUTH_USER.ID,
                    AUTH_USER.USERNAME)
                    .from(AUTH_USER)
                    .where(AUTH_USER.USERNAME.eq(username))
                    .fetchOne();

            commit();
            if (e == null)
                return Validation.fail(StatusCode.NOT_FOUND);
            else
                return Validation.success(new User(e.component1(), e.component2(), null));
        });

        if (r.isSuccess()) {
            return r.success();
        } else
            throw new UsernameNotFoundException("");
    }

    @Override
    public boolean supports(Class<?> c) {
        return c.equals(UsernamePasswordAuthenticationToken.class);
    }
}
