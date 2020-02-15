<#import "base.html.ftl" as base>
<@base.base>
    <div>
        <h2>${ translate("Merge %s with", actor) }</h2>
    </div>
    <div>
        <form action="${ url("actor_merge", actor.id) }" method="post">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <div class="form-group"><label class="control-label"
                                           for="id_actors">${ translate("Actors") }</label>
                <select name="other" class="form-control"
                        id="id_actors" required="">
                    <option value="" selected="">--------</option>
                    <#list actors as actor>
                        <option value="${ actor.id?string.computer }">${ actor }</option>
                    </#list>
                </select>
            </div>
            <input class="btn btn-success" type="submit"
                   value="${ translate("Save") }">
        </form>
    </div>
</@base.base>