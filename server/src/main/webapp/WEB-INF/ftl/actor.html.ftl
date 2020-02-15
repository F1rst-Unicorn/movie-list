<#import "base.html.ftl" as base>
<@base.base>
    <#if edit>
        <#assign url = url("actor_edit", actor.id)>
        <#assign headline = translate("Edit actor")>
        <#assign submitLabel = translate("Save")>
    <#else>
        <#assign url = url("add_actor")>
        <#assign headline = translate("Add new actor")>
        <#assign submitLabel = translate("Add")>
    </#if>
    <div>
        <h2>${ headline }</h2>
    </div>
    <div>
        <form action="${ url }" method="post">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <div class="form-group">
                <label class="control-label"
                       for="id_firstname">${ translate("First name")}
                </label>
                <input type="text" name="firstname" class="form-control"
                       id="id_firstname" value="${ (actor.firstName)! }">
            </div>
            <div class="form-group">
                <label class="control-label"
                       for="id_lastname">${ translate("Surname")}
                </label>
                <input type="text" name="lastname" class="form-control"
                       id="id_lastname" value="${ (actor.lastName)! }">
            </div>
            <input class="btn btn-success" type="submit"
                   value="${ submitLabel }">
        </form>
    </div>

</@base.base>