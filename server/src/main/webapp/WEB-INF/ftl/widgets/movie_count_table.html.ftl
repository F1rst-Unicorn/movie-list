<div class="table-responsive">
    <table class="table table-hover table-striped table-bordered">
        <tr>
            <th>${ translate("Name") }</th>
            <th>${ translate("Number of movies") }</th>
        </tr>
        <#list items as item >
            <tr class="clickable-row" style="cursor: pointer"
                onclick="window.location='${ url(path, item.id) }'">
                <td>${ item.name }</td>
                <td>${ item.count }</td>
            </tr>
        </#list>
    </table>
</div>
