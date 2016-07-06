<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
    <c:when test="${minified}">
        <%--
            Minified resources
        --%>
        <link rel="stylesheet" href="<spring:url value='/content/game.css' />" />
        <script src="<spring:url value='/content/game.js' />"></script>
    </c:when>
    <c:otherwise>
        <%--
            Disable browser caching
        --%>
        <meta http-equiv="cache-control" content="no-cache" />

        <%--
            Non-Minified resources
        --%>
        <%@ include file="_resources-css.jsp" %>
        <%@ include file="_resources-js.jsp" %>
    </c:otherwise>
</c:choose>
