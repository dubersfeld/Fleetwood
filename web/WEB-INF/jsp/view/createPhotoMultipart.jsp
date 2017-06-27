<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>createPhotoMultipart</title>
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/2.3.1/css/bootstrap.min.css" />    
    <link rel="stylesheet" href="<c:url value="/resource/stylesheet/main.css" />" />    
</head>
<body>

<h2><spring:message code="photo.data" /></h2>
<form:form method="POST" enctype="multipart/form-data" action="createPhotoMulti" modelAttribute="photoMulti">
   <table>
    <tr>
    	<td><form:label path="uploadedFile"><spring:message code="photo.file" />:</form:label></td>
        <td><input type="file" name="uploadedFile" /></td>
        <td><form:errors path="uploadedFile" class="error"/></td>  
    </tr>
    <tr>
    	<td><form:label path="title"><spring:message code="photo.title" />:</form:label></td>
        <td><input type="text" name="title" /></td>
        <td><form:errors path="title" class="error"/></td>
    </tr>  
    <tr>
    	<td><form:label path="shared"><spring:message code="photo.shared" />:</form:label></td>
                <td><input type="radio" name="shared" value="true" /><spring:message code="shared.true" />  
        			<input type="radio" name="shared" value="false" checked /><spring:message code="shared.false" /></td>
        <td><form:errors path="shared" class="error"/></td>  
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