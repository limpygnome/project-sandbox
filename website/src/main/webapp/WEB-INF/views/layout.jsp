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
</head>
<body>


                <tiles:insertAttribute name="content" />

</body>
</html>
