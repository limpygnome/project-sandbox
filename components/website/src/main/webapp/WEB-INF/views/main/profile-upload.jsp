<%@ taglib prefix="c"       uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"    uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ps"      tagdir="/WEB-INF/tags/main" %>

<h2>
    <a href="<spring:url value='/profile' />">Profile</a> - Upload
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
            <form:input type="file" path="fileUpload" />
        </p>
        <p class="center">
            <a href="/profile" class="button">
                Cancel
            </a>
            <input type="submit" value="Upload" />
        </p>


        <ps:csrf />

        <div class="messages">
            <ps:errorList modelAttribute="profilePictureUploadForm" cssClass="error" />

            <c:if test="${not empty profile_picture_success}">
                <div class="success">
                    Uploaded successfully
                </div>
            </c:if>
        </div>

    </form:form>
</div>
