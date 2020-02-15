
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
