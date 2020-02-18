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
    <div class="row justify-content-between">
        <div class="col">
            <h2>${ genre }</h2>
        </div>
        <div class="col col-auto">
            <a class="btn btn-success"
               href="${ url('genre_edit', genre.id) }">
                <img class="white-icon"
                     src="${ static("octicons/build/svg/pencil.svg") }"/>
            </a>
        </div>
    </div>
    <#include "widgets/movie_table.html.ftl">
</@base.base>