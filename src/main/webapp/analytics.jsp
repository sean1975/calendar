<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sean's Calendar Analytics</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>

<!-- TODO: CSS, pie/bar charts -->
<body>

	<c:set var="calendarSummary"
		value="${requestScope['calendar'].summary}" />
	<c:set var="calendarTimeZone"
		value="${requestScope['calendar'].timeZone}" />

	<div id="title">
		<span>Calendar: <c:out value="${calendarSummary}" /></span> <br>
		<span>Time Zone: <c:out value="${calendarTimeZone}" /></span>
	</div>

	<div id='main'>
		<table>
			<tr>
				<th>Summary</th>
				<th>Occurrences</th>
				<th>Last Date</th>
			</tr>
			<c:forEach items="${requestScope['events']}" var="event">
				<tr>
					<td><c:out value="${event.summary}" /></td>
					<td><c:out value="${event.occurrence}" /></td>
					<td><fmt:formatDate value="${event.startTime}" type="date"
							timeZone="${calendarTimeZone}" /></td>
				</tr>
			</c:forEach>
		</table>
	</div>

</body>
</html>