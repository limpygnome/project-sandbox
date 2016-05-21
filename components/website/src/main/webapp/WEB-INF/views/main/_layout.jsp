<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"	uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<!DOCTYPE html>
<html>
<head>
    <title>
        Project Sandbox - <c:out value="${title}" />
    </title>
    <link rel="stylesheet" href="/content/main/css/layout.css" />
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
                    <a class="user" href="/account">
                        <c:out value="${user.nickname}" /> [ROLE]
                        <img src="/content/game/images/annie.png" alt="User avatar" />
                    </a>
                    <a href="/auth/logout" class="button">
                        Logout
                    </a>
                </div>
            </ps:authenticated>
            <div class="nav">
                <ul>
                    <li>
                        <a href="/">
                            Join
                        </a>
                    </li>
                    <ps:authenticated auth="true">
                        <li>
                            <a href="/account">
                                Account
                            </a>
                        </li>
                    </ps:authenticated>
                    <li>
                        <a href="/stats">
                            Stats
                        </a>
                    </li>
                    <li>
                        <a href="/credits">
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
