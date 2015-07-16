<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles"	uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html>
<html>
<head>
    <title>
        Project Sandbox - ${title}
    </title>
    <link rel="stylesheet" href="/content/css/main/layout.css" />
</head>
<body>
    <div class="header">
        <h1>
            Project Sandbox
        </h1>
        <div class="nav">
            <ul>
                <li>
                    <a href="/">
                        Join Game
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
        <div class="content">
            <tiles:insertAttribute name="content" />
        </div>
        <div class="footer">
            Copyright &copy; Marcus Craske <c:out value="${copyright_year}"/>
        </div>
    </div>
</body>
</html>