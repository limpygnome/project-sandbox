<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="email" value="shambles@kittenpile.com" />
<c:set var="tumblr_text" value="annie does art" />
<c:set var="tumblr_url" value="http://giricocola.tumblr.com" />
<c:set var="fb_text" value="click here" />
<c:set var="fb_url" value="https://www.facebook.com/annie.glover.9" />

<p>
    e-mail: <a href="mailto:<c:out value="${email}"/>"><c:out value="${email}"/></a>
</p>

<p>
    tumblr: <a href="<c:out value="${tumblr_url}"/>"><c:out value="${tumblr_text}"/></a>
</p>

<p>
    fb: <a href="<c:out value="${fb_url}"/>"><c:out value="${fb_text}"/></a>
</p>
