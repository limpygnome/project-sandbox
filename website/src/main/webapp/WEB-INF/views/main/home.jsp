<form class="box guest" method="post" action="/auth/guest">
    <h3>
        Play as Guest...
    </h3>
    <table>
        <tr>
            <td>
                <input type="text" name="nickname" placeholder="Enter an in-game nickname..." />
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="Join" />
            </td>
        </tr>
    </table>
</form>

<form class="box login" method="post" action="/auth/login">
    <h3>
        Login
    </h3>
    <table>
        <tr>
            <td>
                <input type="text" name="username" placeholder="Username..." />
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" name="password" placeholder="Password..." />
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="Login" />
            </td>
        </tr>
    </table>
</form>

<form class="box register" method="post" action="/auth/register">
    <h3>
        Register
    </h3>
    <table>
        <tr>
            <td>
                <input type="text" name="username" placeholder="Username..." />
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" name="password" placeholder="Password..." />
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" name="email" placeholder="E-mail..." />
            </td>
        </tr>
         <tr>
            <td>
                <input type="text" name="nickname" placeholder="Nickname..." />
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="Login" />
            </td>
        </tr>
    </table>
</form>

<form class="box user" method="post" action="/auth/user">
    <h3>
        Welcome username!
    </h3>
    <table>
        <tr>
            <td>
                <input type="submit" value="Join Now" />
            </td>
        </tr>
    </table>
</form>
