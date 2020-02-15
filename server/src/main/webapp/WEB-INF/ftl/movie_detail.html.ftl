<#import "base.html.ftl" as base>
<@base.base>
    <div>
        <div class="row">
            <div class="col">
                <h2>${ movie.name }</h2>
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
                <a class="btn btn-success"
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
            </div>
        </div>
    </div>
    <div class="container">
        <div class="row">
            <div class="col">
                <dl class="dl-horizontal">
                    <#if movie.year != 0>
                        <dt>${ translate("Year") }</dt>
                        <dd>${ movie.year }</dd>
                    </#if>
                    <dt>${ translate("Recorded on") }</dt>
                    <dd>${ movie.createdAt }</dd>
                    <#if movie.link?? >
                        <a href="${ movie.link }">${ translate("External information") }</a>
                    </#if>
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
                </dl>
            </div>
            <div class="col">
                <dl class="dl-horizontal">
                    <dt>${ translate("Actors") }</dt>
                    <dd>
                        <ul>
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
                                <li>${ status.user.username }
                                    (${ status.watchedOn })
                                </li>
                            </#list>
                        </ul>
                    </dd>
                </dl>
            </div>
        </div>
    </div>
    <p>${ movie.description?replace("\n", "<br>") }</p>
    <h2>${ translate("Comments") }</h2>
    <div class="container">
        <#list movie.comments as comment>
            <div style="margin-top: 5px; padding: 5px"
                 class="justify-content-between row border rounded">
                <div class="col">
                    ${ comment.creator.username }: ${ comment.content }
                </div>
                <a class="col-auto"
                   href="${ url('delete_comment', movie.id, comment.id) }">
                    <img style="height: 25px" src="${ static("octicons/build/svg/trashcan.svg") }"/>
                </a>
            </div>
        </#list>
    </div>
    <div style="margin: 10px;">
        <form id="form" class="form-inline"
              action="${ url('detail', movie.id) }" method="post">
            <input type="hidden" name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <textarea placeholder="${ translate("Leave a comment") }" form="form"
                      class="form-control" rows="2" name="content"
                      hint="${ translate("Add comment") }"></textarea>
            <input class="btn btn-success" type="submit"
                   value="${ translate("Comment") }"/>
        </form>
    </div>
</@base.base>