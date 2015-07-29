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
    Change Password
</h3>
<form:form action="/account/update-password" method="post" modelAttribute="changePasswordForm">
    <table>
        <tr>
            <th>
                Password:
            </th>
            <td>
                <ps:inputWithErrors modelAttribute="changePasswordForm" path="password" placeholder="New password..." type="password" />
            </td>
        </tr>
        <tr>
            <th>
                Confirm Password:
            </th>
            <td>
                <ps:inputWithErrors modelAttribute="changePasswordForm" path="confirmPassword" placeholder="Confirm password..." type="password" />
            </td>
        </tr>
        <tr>
            <td colspan="2" class="center">
                <input type="submit" value="Update" />
            </td>
        </tr>
    </table>

    <ps:csrf />
    <ps:errorList modelAttribute="loginForm" cssClass="errors" singleError="true" />

</form:form>

<h3>
    Change E-mail
</h3>

<h3>
    Options
</h3>

<table>
    <tr>
        <td>
            <form method="post" action="/account/delete">
                <input type="submit" value="Delete Account" />
            </form>
        </td>
        <td>
            This will permanently delete your account and any associated data.
        </td>
    </tr>
    <tr>
        <td>
            <form method="post" action="/account/reset_game_session">
                <input type="submit" value="Reset Game Session" />
            </form>
        </td>
        <td>
            This will reset your in-game player, meaning you will lose any customisation, such as weapon pickups.
        </td>
    </tr>
    <tr>
        <td>
            <form method="post" action="/account/reset_stats">
                <input type="submit" value="Reset Stats" />
            </form>
        </td>
        <td>
            This will reset the stats for your account.
        </td>
    </tr>
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
</table>
