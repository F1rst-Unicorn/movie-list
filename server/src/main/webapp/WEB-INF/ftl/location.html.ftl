<#--
    movie-list is a website to manage a large list of movies
    Copyright (C) 2019  The movie-list developers

    This file is part of the movie-list program suite.

    movie-list is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    movie-list is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see https://www.gnu.org/licenses/.
-->
<#import "base.html.ftl" as base>
<@base.base>
    <div>
        <h2>${ translate("Add new location") }</h2>
    </div>
    <div>
        <form action="${ url("add_location") }" method="post">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <div class="form-group">
                <label class="control-label"
                       for="id_name">${ translate("Name")}
                </label>
                <input type="text" name="name" class="form-control"
                       id="id_name">
            </div>
            <div class="form-group">
                <label class="control-label"
                       for="id_index">${ translate("Name")}
                </label>
                <input type="number" name="index" class="form-control"
                       id="id_index">
            </div>
            <input class="btn btn-success" type="submit"
                   value="${ translate("Add") }">
        </form>
    </div>

</@base.base>