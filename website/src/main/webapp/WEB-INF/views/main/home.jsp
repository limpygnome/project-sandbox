<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<ps:authenticated auth="false">
    <form:form cssClass="box guest" method="post" action="/auth/guest" modelAttribute="guestForm">
        <h3>
            Play as Guest...
        </h3>
        <table>
            <tr>
                <td>
                    <input type="text" name="nickname" placeholder="Enter a nickname..." value="test" />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="Join" class="button" />
                </td>
            </tr>
        </table>
        <ps:csrf />
    </form:form>

    <form class="box login" method="post" action="/auth/login" modelAttribute="loginForm">
        <h3>
            Login
        </h3>
        <table>
            <tr>
                <td>
                    <input type="text" name="user" placeholder="Username..." />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="text" name="pass" placeholder="Password..." />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="Login" class="button" />
                </td>
            </tr>
        </table>
        <ps:csrf />
    </form>

    <form:form cssClass="box register" method="post" action="/auth/register" modelAttribute="registerForm" >
        <h3>
            Register
        </h3>
        <table>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="registerForm" path="username" placeholder="Username..." errorCssClass="error" />
                </td>
            </tr>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="registerForm" path="password" placeholder="Password..." errorCssClass="error" type="password" />
                </td>
            </tr>
            <tr>
                <td>
                    <ps:inputWithErrors modelAttribute="registerForm" path="email" placeholder="E-mail..." errorCssClass="error" />
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="Login" class="button" />
                </td>
            </tr>
        </table>
        <ps:csrf />

        <ps:errorList modelAttribute="registerForm" cssClass="errors" />

    </form:form>
</ps:authenticated>

<ps:authenticated auth="true">
    <form class="box user" method="post" action="/auth/user">
        <h3>
            Welcome username!
        </h3>
        <table>
            <tr>
                <td>
                    <input type="submit" value="Join Now" class="button" />
                </td>
            </tr>
        </table>
        <ps:csrf />
    </form>
</ps:authenticated>
