<%@ taglib prefix="c"		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<h2>
    <a href="/profile">Profile</a> - Upload
</h2>

<div class="profile">
    <p>
        Upload a picture below to set it as your profile picture, this will be displayed on your profile and in-game.
    </o>
    <p class="center picture">
        <img src="<ps:profilePictureUrl userId='${user.userId}' />" alt="Current profile picture" />
    </p>
    <form:form method="post" commandName="profilePictureUploadForm" enctype="multipart/form-data">
        <p class="center">
            <input type="file" name="fileUpload" />
        </p>
        <p class="center">
            <input type="submit" value="Upload" />
        </p>

        <ps:csrf />
        <ps:errorList modelAttribute="guestForm" cssClass="error" />

    </form:form>
</div>
