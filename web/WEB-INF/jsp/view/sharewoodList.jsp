<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<c:url value="/" var="base" />	
 	<title>Fleetwood</title>
 	<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.1/css/bootstrap.min.css" />    
    <link rel="stylesheet" href="<c:url value="/resource/stylesheet/main.css" />" />    
</head>
<body>

	<br/><br/>
	<a href="<c:url value="/backHome"/>"><spring:message code="home" /></a><br /><br />

	<a href="<c:url value="/logout" />"><spring:message code="logout" /></a><br/><br/>
	
	<h1><spring:message code="title.photos.my" /></h1>
	
	<div class="container">
		
		<br />
			
		<c:forEach var="sharewoodPhoto" items="${photos}">
			<c:set var="shared" value="shared.${sharewoodPhoto.shared}" />
			<spring:message code="photo.number" />: <c:out value="${sharewoodPhoto.id}" /><br />
			<spring:message code="photo.title" />: <c:out value="${sharewoodPhoto.title}" /><br />
			<spring:message code="photo.shared" />: <spring:message code="${shared}" /><br />
			<img src="${base}sharewood/photosList/${sharewoodPhoto.id}" /><br /><br />
		</c:forEach>
		
			
	</div>
</body>
</html>