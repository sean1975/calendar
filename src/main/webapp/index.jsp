<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Sean's Calendar Analytics</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>

<c:set var="username" value="${requestScope['username']}" />
<c:set var="id" value="${requestScope['id']}" />
<body>

	<p>
		User: ${fn:escapeXml(username)} (<a href="/logout">Sign out</a>)
	</p>

    <p>
        Please select a calendar to be analyzed:
    </p>
    
    <form action="analytics">
    <c:forEach items="${requestScope['calendarList']}" var="calendar" varStatus="status">
        <input type="radio" name="cid" value="${calendar.id}" <c:if test="${status.first}">checked</c:if> ><c:out value="${calendar.summary} (${calendar.id})" /><br>
    </c:forEach>
        <input type="submit" value="Analyze">
    </form> 
</body>
</html>