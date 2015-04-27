<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="subsections">
    <span>
        tags:
    </span>
    <ul>
        <li>
            <a href="/portfolio">all</a>
        </li>
        <c:forEach items="${portfolio_tags}" var="portfolio_tag">
            <li>
                <a href="/portfolio?tag=<c:out value="${portfolio_tag.name}"/>">
                    <c:out value="${portfolio_tag.name}"/>
                </a>
            </li>
        </c:forEach>
    </ul>
<div class="clear"></div>
</div>

<div class="portfolio">
    <c:forEach items="${portfolio_items}" var="portfolio_item">
        <p class="photo">
            <a href="<c:out value="${portfolio_item.getImageUrl()}"/>">
                <img src="<c:out value="${portfolio_item.getImageUrl()}"/>" />
            </a>
        </p>
    </c:forEach>
</div>
