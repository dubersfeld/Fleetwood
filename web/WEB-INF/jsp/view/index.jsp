<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Fleetwood</title>
    </head>
    <body>
    	Language:<br/>
		<a href="?locale=fr_FR">Français</a><br/>
		<a href="?locale=en_US">English</a><br/>
		<br/>
    	<h1>
    	
    	<spring:message code="welcome" />
    	<c:if test="${!empty username}">
    		<c:out value="${username}" />
    	</c:if>
    	</h1>
    	
    	<a href="<c:url value="/logout" />"><spring:message code="logout" /></a><br/><br/>
 
 		<br /><br />
        <a href="<c:url value="/sharewood/createPhotoMulti" />"><spring:message code="photo.create" /></a>
    	
    	<br /><br />
        <a href="<c:url value="/sharewood/deletePhoto" />"><spring:message code="photo.delete" /></a>
    	
    	<br /><br />
        <a href="<c:url value="/sharewood/updatePhoto" />"><spring:message code="photo.update" /></a>
    	
        <br /><br />
		<a href="<c:url value="/sharewood/sharedPhotos"/>"><spring:message code="photos.shared" /></a>
		
    	<br /><br />
		<a href="<c:url value="/sharewood/photosMy"/>"><spring:message code="photos.my" /></a>
		
  
    </body>
</html>
