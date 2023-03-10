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
import de.njsm.movielist.server.util.HystrixWrapper;
import de.njsm.movielist.server.util.ProducerWithExceptions;
import fj.data.Validation;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

public class FailSafeDatabaseHandler implements HystrixWrapper<DSLContext, SQLException> {

    private String resourceIdentifier;

    private final int timeout;

    private ConnectionFactory connectionFactory;

    public FailSafeDatabaseHandler(ConnectionFactory connectionFactory,
                                   String resourceIdentifier,
                                   int timeout) {
        this.resourceIdentifier = resourceIdentifier;
        this.timeout = timeout;
        this.connectionFactory = connectionFactory;
    }

    boolean isCircuitBreakerOpen() {
        return new HystrixProducer<>(resourceIdentifier, 1000, null, null)
                .isCircuitBreakerOpen();
    }

    public StatusCode commit() {
        return new ConnectionHandler(resourceIdentifier + "-cleanup", connectionFactory, 10000).commit();
    }

    public StatusCode rollback() {
        return new ConnectionHandler(resourceIdentifier + "-cleanup", connectionFactory, 10000).rollback();
    }

    public StatusCode setReadOnly() {
        return new ConnectionHandler(resourceIdentifier, connectionFactory, timeout).setReadOnly();
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
    public <O> ProducerWithExceptions<Validation<StatusCode, O>, SQLException>
    wrap(FunctionWithExceptions<DSLContext, Validation<StatusCode, O>, SQLException> client) {
        return () -> {
            try {
                Connection con = connectionFactory.getConnection();
                con.setAutoCommit(false);
                if (con.getTransactionIsolation() != Connection.TRANSACTION_SERIALIZABLE)
                    con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                DSLContext context = DSL.using(con, SQLDialect.POSTGRES);
                return client.apply(context);
            } catch (RuntimeException e) {
                return ConnectionHandler.lookForSqlException(e);
            } catch (SQLException e) {
                if (ConnectionHandler.isSerialisationConflict(e))
                    return Validation.fail(StatusCode.SERIALISATION_CONFLICT);
                else
                    throw e;
            }

        };
    }


}
