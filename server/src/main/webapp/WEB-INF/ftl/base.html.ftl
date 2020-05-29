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
<#macro base>
    <#compress>
        <!DOCTYPE html>
        <html lang="${.lang}">
        <head>
            <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport"
                  content="width=device-width, initial-scale=1 shrink-to-fit=no">
            <link rel="stylesheet"
                  href="${ static("bootstrap/dist/css/bootstrap.min.css")}">
            <link rel="stylesheet" href="${ static("custom.css")}">
            <link rel="icon" type="image/png"
                  href="${ static("favicon.png") }"/>
            <title>${ translate("Movie Database") }</title>
        </head>
        <body>
        <div>
            <nav class="navbar navbar-expand-sm navbar-dark bg-dark d-print">
                <#if user.authenticated >
                    <div class="container collapse navbar-collapse"
                         id="navbardropdown">
                        <ul class="navbar-nav">
                            <li class="nav-item">
                                <a href="${ url("index") }" id="home"
                                   class="nav-link">
                                    <img class="white-icon"
                                         style="width: 24px; height: auto;"
                                         src="${ static("octicons/build/svg/home.svg") }"/>
                                </a>
                            </li>
                            <li class="nav-item dropdown" id="add-dropdown">
                                <a href="#" class="nav-link dropdown-toggle"
                                   data-toggle="dropdown" aria-haspopup="true"
                                   aria-expanded="false">
                                    <img class="white-icon"
                                         src="${ static("octicons/build/svg/plus.svg") }"/>
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a class="dropdown-item" id="add-movie"
                                           href="${ url('add_movie') }">
                                            ${ translate("Movie") }
                                        </a>
                                        <a class="dropdown-item" id="add-actor"
                                           href="${ url('add_actor') }">
                                            ${ translate("Actor") }
                                        </a>
                                        <a class="dropdown-item" id="add-genre"
                                           href="${ url('add_genre') }">
                                            ${ translate("Genre") }
                                        </a>
                                        <a class="dropdown-item" id="add-location"
                                           href="${ url('add_location') }">
                                            ${ translate("Location") }
                                        </a>
                                    </li>
                                </ul>
                            </li>
                            <li class="nav-item dropdown" id="nav-dropdown">
                                <a href="#" class="nav-link dropdown-toggle"
                                   data-toggle="dropdown" aria-haspopup="true"
                                   aria-expanded="false">
                                    ${ translate("Browse") }
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a class="dropdown-item"
                                           href="${ url('index') }">
                                            ${ translate("Movies") }
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" id="latest"
                                           href="${ url('latest') }">
                                            ${ translate("Newly recorded") }
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" id="actors"
                                           href="${ url('actors') }">
                                            ${ translate("Actors") }
                                        </a>
                                    </li>
                                    <li>
                                        <a class="dropdown-item" id="genres"
                                           href="${ url('genres') }">
                                            ${ translate("Genres") }
                                        </a>
                                    </li>
                                </ul>
                            </li>
                            <li class="nav-item dropdown" id="mgmt-dropdown">
                                <a href="#" class="nav-link dropdown-toggle"
                                   data-toggle="dropdown" aria-haspopup="true"
                                   aria-expanded="false">
                                    ${ translate("Manage") }
                                </a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a class="dropdown-item" id="removed"
                                           href="${ url('removed_movies') }">
                                            ${ translate("Movies to remove") }
                                        </a>
                                    </li>
                                    <li class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" id="absent"
                                           href="${ url('absent_movies') }">
                                            ${ translate("Absent movies") }
                                        </a>
                                </ul>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="search"
                                   href="${ url('search') }">
                                    ${ translate("Search") }
                                </a>
                            </li>
                        </ul>
                    </div>
                </#if>
                <div class="navbar-header navbar-brand navbar-right">
                    <#if user.authenticated >
                        <img class="white-icon"
                             src="${ static("octicons/build/svg/person.svg") }"/>
                        ${ user.username }
                        <form style="display: inline" action="${ url("logout") }" method="post">
                            <input class="btn btn-outline-primary" type="submit"
                                   id="logout"
                                   value="${ translate("Logout") }">
                            <input type="hidden"
                                   name="${ csrftoken.parameterName }"
                                   value="${ csrftoken.token }"/>
                        </form>
                    </#if>
                </div>
            </nav>
            <div class="container" style="margin-top: 10px">
                <#nested>
            </div>
        </div>
        <script src="${ static("jquery/dist/jquery.slim.min.js") }"></script>
        <script src="${ static("bootstrap/dist/js/bootstrap.bundle.min.js") }"></script>
        <script>
            <#include "js/functions.js.ftl">
        </script>
        <script>
            <@scripts />
        </script>
        </body>
    </#compress>
</#macro>

<#macro scripts></#macro>
