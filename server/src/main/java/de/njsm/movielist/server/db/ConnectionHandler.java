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
import de.njsm.movielist.server.util.HystrixWrapper;
import de.njsm.movielist.server.util.ProducerWithExceptions;
import fj.data.Validation;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHandler implements HystrixWrapper<Connection, SQLException> {

    private static final String SERIALISATION_FAILURE_SQL_STATE = "40001";

    private String resourceIdentifier;

    private ConnectionFactory connectionFactory;

    private int timeout;

    public ConnectionHandler(String resourceIdentifier,
                             ConnectionFactory connectionFactory,
                             int timeout) {
        this.resourceIdentifier = resourceIdentifier;
        this.connectionFactory = connectionFactory;
        this.timeout = timeout;
    }

    public StatusCode commit() {
        return runCommand(con -> {
            con.setAutoCommit(false);
            con.commit();
            con.close();
            connectionFactory.reset();
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode rollback() {
        return runCommand(con -> {
            con.setAutoCommit(false);
            con.rollback();
            con.close();
            connectionFactory.reset();
            return StatusCode.SUCCESS;
        });
    }

    public StatusCode setReadOnly() {
        return runCommand(con -> {
            con.setReadOnly(true);
            return StatusCode.SUCCESS;
        });
    }

    @Override
    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    @Override
    public int getCircuitBreakerTimeout() {
        return timeout;
    }

    @Override
    public StatusCode getDefaultErrorCode() {
        return StatusCode.DATABASE_UNREACHABLE;
    }

    @Override
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException> wrap(FunctionWithExceptions<Connection, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            try {
                Connection con = connectionFactory.getConnection();
                return client.apply(con);
            } catch (RuntimeException e) {
                return lookForSqlException(e);
            } catch (SQLException e) {
                if (isSerialisationConflict(e))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
                else
                    throw e;
            }
        };
    }

    static <O> Validation<StatusCode, O> lookForSqlException(RuntimeException e) throws RuntimeException {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof SQLException) {
                if (isSerialisationConflict((SQLException) cause))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
            }
            cause = cause.getCause();
        }
        throw e;
    }

    static boolean isSerialisationConflict(SQLException cause) {
        String sqlState = cause.getSQLState();

        if (sqlState != null && sqlState.equals(SERIALISATION_FAILURE_SQL_STATE)) {
            LOG.warn("Serialisation error, transaction was rolled back");
            return true;
        }

        return false;
    }
}
