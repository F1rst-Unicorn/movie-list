<#import "base.html.ftl" as base>
<@base.base>
    <#if edit>
        <#assign url = url("genre_edit", genre.id)>
        <#assign headline = translate("Edit genre")>
        <#assign submitLabel = translate("Save")>
    <#else>
        <#assign url = url("add_genre")>
        <#assign headline = translate("Add new genre")>
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
                       for="id_name">${ translate("Name")}
                </label>
                <input type="text" name="name" class="form-control"
                       id="id_name" value="${ (genre.name)! }">
            </div>
            <input class="btn btn-success" type="submit"
                   value="${ submitLabel }">
        </form>
    </div>

</@base.base>