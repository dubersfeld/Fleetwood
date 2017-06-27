<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>deletePhotoFailure</title>
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.1/css/bootstrap.min.css" />    
    <link rel="stylesheet" href="<c:url value="/resource/stylesheet/main.css" />" />    
</head>

<body>

	<a href="<c:url value="/backHome"/>"><spring:message code="home" /></a><br /><br />

	<h1>
	<spring:message code="photo.delete.failure" />
	
	<spring:message code="${cause}" />
	</h1>
	

</body>
</html>