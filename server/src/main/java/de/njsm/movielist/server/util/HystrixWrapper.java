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

package de.njsm.movielist.server.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import de.njsm.movielist.server.business.StatusCode;
import fj.data.Validation;

public interface HystrixWrapper<I, E extends Exception> {

    Logger LOG = LogManager.getLogger(HystrixWrapper.class);

    default StatusCode runCommand(FunctionWithExceptions<I, StatusCode, E> client) {
        Validation<StatusCode, StatusCode> result = runFunction(input -> Validation.fail(client.apply(input)));
        return result.fail();
    }

    default <O> Validation<StatusCode, O> runFunction(FunctionWithExceptions<I, Validation<StatusCode, O>, E> function) {
        HystrixProducer<I, Validation<StatusCode, O>, E> producer = new HystrixProducer<>(getResourceIdentifier(),
                getCircuitBreakerTimeout(),
                this::wrap,
                function);

        try {
            return producer.execute();
        } catch (HystrixRuntimeException e) {
            LOG.error("circuit breaker '{}' has error: {}", getResourceIdentifier(), e.getFailureType());

            if (e.getFailureType() == HystrixRuntimeException.FailureType.COMMAND_EXCEPTION ||
                e.getFailureType() == HystrixRuntimeException.FailureType.BAD_REQUEST_EXCEPTION)
                LOG.error("", e);
            else
                LOG.debug("", e);

            return Validation.fail(getDefaultErrorCode());
        }
    }

    String getResourceIdentifier();

    int getCircuitBreakerTimeout();

    StatusCode getDefaultErrorCode();

    <O> ProducerWithExceptions<Validation<StatusCode, O>, E>
    wrap(FunctionWithExceptions<I, Validation<StatusCode, O>, E> client);
}
