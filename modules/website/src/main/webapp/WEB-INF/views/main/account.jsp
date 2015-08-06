<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<h2>
    Account
</h2>

<p class="center">
    <a href="/profile" class="button">
        View Profile
    </a>
</p>

<h3>
    Update Details
</h3>
<form:form cssClass="account-update-details" action="/account/update" method="post" modelAttribute="updateDetailsForm">

    <%-- Check if form data provided i.e. invalid value --%>
    <c:choose>
        <c:when test="${updateDetailsForm != null && updateDetailsForm.email != null}">
            <c:set var="account_update_email" value="${updateDetailsForm.email}" />
        </c:when>
        <c:otherwise>
            <c:set var="account_update_email" value="${user.email}" />
        </c:otherwise>
    </c:choose>

    <table>
        <tr>
            <th>
                Current Password:
            </th>
            <td>
                <ps:inputWithErrors modelAttribute="updateDetailsForm" path="currentPassword" placeholder="Current password..." type="password" />
            </td>
        </tr>
        <tr>
            <th>
                New Password:
            </th>
            <td>
                <ps:inputWithErrors modelAttribute="updateDetailsForm" path="newPassword" placeholder="New password..." type="password" />
            </td>
        </tr>
        <tr>
            <th>
                Confirm Password:
            </th>
            <td>
                <ps:inputWithErrors modelAttribute="updateDetailsForm" path="confirmNewPassword" placeholder="Confirm password..." type="password" />
            </td>
        </tr>
        <tr>
            <th>
                E-mail:
            </th>
            <td>
                <ps:inputWithErrors modelAttribute="updateDetailsForm" path="email" placeholder="E-mail..." value="${account_update_email}" />
            </td>
        </tr>
        <tr>
            <td colspan="2" class="center">
                <input type="submit" value="Update" />
            </td>
        </tr>
    </table>

    <ps:csrf />

    <div class="messages">
        <ps:errorList modelAttribute="updateDetailsForm" cssClass="errors" singleError="true" />

        <c:if test="${not empty account_update_success}">
            <div class="success">
                Updated account details.
            </div>
        </c:if>
    </div>

</form:form>

<h3>
    Options
</h3>

<table>
    <tr>
        <td>
            <form method="post" action="/account/delete">
                <input type="submit" value="Delete Account" />
                <ps:csrf />
            </form>
        </td>
        <td>
            This will permanently delete your account and any associated data.
        </td>
    </tr>
    <tr>
        <td>
            <form method="post" action="/account/reset/game-session">
                <input type="submit" value="Reset Game Session" />
                <ps:csrf />
            </form>
        </td>
        <td>
            This will reset your in-game player, meaning you will lose any customisation, such as weapon pickups.
        </td>
    </tr>
    <tr>
        <td>
            <form method="post" action="/account/reset/stats">
                <input type="submit" value="Reset Stats" />
                <ps:csrf />
            </form>
        </td>
        <td>
            This will reset the stats for your account.
        </td>
    </tr>

    <ps:authenticated auth="true" roles="administrator,moderator">
        <tr>
            <td>
                <a href="/control" class="button">
                    Control Centre
                </a>
            </td>
            <td>
                The administration/moderator control centre.
            </td>
        </tr>
    </ps:authenticated>

</table>
