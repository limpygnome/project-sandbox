<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"   uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<!DOCTYPE html>
<html>
<head>
    <title>
        Project Sandbox - <c:out value="${title}" />
    </title>
    <link rel="stylesheet" href="<spring:url value='/content/main/css/layout.css' />" />
</head>
<body>
    <div class="parent">
        <div class="header">
            <h1>
                Project Sandbox
            </h1>
            <ps:authenticated auth="true">
                <div class="buttons">
                    <span class="score">
                        $ <fmt:formatNumber type="number" value="${user.playerMetrics.score}" />
                    </span>
                    <a class="user" href="<spring:url value='/account' />">
                        <span class="thumbnail">
                            <img src="<spring:url value='/content/game/images/annie.png' />" alt="User avatar" />
                        </span>
                        <span class="name">
                            <c:out value="${user.nickname}" />
                        </span>
                        <span class="role">
                            <c:out value="${user.roles.primaryRole.displayName}" />
                        </span>
                    </a>
                    <a href="<spring:url value='/account' />" class="button">
                        Account
                    </a>
                    <ps:authenticated auth="true" roles="administrator">
                        <a href="<spring:url value='/map-editor' />" class="button">
                            Map Editor
                        </a>
                    </ps:authenticated>
                    <a href="<spring:url value='/auth/logout' />" class="button">
                        Logout
                    </a>
                </div>
            </ps:authenticated>
            <div class="nav">
                <ul>
                    <li>
                        <a href="<spring:url value='/' />">
                            Join
                        </a>
                    </li>
                    <ps:authenticated auth="true">
                        <li>
                            <a href="<spring:url value='/account' />">
                                Account
                            </a>
                        </li>
                    </ps:authenticated>
                    <li>
                        <a href="<spring:url value='/stats' />">
                            Stats
                        </a>
                    </li>
                    <li>
                        <a href="<spring:url value='/credits' />">
                            Credits
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <div class="wrapper">
            <div class="${contentClass}">
                <tiles:insertAttribute name="content" />
            </div>
        </div>
        <div class="footer">
            Copyright &copy; limpygnome <c:out value="${copyright_year}"/>
        </div>
    </div>
</body>
</html>
