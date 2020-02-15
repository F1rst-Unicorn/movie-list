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
                        <form style="display: inline"
                              action="${ url("delete", movie.id) }"
                              method="post">
                            <input class="btn btn-warning" type="submit"
                                   value="${ translate("Record again") }" >
                            <input type="hidden"
                                   name="${ csrftoken.parameterName }"
                                   value="${ csrftoken.token }"/>
                        </form>
                    <#elseif movie.toDelete >
                        <form style="display: inline"
                              action="${ url("mark_removal", movie.id) }"
                              method="post">
                            <input class="btn btn-warning" type="submit"
                                   value="${ translate("Keep anyway") }" >
                            <input type="hidden"
                                   name="${ csrftoken.parameterName }"
                                   value="${ csrftoken.token }"/>
                        </form>
                        <form style="display: inline"
                              action="${ url("delete", movie.id) }"
                              method="post">
                            <input class="btn btn-danger" type="submit"
                                   value="${ translate("Remove") }" >
                            <input type="hidden"
                                   name="${ csrftoken.parameterName }"
                                   value="${ csrftoken.token }"/>
                        </form>
                    <#else>
                        <form style="display: inline"
                              action="${ url("mark_removal", movie.id) }"
                              method="post">
                            <input class="btn btn-warning" type="image"
                                   src="${ static("octicons/build/svg/trashcan.svg") }">
                            <input type="hidden"
                                   name="${ csrftoken.parameterName }"
                                   value="${ csrftoken.token }"/>
                        </form>
                    </#if>
                </td>
            </tr>
        </#list>
    </table>
</div>
