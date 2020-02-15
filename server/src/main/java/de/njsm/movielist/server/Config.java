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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

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

    private String dbAddress;
    private String dbPort;
    private String dbName;
    private String basePath;
    private String staticBasePath;
    private int circuitBreakerTimeout;
    private Properties dbProperties;

    public Config(Properties p) {
        readProperties(p);
    }

    private void readProperties(Properties p) {
        dbAddress = p.getProperty(DB_ADDRESS_KEY);
        dbPort = p.getProperty(DB_PORT_KEY);
        dbName = p.getProperty(DB_NAME_KEY);
        basePath = p.getProperty(HTTP_BASE_PATH);
        staticBasePath = p.getProperty(HTTP_STATIC_BASE_PATH);

        String rawCircuitBreakerTimeout = p.getProperty(DB_CIRCUIT_BREAKER_TIMEOUT_KEY);
        try {
            circuitBreakerTimeout = Integer.parseInt(rawCircuitBreakerTimeout);
        } catch (NumberFormatException e) {
            LOG.error(DB_CIRCUIT_BREAKER_TIMEOUT_KEY + " is not an integer", e);
            throw e;
        }

        dbProperties = filterPostgresqlProperties(p);
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getBasePath() {
        return basePath;
    }

    public String getStaticBasePath() {
        return staticBasePath;
    }

    public int getCircuitBreakerTimeout() {
        return circuitBreakerTimeout;
    }

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
}
