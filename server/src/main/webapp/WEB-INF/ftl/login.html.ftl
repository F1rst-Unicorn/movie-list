<#import "base.html.ftl" as base>
<@base.base>
    <h1>${ translate("Login") }</h1>
    <#if error>
        <div class="alert alert-danger"
             role="alert">${ translate("Username or password wrong!")}</div>
    </#if>
    <form action="${ url("login") }" method="post">
        <div class="form-group">
            <label class="control-label"
                   for="id_username">${ translate("Username") }</label>
            <input id="id_username" class="form-control" autofocus=""
                   autocapitalize="none" autocomplete="username" maxlength="150"
                   required="" type="text" name="username"/>
        </div>
        <div class="form-group">
            <label class="control-label"
                   for="id_password">${ translate("Password") }</label>
            <input id="id_password" class="form-control"
                   autocomplete="current-password" required="" type="password"
                   name="password"/>
        </div>
        <input type="hidden" name="${ csrftoken.parameterName }"
               value="${ csrftoken.token }"/>
        <input type="hidden" name="remember-me"
               value="1"/>
        <input class="btn btn-success" type="submit"
               value="${ translate("Login") }">
    </form>
    <p><a class="btn btn-primary"
          href="${ url('create_account') }">${ translate("Create account") }</a>
    </p>
</@base.base>