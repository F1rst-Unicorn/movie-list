<div class="table-responsive">
    <table class="table table-hover table-striped table-bordered">
        <tr>
            <th>${ translate("Name") }</th>
            <th>${ translate("Actors") }</th>
            <th>${ translate("Location") }</th>
            <th>${ translate("Actions") }</th>
        </tr>
        <#list movies as movie>
            <tr class="clickable-row" style="cursor: pointer"
                onclick="window.location='${ url("detail", movie.id) }'">
                <td>${ movie.name }</td>
                <td>
                    <#list movie.actors as actor>
                        ${ actor }<#sep>, </#sep>
                    </#list>
                </td>
                <td>${ movie.location }</td>
                <td>
                    <#if ! movie.watchedByUser >
                        <a id="movies.mark_watched.${ movie.id?string.computer }"
                           class="btn btn-sm btn-primary"
                           onclick="event.stopPropagation(); mark_watched(${ movie.id?string.computer }, ${ user.id })">
                            <img class="white-icon"
                                 src="${ static("octicons/build/svg/eye.svg") }"/>
                        </a>
                    </#if>
                    <#if movie.deleted >
                        <a href="${ url("delete", movie.id) }"
                           class="btn btn-warning">${ translate("Record again") }</a>
                    <#elseif movie.toDelete >
                        <a href="${ url("mark_removal", movie.id) }"
                           class="btn btn-warning">${ translate("Keep anyway") }</a>
                        <a href="${ url("delete", movie.id) }"
                           class="btn btn-danger">${ translate("Remove") }</a>
                    <#else>
                        <a href="${ url("mark_removal", movie.id) }"
                           class="btn btn-sm btn-warning"><img
                                    src="${ static("octicons/build/svg/trashcan.svg") }"/>
                        </a>
                    </#if>
                </td>
            </tr>
        </#list>
    </table>
</div>
