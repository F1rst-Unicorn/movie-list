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