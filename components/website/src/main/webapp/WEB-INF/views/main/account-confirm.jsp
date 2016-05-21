<%@ taglib prefix="tiles"	uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<h2>
    <a href="/account">Account</a> - Confirm Action
</h2>

<tiles:insertDefinition name="main/account_subpages/${subpage}" />

<form method="post" action="">

    <p class="center">
        <a href="/account" class="button">
            Cancel
        </a>

        <input type="submit" value="Confirm" class="confirm" />
        <input type="hidden" name="confirm" value="1" />
    </p>

    <ps:csrf />

</form>
