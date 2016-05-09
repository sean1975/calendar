<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content='width=device-width, initial-scale=1' name='viewport'/>
<title>Sean's Calendar Analytics</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>

<c:set var="username" value="${requestScope['username']}" />
<c:set var="id" value="${requestScope['id']}" />
<c:set var="error" value="${requestScope['error']}" />
<body>

<div class="title">
	<label>User:</label>${fn:escapeXml(username)} (<a href="/logout">Sign out</a>)
</div>
<div>
    <div>Please select a calendar to be analyzed:</div>
    <div>
    <form action="analytics">
    <c:forEach items="${requestScope['calendarList']}" var="calendar" varStatus="status">
        <input type="radio" name="cid" value="${calendar.id}" <c:if test="${status.first}">checked</c:if> ><c:out value="${calendar.summary}" /><br>
    </c:forEach>
        <div>Or manually input the calendar ID, such as <b>fm9qs8tn9kgh2sdc2vt5l3qbqg@group.calendar.google.com</b></div>
        <input type="radio" name="cid" id="cid_other" value=""><input type="text" id="cid_other_value" value="" onchange="manualInputCid(this.value);">
        <div class="err_msg"><c:out value=" ${error}" /></div><br>
        <input type="submit" value="Analyze">
    </form>
    </div>
</div>
<script type="text/javascript">
    function manualInputCid(val) {
    	var cid_other = document.getElementById("cid_other");
    	cid_other.value = val;
    	cid_other.checked = true;
    }
</script>
</body>
</html>