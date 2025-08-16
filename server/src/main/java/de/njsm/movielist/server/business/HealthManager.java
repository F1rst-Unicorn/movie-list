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

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import de.njsm.movielist.server.business.data.Health;
import de.njsm.movielist.server.db.HealthHandler;
import fj.data.Validation;

@Service
@RequestScope
public class HealthManager extends BusinessObject {

    private HealthHandler dbBackend;

    public HealthManager(HealthHandler dbBackend) {
        super(dbBackend);
        this.dbBackend = dbBackend;
    }

    public Validation<StatusCode, Health> get() {
        dbBackend.setReadOnly();

        StatusCode db = dbBackend.get();
        StatusCode dbCommit = dbBackend.commit();

        return Validation.success(
                new Health(db == StatusCode.SUCCESS && dbCommit == StatusCode.SUCCESS));
    }
}
