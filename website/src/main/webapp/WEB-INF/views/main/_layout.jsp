<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"	uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<!DOCTYPE html>
<html>
<head>
    <title>
        Project Sandbox - ${title}
    </title>
    <link rel="stylesheet" href="/content/main/css/layout.css" />
</head>
<body>
    <div class="header">
        <h1>
            Project Sandbox
        </h1>
        <ps:authenticated auth="true">
            <div class="buttons">
                <span class="score">
                    $ 1,000,000,000,000
                </span>
                <a class="user" href="/account">
                    test_user
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
                <li>
                    <a href="/account">
                        Account
                    </a>
                </li>
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
        Copyright &copy; Marcus Craske <c:out value="${copyright_year}"/>
    </div>
</body>
</html>
