<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="modelAttribute"     required="true" %>
<%@ attribute name="path"               required="true" %>
<%@ attribute name="errorCssClass"      required="true" %>

<%@ attribute name="type"               required="false" %>
<%@ attribute name="placeholder"        required="false" %>

<spring:bind path="${modelAttribute}.${path}">

    <c:choose>
        <c:when test="${status.error}">
            <c:set var="cssClass" value="${errorCssClass}" />
        </c:when>
    </c:choose>

    <c:choose>
        <c:when test="${empty type || type == 'text'}">
            <form:input     path="${path}" placeholder = "${placeholder}" cssClass="${cssClass}" />
        </c:when>
        <c:when test="${type == 'password'}">
            <form:password  path="${path}" placeholder = "${placeholder}" cssClass="${cssClass}" />
        </c:when>
    </c:choose>

</spring:bind>
