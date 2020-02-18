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
function mark_watched(movie_id, user_id) {
    var request = new XMLHttpRequest();
    var url = "${ url("mark_watched") }"
        .replace("1234", movie_id)
        .replace("5678", user_id);
    request.onreadystatechange = function () {
        if (this.readyState == 4 && (this.status == 200 || this.status == 204)) {
            var button = document.getElementById("movies.mark_watched." + movie_id);
            if (button !== null) {
                button.style.display = "none";
            } else {
                location.reload();
            }
        }
    };
    request.open("POST", url, true);
    request.setRequestHeader("${ csrftoken.headerName }", "${ csrftoken.token }")
    request.send();
}
