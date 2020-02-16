<#import "base.html.ftl" as base>
<@base.base>
    <div>
        <h2>${ translate("Add new location") }</h2>
    </div>
    <div>
        <form action="${ url("add_location") }" method="post">
            <input type="hidden"
                   name="${ csrftoken.parameterName }"
                   value="${ csrftoken.token }"/>
            <div class="form-group">
                <label class="control-label"
                       for="id_name">${ translate("Name")}
                </label>
                <input type="text" name="name" class="form-control"
                       id="id_name">
            </div>
            <div class="form-group">
                <label class="control-label"
                       for="id_index">${ translate("Name")}
                </label>
                <input type="number" name="index" class="form-control"
                       id="id_index">
            </div>
            <input class="btn btn-success" type="submit"
                   value="${ translate("Add") }">
        </form>
    </div>

</@base.base>