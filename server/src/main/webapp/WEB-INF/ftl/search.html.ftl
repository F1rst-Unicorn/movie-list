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
    <div class="container">
        <form id="movies.search.form" action="${ url("search") }"
              method="post">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <div class="form-group">
                <label class="control-label"
                       for="id_text">${ translate("Query")}
                </label>
                <input type="text" name="text" class="form-control"
                       id="id_text">
            </div>
            <div class="form-group">
                <label class="control-label" for="id_choices">
                    ${ translate("Search in")}
                </label>
                <select name="choices" class=" form-control"
                        required=""
                        id="id_choices" multiple="">
                    <option value="0"
                            selected="">${ translate("Name") }</option>
                    <option value="1"
                            selected="">${ translate("Description") }
                    </option>
                    <option value="2"
                            selected="">${ translate("Comments") }</option>
                </select>
            </div>
            <div class="form-group">
                <div class="checkbox"><label><input type="checkbox"
                                                    name="deleted_movies"
                                                    id="id_deleted_movies"><span>${ translate("Include missing movies") }</span></label>
                </div>
            </div>
            <div class="form-group"><label class="control-label"
                                           for="id_genres">${ translate("Genres") }</label>
                <select name="genres" class="form-control"
                                       id="id_genres"
                                       multiple="">
                        <#list genres as genre>
                            <option value="${ genre.id}">${ genre.name }</option>
                        </#list>
                    </select>
            </div>
            <div class="form-group"><label class="control-label"
                                           for="id_unwatched_by">${ translate("New for") }</label>
                <select name="unwatched_by" class="form-control"
                        id="id_unwatched_by" multiple="">
                    <#list users as user>
                        <option value="${ user.id}">${ user.name }</option>
                    </#list>
                </select>
            </div>
            <input class="btn btn-success" type="submit" id="submit"
                   value="${ translate("Search") }">
        </form>
        <#if movies?? >
            <#include "widgets/movie_table.html.ftl">
        </#if>
    </div>
</@base.base>