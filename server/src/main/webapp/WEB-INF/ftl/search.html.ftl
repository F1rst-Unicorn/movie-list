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
            <input class="btn btn-success" type="submit"
                   value="${ translate("Search") }">
        </form>
        <#if movies?? >
            <#include "widgets/movie_table.html.ftl">
        </#if>
    </div>
</@base.base>