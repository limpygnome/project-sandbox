<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty csrf}">
    <input type="hidden" name="csrf" value="<c:out value="${csrf}" />" />
</c:if>
