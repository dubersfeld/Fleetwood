<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Fleetwood Log In</title>
    </head>
    <body>
    	Language:<br/>
		<a href="?locale=fr_FR">Français</a><br/>
		<a href="?locale=en_US">English</a><br/>
		<br/>
        <c:if test="${param.containsKey('loginFailed')}">
            <b class="errors"><spring:message code="login.error" />.</b><br />
        </c:if><c:if test="${param.containsKey('loggedOut')}">
            <i><spring:message code="loggedOut" /></i><br /><br />
        </c:if>
        <spring:message code="login.ask" /><br /><br />
      
        <form:form method="post" modelAttribute="loginForm" autocomplete="off">
            <form:label path="username"><spring:message code="username" /></form:label><br />
            <form:input path="username" autocomplete="off" /><br />
            <form:label path="password"><spring:message code="password" /></form:label><br />
            <form:password path="password" autocomplete="off" /><br />
            <input type="submit" value="Log In" />
        </form:form>
    </body>
</html>
