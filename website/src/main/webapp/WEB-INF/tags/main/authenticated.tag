<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="auth" required="true" %>

<c:if test="${ (user != null && auth) || (user == null && !auth) }">
    <jsp:doBody />
</c:if>
