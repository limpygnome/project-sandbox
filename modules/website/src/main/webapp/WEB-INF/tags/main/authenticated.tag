<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="auth" required="true" %>
<%@ attribute name="roles" required="false" %>

<c:if test="${ (user != null && auth) || (user == null && !auth) }">
    <c:if test="${ empty roles || (user != null && user.roles.containsTag(roles)) }">
        <jsp:doBody />
    </c:if>
</c:if>
