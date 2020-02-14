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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static de.njsm.movielist.server.db.jooq.Tables.AUTH_USER;

public class UserHandler extends FailSafeDatabaseHandler implements UserDetailsService, AuthenticationProvider {


    public UserHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
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
                return Validation.success(new User(e.component1(), e.component2()));
            } else {
                return Validation.fail(StatusCode.ACCESS_DENIED);
            }
        });


        if (r.isSuccess()) {
            return r.success();
        } else
            throw new BadCredentialsException("test");
    }

    @Override
    public boolean supports(Class<?> c) {
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Validation<StatusCode, UserDetails> r = runFunction(context -> {
            Record2<Integer, String> e = context.select(AUTH_USER.ID,
                    AUTH_USER.USERNAME)
                    .from(AUTH_USER)
                    .where(AUTH_USER.USERNAME.eq(username))
                    .fetchOne();

            if (e == null)
                return Validation.fail(StatusCode.NOT_FOUND);
            else
                return Validation.success(new User(e.component1(), e.component2()));
        });

        if (r.isSuccess()) {
            return r.success();
        } else
            throw new BadCredentialsException("test");
    }
}
