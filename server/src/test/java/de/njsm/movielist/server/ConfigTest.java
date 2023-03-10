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

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static de.njsm.movielist.server.Config.*;
import static org.junit.Assert.assertEquals;

public class ConfigTest {

    private Properties p;

    @Before
    public void setup() {
        p = new Properties();
        p.put(DB_ADDRESS_KEY, "localhost");
        p.put(DB_PORT_KEY, "1234");
        p.put(DB_NAME_KEY, "name");
        p.put(DB_CIRCUIT_BREAKER_TIMEOUT_KEY, "2000");
        p.put(POSTGRESQL_CONFIG_PREFIX + "user", "user");
        p.put(POSTGRESQL_CONFIG_PREFIX + "password", "password");
    }

    @Test
    public void testValidInitialisation() {
        Config uut = new Config(p);

        assertEquals("localhost", uut.getDbAddress());
        assertEquals("1234", uut.getDbPort());
        assertEquals("name", uut.getDbName());
        assertEquals(2000, uut.getCircuitBreakerTimeout());
        assertEquals("user", uut.getDbProperties().getProperty("user"));
        assertEquals("password", uut.getDbProperties().getProperty("password"));
    }

    @Test(expected = NumberFormatException.class)
    public void testInvalidNumberInCircuitBreakerTimeout() {
        p.put(DB_CIRCUIT_BREAKER_TIMEOUT_KEY, "invalidNumber");

        new Config(p);
    }
}