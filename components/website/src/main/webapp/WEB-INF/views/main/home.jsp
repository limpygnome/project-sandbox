<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<ps:authenticated auth="false">
    <form:form cssClass="box guest" method="post" servletRelativeAction="/auth/guest" modelAttribute="guestForm">
        <h3>
            Play as Guest...
        </h3>
        <table>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="guestForm" path="nickname" placeholder="Enter a nickname..." />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="Join" class="button" />
                </td>
            </tr>
        </table>

        <ps:csrf />
        <ps:errorList modelAttribute="guestForm" cssClass="error" />

    </form:form>

    <form:form cssClass="box login" method="post" servletRelativeAction="/auth/login" modelAttribute="loginForm">
        <h3>
            Login
        </h3>
        <table>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="loginForm" path="nickname" placeholder="Nickname..." />
                </td>
            </tr>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="loginForm" path="password" placeholder="Password..." type="password" />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="Login" class="button" />
                </td>
            </tr>
        </table>

        <ps:csrf />
        <ps:errorList modelAttribute="loginForm" cssClass="error" singleError="true" />

    </form:form>

    <form:form cssClass="box register" method="post" servletRelativeAction="/auth/register" modelAttribute="registerForm">
        <h3>
            Register
        </h3>
        <table>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="registerForm" path="nickname" placeholder="Enter a nickname..." />
                </td>
            </tr>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="registerForm" path="password" placeholder="Enter a password..." type="password" />
                </td>
            </tr>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="registerForm" path="email" placeholder="E-mail..." />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="Login" class="button" />
                </td>
            </tr>
        </table>

        <ps:csrf />
        <ps:errorList modelAttribute="registerForm" cssClass="error" />

    </form:form>
</ps:authenticated>

<ps:authenticated auth="true">
    <form:form cssClass="box user" method="post" servletRelativeAction="/auth/user">
        <h3>
            Welcome <c:out value="${user.nickname}" />!
        </h3>
        <table>
            <tr>
                <td>
                    <input type="submit" value="Join Now" class="button" />
                </td>
            </tr>
        </table>
        <ps:csrf />
    </form:form>
</ps:authenticated>

<div class="online">
    <span><c:out value="${playersOnline}" /></span> players online
</div>
