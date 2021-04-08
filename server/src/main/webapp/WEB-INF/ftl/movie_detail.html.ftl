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
        <div class="row">
            <div class="col">
                <h2 id="title">${ movie.name }</h2>
            </div>
            <div class="col col-auto">
                <div class="dropdown dropdown-menu-right">
                    <button id="Watched" type="button"
                            class="btn btn-primary dropdown-toggle"
                            data-toggle="dropdown" aria-haspopup="true"
                            aria-expanded="false">
                        <img class="white-icon"
                             src="${ static("octicons/build/svg/eye.svg") }"/>
                    </button>
                    <div class="dropdown-menu dropdown-menu-right"
                         aria-labelledby="Watched">
                        <#list users as user >
                            <div style="cursor: pointer" class="dropdown-item"
                                 onclick="mark_watched(${ movie.id?string.computer }, ${ user.id?string.computer })">
                                ${ user.username }
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
            <div class="col col-auto">
                <a class="btn btn-success" id="edit"
                   href="${ url('edit', movie.id) }">
                    <img class="white-icon"
                         src="${ static("octicons/build/svg/pencil.svg") }"/>
                </a>
            </div>
            <div class="col col-auto">
                <#if movie.deleted >
                    <form style="display: inline"
                          action="${ url("delete", movie.id) }"
                          method="post">
                        <input class="btn btn-warning" type="submit" id="record"
                               value="${ translate("Record again") }">
                        <input type="hidden"
                               name="${ csrftoken.parameterName }"
                               value="${ csrftoken.token }"/>
                    </form>
                <#elseif movie.toDelete >
                    <form style="display: inline"
                          action="${ url("mark_removal", movie.id) }"
                          method="post">
                        <input class="btn btn-warning" type="submit" id="keep"
                               value="${ translate("Keep anyway") }">
                        <input type="hidden"
                               name="${ csrftoken.parameterName }"
                               value="${ csrftoken.token }"/>
                    </form>
                    <form style="display: inline"
                          action="${ url("delete", movie.id) }"
                          method="post">
                        <input class="btn btn-danger" type="submit" id="make-absent"
                               value="${ translate("Remove") }">
                        <input type="hidden"
                               name="${ csrftoken.parameterName }"
                               value="${ csrftoken.token }"/>
                    </form>
                <#else>
                    <form style="display: inline"
                          action="${ url("mark_removal", movie.id) }"
                          method="post">
                        <input class="btn btn-warning" type="image" id="delete"
                               src="${ static("octicons/build/svg/trashcan.svg") }">
                        <input type="hidden"
                               name="${ csrftoken.parameterName }"
                               value="${ csrftoken.token }"/>
                    </form>
                </#if>
            </div>
        </div>
    </div>
    <div class="container">
        <div class="row">
            <div class="col">
                <dl class="dl-horizontal">
                    <#if movie.year != 0>
                        <dt>${ translate("Year") }</dt>
                        <dd id="year">${ movie.year?string.computer }</dd>
                    </#if>
                    <dt>${ translate("Recorded on") }</dt>
                    <dd>${ movie.createdAt }</dd>
                    <dt>${ translate("Location") }</dt>
                    <dd id="location">${ movie.prettyLocation }</dd>
                    <#if movie.link?? && ! movie.link?matches("^$") >
                        <a href="${ movie.link }" id="link">${ translate("External information") }</a>
                        <br>
                    </#if>
                        <a href="https://de.wikipedia.org/w/index.php?search=${ movie.name }&title=Spezial%3ASuche&go=Artikel&ns0=1" id="link">${ translate("Wikipedia Search") }</a>
                        <br>
                        <a href="https://duckduckgo.com/?q=${ movie.name }" id="link">${ translate("DuckDuckGo Search") }</a>
                    <div id="movie-genres">
                        <#list movie.genres as genre >
                            <#if genre?is_first>
                                <dt>${ translate("Genres") }</dt>
                            </#if>
                            <a href="${ url("genre_detail", genre.id) }">
                                <dd class="badge badge-info"
                                    style="margin-right: 5px">${ genre.name }
                                </dd>
                            </a>
                        </#list>
                    </div>
                </dl>
            </div>
            <div class="col">
                <dl class="dl-horizontal">
                    <dt>${ translate("Actors") }</dt>
                    <dd>
                        <ul id="actors">
                            <#list movie.actors as actor>
                                <a href="${ url("actor_detail", actor.id) }">
                                    <li>${ actor.firstName } ${ actor.lastName }</li>
                                </a>
                            </#list>
                        </ul>
                    </dd>
                </dl>
            </div>
            <div class="col">
                <dl class="dl-horizontal">
                    <dt>${ translate("Watched by") }</dt>
                    <dd>
                        <ul>
                            <#list movie.watchedBy as status>
                                <li id="watchedBy">${ status.user.username }
                                    (${ status.watchedOn })
                                </li>
                            </#list>
                        </ul>
                    </dd>
                </dl>
            </div>
        </div>
    </div>
    <p id="description">${ movie.description?replace("\n", "<br>")?no_esc }</p>
    <h2>${ translate("Comments") }</h2>
    <div id="comments" class="container">
        <#list movie.comments as comment>
            <div style="margin-top: 5px; padding: 5px"
                 class="justify-content-between row border rounded">
                <div class="col">
                    ${ comment.creator.username }: ${ comment.content }
                </div>
                <form style="display: inline"
                      action="${ url("delete_comment", movie.id, comment.id) }"
                      method="post">
                    <input type="image" class="btn btn-sm btn-warning"
                           src="${ static("octicons/build/svg/trashcan.svg") }">
                    <input type="hidden"
                           name="${ csrftoken.parameterName }"
                           value="${ csrftoken.token }"/>
                </form>
            </div>
        </#list>
    </div>
    <div style="margin: 10px;">
        <form id="form" class="form-inline"
              action="${ url('add_comment', movie.id) }" method="post">
            <input type="hidden" name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <textarea placeholder="${ translate("Leave a comment") }"
                      form="form" id="comment"
                      class="form-control" rows="2" name="comment"
                      hint="${ translate("Add comment") }"></textarea>
            <input class="btn btn-success" type="submit" id="submit"
                   value="${ translate("Comment") }"/>
        </form>
    </div>
</@base.base>
