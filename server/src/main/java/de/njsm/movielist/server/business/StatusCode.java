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

package de.njsm.movielist.server.business;

import fj.data.Validation;

import javax.ws.rs.core.Response;

public enum StatusCode {

    SUCCESS {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.OK;
        }
    },

    GENERAL_ERROR {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    },

    NOT_FOUND {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.NOT_FOUND;
        }
    },

    INVALID_DATA_VERSION {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.BAD_REQUEST;
        }
    },

    FOREIGN_KEY_CONSTRAINT_VIOLATION {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.BAD_REQUEST;
        }
    },

    DATABASE_UNREACHABLE {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    },

    ACCESS_DENIED {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.UNAUTHORIZED;
        }
    },

    INVALID_ARGUMENT {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.BAD_REQUEST;
        }
    },

    CA_UNREACHABLE {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    },

    SERIALISATION_CONFLICT {
        @Override
        public Response.Status toHttpStatus() {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    };

    public abstract Response.Status toHttpStatus();

    public boolean isFail() {
        return this != SUCCESS;
    }

    public Validation<StatusCode, StatusCode> toValidation() {
        if (isFail())
            return Validation.fail(this);
        else
            return Validation.success(this);
    }

    public static StatusCode toCode(Validation<StatusCode, StatusCode> v) {
        if (v.isSuccess() && v.success() == SUCCESS)
            return v.success();
        else if (v.isFail() && v.fail() != SUCCESS)
            return v.fail();
        else
            throw new IllegalStateException("Validation and StatusCode contradict: " +  v);
    }

}
