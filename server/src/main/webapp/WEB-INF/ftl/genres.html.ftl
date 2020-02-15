<#import "base.html.ftl" as base>
<@base.base>
    <#assign headline = translate("Genre List")>
    <#include "widgets/headline.html.ftl">
    <#assign path = "genre_detail">
    <#include "widgets/movie_count_table.html.ftl">
</@base.base>