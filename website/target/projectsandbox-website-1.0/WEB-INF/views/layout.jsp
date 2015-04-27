<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!--
    Copyright © Annie Glover <c:out value="${copyright_year}"/>
-->
<!DOCTYPE html>
<html>
<head>
    <title>annie glover :: <c:out value="${title}"/></title>
    <meta name="keywords" content="annie,glover,art,portfolio,commissions.tumblr" />
    <meta name="description" content="My art portfolio and collection of sketches." />
    <meta name="robots" content="All" />
    <meta http-equiv="Cache-control" content="public">
    <link rel="stylesheet" type="text/css" href="/content/site.min.css" />
    <script src="/content/site.min.js"></script>
    <script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=ra-55342c5b14bd83df" async="async"></script>
</head>
<body onload="bgSwitch();">

    <div class="wrapper">
        <div class="banner">
            <h1>annie glover</h1>
        </div>
        <div class="main">
            <div class="nav">
                <ul>
                    <li>
                        <a href="/home">home</a>
                    </li>
                    <li>
                        <a href="/about">about</a>
                    </li>
                    <li>
                        <a href="/portfolio">portfolio</a>
                    </li>
                    <li>
                        <a href="/contact">contact</a>
                    </li>
                    <li>
                        <a href="http://giricocola.tumblr.com/">tumblr</a>
                    </li>
                </ul>
            </div>
            <div class="content <c:if test="${!content_frame}">noframe</c:if>">
                <c:if test="${content_header}">
                    <h2>
                        <c:out value="${title}"/>
                    </h2>
                </c:if>

                <tiles:insertAttribute name="content" />

                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </div>

    <div class="faux_footer">
        &nbsp;
    </div>

    <div class="footer">
        &copy; Annie Glover <c:out value="${copyright_year}"/>
    </div>

</body>
</html>
