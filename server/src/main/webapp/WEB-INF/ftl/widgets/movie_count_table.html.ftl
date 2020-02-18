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
<div class="table-responsive">
    <table class="table table-hover table-striped table-bordered">
        <tr>
            <th>${ translate("Name") }</th>
            <th>${ translate("Number of movies") }</th>
        </tr>
        <#list items as item >
            <tr class="clickable-row" style="cursor: pointer"
                onclick="window.location='${ url(path, item.id) }'">
                <td>${ item.name }</td>
                <td>${ item.count }</td>
            </tr>
        </#list>
    </table>
</div>
