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
<meta content='width=device-width, initial-scale=1' name='viewport'/>
<title>Sean's Calendar Analytics</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<link type="text/css" rel="stylesheet" href="/stylesheets/analytics.css" />
<%@ include file="title_script.jsp"%>
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

    <%@ include file="title_body.jsp" %>
    
	<div class="main">
        <div class="chart" id="piechart"></div>
        <div class="statistics">
		<table>
			<tr>
			    <th class="index"></th>
				<th class="text">Summary</th>
				<th class="number"><div class="long_th">Occurrences</div><div class="short_th">&nbsp;#</div></th>
				<th class="number"><div>&nbsp;%</div></th>
				<th class="date">Last Date</th>
			</tr>
			<c:forEach items="${requestScope['events']}" var="event" varStatus="status">
			    <c:choose>
			        <c:when test="${status.first}">
			            <c:set var="summary" value="${event.summary}" />
			        </c:when>
			        <c:otherwise>
			            <c:set var="summary" value="${summary},${event.summary}" />
			        </c:otherwise>
			    </c:choose>
				<c:set var="odd_row" value="${status.count % 2}" />
			    <tr class="odd_row_${odd_row}">
				    <td class="index"><a href="analytics?cid=${calendarId}&start=${startDate}&end=${endDate}&summary=${summary}"><c:out value="${status.count}." /></a></td>
					<td class="text"><c:out value="${event.summary}" /></td>
					<td class="number"><c:out value="${event.occurrence}" /></td>
					<td class="number"><fmt:formatNumber value="${event.occurrencePercentage}" type="percent" maxFractionDigits="1" minFractionDigits="1" /></td>
					<td class="date"><fmt:formatDate value="${event.startTime}" type="date" dateStyle="short"
							timeZone="${calendarTimeZone}" /></td>
				</tr>
			</c:forEach>
		</table>
		</div>
	</div>

</body>
</html>