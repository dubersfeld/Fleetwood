<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>updatePhoto</title>
    <c:url value="/" var="base" />	
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.1/css/bootstrap.min.css" />    
    <link rel="stylesheet" href="<c:url value="/resource/stylesheet/main.css" />" />    
</head>
<body>

  <h2>
  <spring:message code="photo.update.id">
  	<spring:argument value="${photo.id}" />
  </spring:message>
  </h2>
  
  
  <form:form method="POST" action="updatePhoto2" modelAttribute="photo">
  	<table>
  	<tr>
        <td><form:label path="id"><spring:message code="photo.number" />:</form:label></td>
        <td><form:input path="id" disabled="true"/></td>
    </tr>
    <tr>
        <td><form:label path="title"><spring:message code="photo.title" />:</form:label></td>
        <td><form:input path="title" disabled="false"/></td>
    </tr>
     <tr>
        <td><form:label path="username"><spring:message code="username" />:</form:label></td>
        <td><form:input path="username" disabled="true"/></td>
    </tr>
    <tr>
    	<td><form:label path="shared"><spring:message code="photo.shared" />:</form:label></td>
                <td><input type="radio" name="shared" value="true" /><spring:message code="shared.true" />  
        			<input type="radio" name="shared" value="false" checked /><spring:message code="shared.false" /></td>
        <td><form:errors path="shared" class="error"/></td>  
    </tr>	

    <tr>
        <td colspan="2">
            <input type="submit" value="valid" />
        </td>
    </tr>
</table>               
    <form:hidden path="id" />
    <form:hidden path="username" />
</form:form>
  
  
  <img src="${base}sharewood/photosList/${photo.id}" /><br /><br />
  


</body>
</html>