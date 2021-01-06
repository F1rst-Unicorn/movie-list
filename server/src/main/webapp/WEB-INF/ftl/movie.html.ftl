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
    <#if edit??>
        <#assign url = url("edit", movie.id)>
        <#assign headline = translate("Edit movie")>
        <#assign submitLabel = translate("Save")>
    <#else>
        <#assign url = url("add_movie")>
        <#assign headline = translate("Add new movie")>
        <#assign submitLabel = translate("Add")>
    </#if>
    <div>
        <h2>${ headline }</h2>
    </div>
    <div>
        <form action="${ url }" method="post">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <input class="btn btn-success" type="submit"
                   value="${ submitLabel }">
            <div class="form-group">
                <label class="control-label"
                       for="id_name">${ translate("Name")}
                </label>
                <input type="text" name="name" class="form-control"
                       id="id_name" value="${ (movie.name)! }">
            </div>
            <div class="form-group"><label class="control-label  "
                                           for="id_description">${ translate("Description")}</label>
                <div class=" "><textarea name="description" cols="40"
                                         rows="10" maxlength="5000"
                                         class=" form-control"
                                         id="id_description">${ (movie.description)! }</textarea>
                </div>
            </div>
            <div class="form-group"><label class="control-label  "
                                           for="id_year">${ translate("Year")}</label>
                <div class=" "><input type="number" name="year"
                                      value="${((movie.year)!0)?string.computer }"
                                      class=" form-control" required=""
                                      id="id_year"></div>
            </div>
            <div class="form-group"><label class="control-label  "
                                           for="id_location">${ translate("Locations")}</label>
                <div class=" "><select name="location" class=" form-control"
                                       required="" id="id_location">
                        <option value="" selected="">---------</option>
                        <#list locations as location>
                            <option value="${ location.id?string.computer }"
                                    <#if location.selected>selected=""</#if>>${ location }</option>
                        </#list>
                    </select></div>
            </div>
            <div class="form-group"><label class="control-label  "
                                           for="id_link">${ translate("Link")}</label>
                <div class=" "><input type="text" name="link"
                                      maxlength="2000" class=" form-control"
                                      id="id_link" value="${(movie.link)! }">
                </div>
            </div>
            <div class="form-group"><label class="control-label  "
                                           for="id_actors">${ translate("Actors")}</label>
                <div class=" "><select name="actors" class=" form-control"
                                       id="id_actors" multiple="">
                        <#list actors as actor>
                            <option value="${ actor.id?string.computer }"
                                    <#if actor.selected>selected=""</#if>>${ actor }</option>
                        </#list>
                    </select></div>
            </div>
            <div class="form-group"><label class="control-label  "
                                           for="id_genres">${ translate("Genres")}</label>
                <div class=" "><select name="genres" class=" form-control"
                                       id="id_genres" multiple="">
                        <#list genres as genre>
                            <option value="${ genre.id?string.computer }"
                                    <#if genre.selected>selected=""</#if>>${ genre.name }</option>
                        </#list>
                    </select></div>
            </div>
            <input class="btn btn-success" type="submit" id="submit"
                   value="${ submitLabel }">
        </form>
    </div>

</@base.base>
