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
    <h1>${ translate("Login") }</h1>
    <#if error>
        <div class="alert alert-danger"
             role="alert">${ translate("Username or password wrong!")}</div>
    </#if>
    <form action="${ url("login") }" method="post">
        <div class="form-group">
            <label class="control-label"
                   for="id_username">${ translate("Username") }</label>
            <input id="id_username" class="form-control" autofocus=""
                   autocapitalize="none" autocomplete="username" maxlength="150"
                   required="" type="text" name="username"/>
        </div>
        <div class="form-group">
            <label class="control-label"
                   for="id_password">${ translate("Password") }</label>
            <input id="id_password" class="form-control"
                   autocomplete="current-password" required="" type="password"
                   name="password"/>
        </div>
        <input type="hidden" name="${ csrftoken.parameterName }"
               value="${ csrftoken.token }"/>
        <input type="hidden" name="remember-me"
               value="1"/>
        <input class="btn btn-success" type="submit"
               value="${ translate("Login") }">
    </form>
    <p><a class="btn btn-primary"
          href="${ url('create_account') }">${ translate("Create account") }</a>
    </p>
</@base.base>