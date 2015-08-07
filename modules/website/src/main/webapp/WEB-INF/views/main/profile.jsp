<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<h2>
    <a href="/profile">Profile</a> - ${profile_user.nickname}
</h2>

<div class="profile">

    <table class="info">
        <tr>
            <td colspan="2" class="picture">
                <img src="/images/" alt="Profile picture" />

                <c:if test="${user != null && profile_user != null && profile_user == user}">
                    <div>
                        <a href="/profile/upload" class="button">
                            Change
                        </a>
                    </div>
                </c:if>

            </td>
        </tr>
        <tr>
            <th>
                Status:
            </th>
            <td class="status">
                <c:choose>
                    <c:when test="${online}">
                        <span class="online">
                            ONLINE
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="offline">
                            OFFLINE
                        </span>
                    </c:otherwise>
                </c:choose>
            </td<
        </tr>
        <tr>
            <th>
                Last Online:
            </th>
            <td>
                <c:choose>
                    <c:when test="${not empty game_session}">
                        <c:out value="${game_session.playerMetrics.lastUpdatedHuman}" />
                    </c:when>
                    <c:otherwise>
                        Never
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                Registered Since:
            </th>
            <td>
                <span title="<c:out value="${profile_user.registered.toString('YYYY-MM-dd HH:mm:ss')}" />">
                    <c:out value="${profile_user.registeredHuman}" />
                </span>
            </td>
        </tr>
        <tr>
            <th>
                Group:
            </th>
            <td>
                <c:out value="${profile_user.roles.primaryRole}" />
            </td>
        </tr>
    </table>
    <table class="info">
        <tr>
            <th class="section" colspan="2">
                Stats
            </th>
        </tr>
        <tr>
            <th>
                Score:
            </th>
            <td>
                <c:out value="${profile_user.playerMetrics.score}" />
            </td>
        </tr>
        <tr>
            <th>
                K/D Ratio:
            </th>
            <td>
                <c:choose>
                    <c:when test="${profile_user.playerMetrics.deaths == 0}">
                        <c:out value="${profile_user.playerMetrics.kills}" />
                    </c:when>
                    <c:otherwise>
                        <%--
                        <c:out value="${profile_user.playerMetrics.kills / ${profile_user.playerMetrics.deaths}" />
                        --%>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                Kills:
            </th>
            <td>
                <c:out value="${profile_user.playerMetrics.kills}" />
            </td>
        </tr>
        <tr>
            <th>
                Deaths:
            </th>
            <td>
                <c:out value="${profile_user.playerMetrics.deaths}" />
            </td>
        </tr>
    </table>

</div>
