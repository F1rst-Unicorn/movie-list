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

import de.njsm.movielist.server.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class BusinessObjectTest {

    private BusinessObject uut;

    private FailSafeDatabaseHandler backend;

    @Before
    public void setup() {
        backend = Mockito.mock(FailSafeDatabaseHandler.class);
        uut = new BusinessObject(backend);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(backend);
    }

    @Test
    public void committingWorks() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.finishTransaction(StatusCode.SUCCESS);

        assertEquals(StatusCode.SUCCESS, result);
        Mockito.verify(backend).commit();
    }

    @Test
    public void failingCommitIsPropagated() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        StatusCode result = uut.finishTransaction(StatusCode.SUCCESS);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(backend).commit();
    }

    @Test
    public void rollingBackWorks() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);

        StatusCode result = uut.finishTransaction(StatusCode.DATABASE_UNREACHABLE);

        assertEquals(StatusCode.DATABASE_UNREACHABLE, result);
        Mockito.verify(backend).rollback();
    }

    @Test
    public void committingWithResultWorks() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.success("yei"));

        assertTrue(result.isSuccess());
        Mockito.verify(backend).commit();
    }

    @Test
    public void failingCommitWithResultIsPropagated() {
        Mockito.when(backend.commit()).thenReturn(StatusCode.DATABASE_UNREACHABLE);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.success("yei"));

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).commit();
    }

    @Test
    public void rollingBackWithResultWorks() {
        Mockito.when(backend.rollback()).thenReturn(StatusCode.SUCCESS);

        Validation<StatusCode, String> result = uut.finishTransaction(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        assertTrue(result.isFail());
        assertEquals(StatusCode.DATABASE_UNREACHABLE, result.fail());
        Mockito.verify(backend).rollback();
    }

}