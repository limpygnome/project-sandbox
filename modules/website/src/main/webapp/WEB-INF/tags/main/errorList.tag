<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>

<%@ attribute name="modelAttribute" required="true" %>
<%@ attribute name="cssClass"       required="false" %>
<%@ attribute name="singleError"    required="false" %>

<spring:bind path="${modelAttribute}.*">
    <c:if test="${status.errors.errorCount > 0}">
        <ul <c:if test="${not empty cssClass}">class="<c:out value='${cssClass}' />"</c:if>>
            <c:choose>
                <c:when test="${not empty singleError && singleError}">
                    <li>
                        <c:out value="${status.errorMessages[0]}" />
                    </li>
                </c:when>
                <c:otherwise>
                    <c:forEach var="error" items="${status.errorMessages}">
                        <li>
                            <c:out value="${error}" />
                        </li>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </ul>
    </c:if>
</spring:bind>
