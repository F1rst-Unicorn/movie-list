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
import de.njsm.movielist.server.util.FunctionWithExceptions;
import de.njsm.movielist.server.util.HystrixProducer;
import fj.data.Validation;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertFalse;

public class FailSafeDatabaseHandlerTest extends DbTestCase {

    private FailSafeDatabaseHandler uut;

    @Before
    public void setup() throws SQLException {
        Connection c = getConnectionFactory().getConnection();
        c.setAutoCommit(false);
        uut = new FailSafeDatabaseHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
    }

    @Test
    public void exceptionReturnsErrorCode() {
        StatusCode result = uut.runCommand(con -> {
            throw new SQLException("test");
        });

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
    }

    @Test
    public void commandErrorCodesArePropagated() {
        StatusCode expected = StatusCode.INVALID_DATA_VERSION;

        StatusCode actual = uut.runCommand(context -> expected);

        assertEquals(expected, actual);
    }


    @Test
    public void openCircuitBreaker() throws InterruptedException {
        FunctionWithExceptions<DSLContext, StatusCode, SQLException> input = (con) -> {
            throw new SQLException("test");
        };

        uut.runCommand(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void openCircuitBreakerWithUncheckedException() throws InterruptedException {
        FunctionWithExceptions<DSLContext, StatusCode, SQLException> input = (con) -> {
            throw new RuntimeException("test");
        };

        uut.runCommand(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void openCircuitBreakerWithTimeout() throws InterruptedException {
        int timeout = 100;
        FailSafeDatabaseHandler uut = new FailSafeDatabaseHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                timeout);

        FunctionWithExceptions<DSLContext, StatusCode, SQLException> input = (con) -> {
            try {
                Thread.sleep(timeout * 2);
            } catch (InterruptedException e) {
                fail();
            }
            return StatusCode.SUCCESS;
        };

        uut.runCommand(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void openCircuitBreakerInNewApi() throws InterruptedException {
        FunctionWithExceptions<DSLContext, Validation<StatusCode, String>, SQLException> input = (con) -> {
            throw new DataAccessException("test");
        };

        uut.runFunction(input);
        Thread.sleep(500);      // hystrix window has to shift

        Assert.assertTrue(uut.isCircuitBreakerOpen());
    }

    @Test
    public void testCommitting() throws SQLException {
        Connection con = getConnectionFactory().getConnection();

        StatusCode result = uut.commit();

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(con.isClosed());
    }

    @Test
    public void testRollingBack() throws SQLException {
        Connection con = getConnectionFactory().getConnection();

        StatusCode result = uut.rollback();

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(con.isClosed());
    }

    @Test
    public void testSettingReadOnly() throws SQLException {

        StatusCode result = uut.setReadOnly();

        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(getConnectionFactory().getConnection().isReadOnly());
    }

    /**
     * Example taken from https://www.postgresql.org/docs/11/transaction-iso.html#XACT-SERIALIZABLE
     */
    @Test
    public void serialisationErrorIsNoted() throws Exception {
        Connection concurrentConnection = DbTestCase.createConnection();
        concurrentConnection.setAutoCommit(false);
        concurrentConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        Statement statement = concurrentConnection.createStatement();
        statement.execute("drop table if exists concurrency_test");
        statement.execute("create table concurrency_test (class int not null, value int not null);" +
                "insert into concurrency_test (class, value) values (1, 10);" +
                "insert into concurrency_test (class, value) values (1, 20);" +
                "insert into concurrency_test (class, value) values (2, 100);" +
                "insert into concurrency_test (class, value) values (2, 200);");
        concurrentConnection.commit();

        StatusCode commandCode = uut.runCommand(context -> {
           context.execute("select sum(value) from concurrency_test where class = 1");
           context.execute("insert into concurrency_test (class, value) values (2, 30)");
           return StatusCode.SUCCESS;
        });

        statement.execute("select sum(value) from concurrency_test where class = 2");
        statement.execute("insert into concurrency_test (class, value) values (1, 300)");
        concurrentConnection.commit();

        StatusCode commitStatusCode = uut.commit();

        statement.execute("drop table concurrency_test");
        concurrentConnection.commit();
        concurrentConnection.close();

        assertEquals(StatusCode.SUCCESS, commandCode);
        assertEquals(StatusCode.SERIALISATION_CONFLICT, commitStatusCode);
        Thread.sleep(500);      // hystrix window has to shift
        assertFalse(new HystrixProducer<>(getNewResourceIdentifier(), CIRCUIT_BREAKER_TIMEOUT, null, null).isCircuitBreakerOpen());
    }
}