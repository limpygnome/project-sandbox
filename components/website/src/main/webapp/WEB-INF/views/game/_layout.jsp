<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"   uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>

<!--
    Copyright &copy; limpygnome <c:out value="${copyright_year}"/>
-->
<!DOCTYPE html>
<html>
    <head>
        <title>
            Project Sandbox
        </title>

        <%--
            Prevent page zooming/scaling on mobile devices

            This prevents page from being zoomed when exiting full-screen mode.
        --%>
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0" />

        <%--
            Minified resources
        --%>
        <link rel="stylesheet" href="<spring:url value='/content/game.css' />" />
        <script src="<spring:url value='/content/game.js' />"></script>

    </head>
    <body>

        <c:if test="${content_header}">
            <h2>
                <c:out value="${title}"/>
            </h2>
        </c:if>

        <tiles:insertAttribute name="content" />

    </body>
</html>
