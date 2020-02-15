<#import "base.html.ftl" as base>
<@base.base>
    <#assign headline = translate("Actor List")>
    <#include "widgets/headline.html.ftl">
    <#assign path = "actor_detail">
    <#include "widgets/movie_count_table.html.ftl">
</@base.base>