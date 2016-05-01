<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content='width=device-width, initial-scale=1' name='viewport'/>
<title>Sean's Calendar Analytics</title>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<%@ include file="title_script.jsp"%>
<%-- column chart for visualize event occurrence proportion --%>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
    <c:set var="distribution" value="${requestScope['distribution']}" />
    <c:set var="eventList" value="${distribution.eventList}" />
    <c:set var="intervals" value="${distribution.intervals}" />
    <c:set var="distributionMap" value="${distribution.distributionMap}" />
    google.charts.load('current', {packages: ['corechart', 'bar']});
    google.charts.setOnLoadCallback(drawChart);
    function drawChart() {
    	var data = new google.visualization.DataTable();
    	data.addColumn('string', 'Interval');
    	<c:forEach items="${eventList}" var="event" varStatus="status">
    	data.addColumn('number', '<c:out value="${event}" />');
    	</c:forEach>
        data.addRows([
                      <c:forEach items="${intervals}" var="startDate" varStatus="status">
                      ['<c:out value="${startDate}" />'<c:forEach items="${eventList}">, 0</c:forEach>]<c:if test="${not status.last}">,</c:if>
                      </c:forEach>
                    ]);
        <c:set var="col" value="0" />
        <c:forEach items="${distributionMap}" var="entry">
            <c:set var="col" value="${col + 1}" />
            <c:forEach items="${entry.value }" var="occurrences" varStatus="occurrencesStatus">
        data.setValue(<c:out value="${occurrencesStatus.index}" />, <c:out value="${col}" />, <c:out value="${occurrences}" />);
            </c:forEach>
        </c:forEach>
        var options = {
                vAxis: {
                  title: 'Occurrences',
                  format: 'decimal',
                  viewWindow: {
                	  min: 0
                  }
                }
              };
        var chart = new google.visualization.ColumnChart(document.getElementById('columnchart'));
        chart.draw(data, options);
    }
</script>
<%-- sort statistics table by jQuery plugin tablesorter http://tablesorter.com/docs/ --%>
<link type="text/css" rel="stylesheet" href="/stylesheets/style.css" />
<script src="scripts/jquery.tablesorter.min.js" type="text/javascript"></script>
<script type="text/javascript">
    $(document).ready(function() {
	    $("#datatable").tablesorter({
	    	widgets: ["zebra"],
	        sortList: [[${fn:length(intervals) + 1}, 1]]
	    });
	}); 
</script>
</head>

<body>

    <%@ include file="title_body.jsp" %>
    
	<div class="main">
        <div class="chart" id="columnchart"></div>
        <div class="statistics">
		<table id="datatable" class="tablesorter">
		    <thead>
			<tr>
				<th>Summary</th>
				<c:forEach items="${intervals}" var="startDate" varStatus="status">
				<th><c:out value="${startDate}" /></th>
				</c:forEach>
				<th>Total</th>
			</tr>
			</thead>
			<tbody>
                <c:forEach items="${distributionMap}" var="entry">
			        <c:set var="subtotal" value="0" />
            <tr>
                <td><c:out value="${entry.key}" /></td>
                    <c:forEach items="${entry.value }" var="occurrences">
                <td><c:out value="${occurrences}" /></td>
                    <c:set var="subtotal" value="${occurrences + subtotal}" />
                    </c:forEach>
                <td><c:out value="${subtotal}" /></td>
            </tr>
                </c:forEach>
            </tbody>    
		</table>
		</div>
	</div>

</body>
</html>