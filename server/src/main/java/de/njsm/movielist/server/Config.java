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

package de.njsm.movielist.server;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Component;

@Component
public class Config {

    private static final Logger LOG = LogManager.getLogger(Config.class);

    public static final DateTimeFormatter USER_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yy");

    static final String DB_ADDRESS_KEY = "de.njsm.movielist.server.db.host";

    static final String DB_PORT_KEY = "de.njsm.movielist.server.db.port";

    static final String DB_NAME_KEY = "de.njsm.movielist.server.db.name";

    static final String DB_CIRCUIT_BREAKER_TIMEOUT_KEY = "de.njsm.movielist.server.circuitbreaker.timeout";

    static final String HTTP_BASE_PATH = "de.njsm.movielist.server.web.base";

    static final String HTTP_STATIC_BASE_PATH = "de.njsm.movielist.server.web.staticbase";

    static final String POSTGRESQL_CONFIG_PREFIX = "de.njsm.movielist.server.db.postgres.";

    static final String LIQUIBASE_CONTEXTS = "de.njsm.movielist.server.db.liquibase.contexts";

    static final String ISSUER_URI = "de.njsm.movielist.server.oidc.issuer.uri";

    static final String CLIENT_ID = "de.njsm.movielist.server.oidc.client.id";

    static final String CLIENT_SECRET = "de.njsm.movielist.server.oidc.client.secret";

    static final String TOKEN_ALGORITHM = "de.njsm.movielist.server.oidc.token.algorithm";

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private String basePath;
    private String staticBasePath;
    private String liquibaseContexts;
    private int circuitBreakerTimeout;
    private Properties dbProperties;
    private String tokenAlgorithm;
    private String clientId;
    private String clientSecret;
    private String issuerUri;

    public Config(@Qualifier("propertiesFile") Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);
        basePath = p.getProperty(HTTP_BASE_PATH);
        staticBasePath = p.getProperty(HTTP_STATIC_BASE_PATH);
        liquibaseContexts = p.getProperty(LIQUIBASE_CONTEXTS);
        issuerUri = p.getProperty(ISSUER_URI);
        clientId = p.getProperty(CLIENT_ID);
        clientSecret = p.getProperty(CLIENT_SECRET);
        tokenAlgorithm = p.getProperty(TOKEN_ALGORITHM);

        String rawCircuitBreakerTimeout = p.getProperty(DB_CIRCUIT_BREAKER_TIMEOUT_KEY);
        try {
            circuitBreakerTimeout = Integer.parseInt(rawCircuitBreakerTimeout);
        } catch (NumberFormatException e) {
            LOG.error(DB_CIRCUIT_BREAKER_TIMEOUT_KEY + " is not an integer", e);
            throw e;
        }

        dbProperties = filterPostgresqlProperties(p);
    }

    @Bean("dbAddress")
    public String getDbAddress() {
        return dbAddress;
    }

    @Bean("dbPort")
    public String getDbPort() {
        return dbPort;
    }

    @Bean("dbName")
    public String getDbName() {
        return dbName;
    }

    @Bean("basePath")
    public String getBasePath() {
        return basePath;
    }

    @Bean("staticBasePath")
    public String getStaticBasePath() {
        return staticBasePath;
    }

    @Bean("liquibaseContexts")
    public String getLiquibaseContexts() {
        return liquibaseContexts;
    }

    @Bean("circuitBreakerTimeout")
    public int getCircuitBreakerTimeout() {
        return circuitBreakerTimeout;
    }

    @Bean("dbConfig")
    public Properties getDbProperties() {
        return dbProperties;
    }

    private Properties filterPostgresqlProperties(Properties config) {
        Properties result = new Properties();

        for (Map.Entry<Object, Object> entry: config.entrySet()) {
            if (entry.getKey() instanceof String &&
                    entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.startsWith(POSTGRESQL_CONFIG_PREFIX) &&
                        ! value.isEmpty()) {
                    result.put(
                            key.replace(POSTGRESQL_CONFIG_PREFIX, ""),
                            entry.getValue()
                    );
                }
            }
        }
        return result;
    }

    @Bean("algorithmResolver")
    public Function<ClientRegistration, JwsAlgorithm> getOidcTokenAlgorithm() {
        return clientRegistration -> {
            SignatureAlgorithm algorithm = SignatureAlgorithm.from(tokenAlgorithm);
            if (algorithm == null) {
                throw new RuntimeException("Invalid token algorithm " + tokenAlgorithm);
            }
            return algorithm;
        };
    }

    @Bean("client")
    public ClientRegistration getOidcClient() {
        return ClientRegistrations.fromOidcIssuerLocation(issuerUri)
                .registrationId("oauth2")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("openid", "profile")
                .userInfoAuthenticationMethod(AuthenticationMethod.HEADER)
                .userNameAttributeName("sub")
                .build();
    }
}
