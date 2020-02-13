<#import "base.html.ftl" as base>
<@base.base>
    <#assign headline = translate("Movie List")>
    <#include "widgets/headline.html.ftl">
    <#include "widgets/movie_table.html.ftl">
</@base.base>