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
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<%-- pop-up menus for selecting start and end dates --%>
<script>
    $.datepicker.setDefaults({
    	dateFormat: "yy-mm-dd"
    });
    $(function() {
        $( "#startdate" ).datepicker();
        $( "#enddate" ).datepicker();
    });
</script>
<%-- piechart for visualize event occurrence proportion --%>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(drawChart);
    function drawChart() {
        var data = google.visualization.arrayToDataTable([
            ['Summary', 'Occurrences'],
            <c:forEach items="${requestScope['events']}" var="event" varStatus="status">
                <c:choose>
                    <c:when test="${status.last}">
                        ['<c:out value="${event.summary}" />', <c:out value="${event.occurrence}" />]
                    </c:when>
                    <c:otherwise>
                        ['<c:out value="${event.summary}" />', <c:out value="${event.occurrence}" />],
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        ]);
        var chart = new google.visualization.PieChart(document.getElementById('piechart'));
        chart.draw(data);
    }
</script>
</head>

<body>

	<c:set var="calendarSummary"
		value="${requestScope['calendar'].summary}" />
	<c:set var="calendarTimeZone"
		value="${requestScope['calendar'].timeZone}" />
    <fmt:formatDate var="startDate" pattern="yyyy-MM-dd" timeZone="${calendarTimeZone}"
        value="${requestScope['calendar'].start}" />
    <fmt:formatDate var="endDate" pattern="yyyy-MM-dd" timeZone="${calendarTimeZone}"
        value="${requestScope['calendar'].end}" />

	<div id="title">
		<span>Calendar: <c:out value="${calendarSummary}" /></span> <br>
		<span>Time Zone: <c:out value="${calendarTimeZone}" /></span>
        <form action="analytics">
            <label for="start_date">Start Date:</label>
            <input type="text" id="startdate" name="start" value="${startDate}">
            <label for="end_date">End Date:</label>
            <input type="text" id="enddate" name="end" value="${endDate}">
            <input type="submit" value="Set">
        </form>
    </div>

	<div id='main'>
        <div id='piechart'></div>
        <div id='statistics'>
		<table>
			<tr>
			    <th class="index"></th>
				<th class="text">Summary</th>
				<th class="number">Occurrences</th>
				<th class="number">%&nbsp;Occurrences</th>
				<th class="date">Last Date</th>
			</tr>
			<c:forEach items="${requestScope['events']}" var="event" varStatus="status">
				<c:set var="odd_row" value="${status.count % 2}" />
			    <tr class="odd_row_${odd_row}">
				    <td class="index"><c:out value="${status.count}." /></td>
					<td class="text"><c:out value="${event.summary}" /></td>
					<td class="number"><c:out value="${event.occurrence}" /></td>
					<td class="number"><fmt:formatNumber value="${event.occurrencePercentage}" type="percent" maxFractionDigits="1" minFractionDigits="1" /></td>
					<td class="date"><fmt:formatDate value="${event.startTime}" type="date"
							timeZone="${calendarTimeZone}" /></td>
				</tr>
			</c:forEach>
		</table>
		</div>
	</div>

</body>
</html>