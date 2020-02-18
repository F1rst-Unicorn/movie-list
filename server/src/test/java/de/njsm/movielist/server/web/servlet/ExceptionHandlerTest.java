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

package de.njsm.movielist.server.web.servlet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

public class ExceptionHandlerTest {

    private HttpServletRequest request;

    private HttpServletResponse response;

    private Exception exception;

    private ExceptionHandler uut;

    @Before
    public void setup() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        exception = new Exception("test");

        uut = new ExceptionHandler();

        Mockito.when(request.getAttribute(ExceptionHandler.EXCEPTION_KEY)).thenReturn(exception);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(request);
        Mockito.verifyNoMoreInteractions(response);
    }

    @Test
    public void testGet() {
        String result = uut.get(request, response);

        assertEquals("General Error", result);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testPut() {
        String result = uut.put(request, response);

        assertEquals("General Error", result);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testDelete() {
        String result = uut.delete(request, response);

        assertEquals("General Error", result);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }

    @Test
    public void testPost() {
        String result = uut.post(request, response);

        assertEquals("General Error", result);
        Mockito.verify(response).setStatus(500);
        Mockito.verify(request).getAttribute(ExceptionHandler.EXCEPTION_KEY);
    }
}