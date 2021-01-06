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
    <#if edit>
        <#assign url = url("actor_edit", actor.id)>
        <#assign headline = translate("Edit actor")>
        <#assign submitLabel = translate("Save")>
    <#else>
        <#assign url = url("add_actor")>
        <#assign headline = translate("Add new actor")>
        <#assign submitLabel = translate("Add")>
    </#if>
    <div>
        <h2>${ headline }</h2>
    </div>
    <div>
        <form action="${ url }" method="post">
            <input class="btn btn-success" type="submit"
                   value="${ submitLabel }">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <div class="form-group">
                <label class="control-label"
                       for="id_firstname">${ translate("First name")}
                </label>
                <input type="text" name="firstname" class="form-control"
                       id="id_firstname" value="${ (actor.firstName)! }">
            </div>
            <div class="form-group">
                <label class="control-label"
                       for="id_lastname">${ translate("Surname")}
                </label>
                <input type="text" name="lastname" class="form-control"
                       id="id_lastname" value="${ (actor.lastName)! }">
            </div>
            <input class="btn btn-success" type="submit" id="submit"
                   value="${ submitLabel }">
        </form>
    </div>

</@base.base>
