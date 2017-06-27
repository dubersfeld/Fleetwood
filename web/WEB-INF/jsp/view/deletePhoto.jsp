<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>deletePhoto</title>
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.1/css/bootstrap.min.css" />    
    <link rel="stylesheet" href="<c:url value="/resource/stylesheet/main.css" />" />    
</head>
<body>

<h2><spring:message code="photo.id" /></h2>
<form:form method="POST" action="deletePhoto" modelAttribute="getPhoto">
   <table>
    <tr>
    	<td><form:label path="id"><spring:message code="photo.id" />:</form:label></td>
        <td><form:input path="id" /></td>
        <td><form:errors path="id" class="error" /></td>  
    </tr>	
    
    <tr>
        <td colspan="2">
            <input type="submit" value="valid"/>
        </td>
    </tr>
  </table>  
</form:form>
</body>
</html>